package naturalsciences.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DescriptionExtractorTest {

    private DescriptionExtractor extractor;

    @Test
    void extractDescription_withMultipleStrategies_prefersSingleEvent() {
        String html = "<html><head>"
            + "<meta property=\"og:description\" content=\"Meta\" />"
            + "</head><body>"
            + "<div class=\"tribe-events-single-event-description\">Single event</div>"
            + "<div class=\"entry-content\">Entry</div>"
            + "</body></html>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Single event");
    }

    @Test
    void extractDescription_withSingleEvent_returnsText() {
        String html = "<div>"
            + "<div class=\"tribe-events-single-event-description\">Science cafe description</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Science cafe description");
    }

    @Test
    void extract_withEntryContent_returnsText() {
        String html = "<div>"
            + "<div class=\"entry-content\">Entry text here</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Entry text here");
    }

    @Test
    void extract_withExactly500Chars_doesNotTruncate() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            text.append("b");
        }
        String html = "<div>"
            + "<div class=\"tribe-events-content\">" + text + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).hasSize(500);
        assertThat(result).doesNotEndWith("...");
    }

    @Test
    void extract_withLongText_truncatesAt500Chars() {
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 600; i++) {
            longText.append("a");
        }
        String html = "<div>"
            + "<div class=\"tribe-events-content\">" + longText + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).hasSize(503); // 500 + "..."
        assertThat(result).endsWith("...");
    }

    @Test
    void extract_withMetaTag_returnsMetaContent() {
        String html = "<html><head>"
            + "<meta property=\"og:description\" content=\"Meta description\" />"
            + "</head><body></body></html>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Meta description");
    }

    @Test
    void extract_withMultipleParagraphs_joinsWithSpace() {
        String html = "<div>"
            + "<div class=\"tribe-events-content\">"
            + "<p>First paragraph</p>"
            + "<p>Second paragraph</p>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("First paragraph Second paragraph");
    }

    @Test
    void extract_withNoMatch_returnsEmptyString() {
        String html = "<div><p>Some content</p></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withTribeEventsContent_returnsText() {
        String html = "<div>"
            + "<div class=\"tribe-events-content\">Event content here</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Event content here");
    }

    @BeforeEach
    void setUp() {
        extractor = new DescriptionExtractor();
    }
}
