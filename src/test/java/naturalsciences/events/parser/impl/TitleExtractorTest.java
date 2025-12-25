package naturalsciences.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TitleExtractorTest {

    private TitleExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new TitleExtractor();
    }

    @Test
    void extractTitle_withH1_returnsTitle() {
        String html = "<html><body><div><h1>Science Cafe</h1></div></body></html>";
        Document doc = Jsoup.parse(html);
        Element eventCard = doc.selectFirst("div");

        String result = extractor.extractTitle(eventCard);

        assertThat(result).isEqualTo("Science Cafe");
    }

    @Test
    void extractTitle_withH2_returnsTitle() {
        String html = "<html><body><div><h2>Museum Event</h2></div></body></html>";
        Document doc = Jsoup.parse(html);
        Element eventCard = doc.selectFirst("div");

        String result = extractor.extractTitle(eventCard);

        assertThat(result).isEqualTo("Museum Event");
    }

    @Test
    void extractTitle_withH3_returnsTitle() {
        String html = "<html><body><div><h3>Nature Walk</h3></div></body></html>";
        Document doc = Jsoup.parse(html);
        Element eventCard = doc.selectFirst("div");

        String result = extractor.extractTitle(eventCard);

        assertThat(result).isEqualTo("Nature Walk");
    }

    @Test
    void extractTitle_withH1Whitespace_returnsTrimmedTitle() {
        String html = "<html><body><div><h1>  Event Title  </h1></div></body></html>";
        Document doc = Jsoup.parse(html);
        Element eventCard = doc.selectFirst("div");

        String result = extractor.extractTitle(eventCard);

        assertThat(result).isEqualTo("Event Title");
    }

    @Test
    void extractTitle_withTribeEventsTitle_returnsTitle() {
        String html = "<html><body><div><span class=\"tribe-events-single-event-title\">Tribe Event</span></div></body></html>";
        Document doc = Jsoup.parse(html);
        Element eventCard = doc.selectFirst("div");

        String result = extractor.extractTitle(eventCard);

        assertThat(result).isEqualTo("Tribe Event");
    }

    @Test
    void extractTitle_withEntryTitle_returnsTitle() {
        String html = "<html><body><div><span class=\"entry-title\">Entry Event</span></div></body></html>";
        Document doc = Jsoup.parse(html);
        Element eventCard = doc.selectFirst("div");

        String result = extractor.extractTitle(eventCard);

        assertThat(result).isEqualTo("Entry Event");
    }

    @Test
    void extractTitle_withMetaTag_returnsTitle() {
        String html = "<html><head><meta property=\"og:title\" content=\"Meta Title\"/></head><body><div></div></body></html>";
        Document doc = Jsoup.parse(html);
        Element eventCard = doc.selectFirst("div");

        String result = extractor.extractTitle(eventCard);

        assertThat(result).isEqualTo("Meta Title");
    }

    @Test
    void extractTitle_withH1Priority_ignoresOtherStrategies() {
        String html = "<html><head><meta property=\"og:title\" content=\"Meta Title\"/></head><body><div>"
                + "<h1>Heading Title</h1>"
                + "<span class=\"tribe-events-single-event-title\">Tribe Title</span>"
                + "</div></body></html>";
        Document doc = Jsoup.parse(html);
        Element eventCard = doc.selectFirst("div");

        String result = extractor.extractTitle(eventCard);

        assertThat(result).isEqualTo("Heading Title");
    }

    @Test
    void extractTitle_withBlankH1_usesTribeEventsTitle() {
        String html = "<html><body><div>"
                + "<h1>   </h1>"
                + "<span class=\"tribe-events-single-event-title\">Tribe Event</span>"
                + "</div></body></html>";
        Document doc = Jsoup.parse(html);
        Element eventCard = doc.selectFirst("div");

        String result = extractor.extractTitle(eventCard);

        assertThat(result).isEqualTo("Tribe Event");
    }

    @Test
    void extractTitle_withEmptyH1_usesEntryTitle() {
        String html = "<html><body><div>"
                + "<h1></h1>"
                + "<h2>   </h2>"
                + "<span class=\"tribe-events-single-event-title\">  </span>"
                + "<span class=\"entry-title\">Entry Title</span>"
                + "</div></body></html>";
        Document doc = Jsoup.parse(html);
        Element eventCard = doc.selectFirst("div");

        String result = extractor.extractTitle(eventCard);

        assertThat(result).isEqualTo("Entry Title");
    }

    @Test
    void extractTitle_withBlankClassSelectors_usesMetaTag() {
        String html = "<html><head><meta property=\"og:title\" content=\"Meta Title\"/></head><body><div>"
                + "<h1>  </h1>"
                + "<h2>   </h2>"
                + "<h3></h3>"
                + "<span class=\"tribe-events-single-event-title\">   </span>"
                + "<span class=\"entry-title\">  </span>"
                + "</div></body></html>";
        Document doc = Jsoup.parse(html);
        Element eventCard = doc.selectFirst("div");

        String result = extractor.extractTitle(eventCard);

        assertThat(result).isEqualTo("Meta Title");
    }

    @Test
    void extractTitle_withNoElements_returnsDefaultTitle() {
        String html = "<html><body><div><p>Some content</p></div></body></html>";
        Document doc = Jsoup.parse(html);
        Element eventCard = doc.selectFirst("div");

        String result = extractor.extractTitle(eventCard);

        assertThat(result).isEqualTo("Untitled Event");
    }

    @Test
    void extractTitle_withBlankMetaTag_returnsDefaultTitle() {
        String html = "<html><head><meta property=\"og:title\" content=\"   \"/></head><body><div>"
                + "<h1>  </h1>"
                + "<h2>   </h2>"
                + "<h3></h3>"
                + "<span class=\"tribe-events-single-event-title\">   </span>"
                + "<span class=\"entry-title\">  </span>"
                + "</div></body></html>";
        Document doc = Jsoup.parse(html);
        Element eventCard = doc.selectFirst("div");

        String result = extractor.extractTitle(eventCard);

        assertThat(result).isEqualTo("Untitled Event");
    }

    @Test
    void extractTitle_withMultipleHeadings_returnsFirstOne() {
        String html = "<html><body><div>"
                + "<h1>First Title</h1>"
                + "<h1>Second Title</h1>"
                + "</div></body></html>";
        Document doc = Jsoup.parse(html);
        Element eventCard = doc.selectFirst("div");

        String result = extractor.extractTitle(eventCard);

        assertThat(result).isEqualTo("First Title");
    }

    @Test
    void extractTitle_withNestedElements_returnsText() {
        String html = "<html><body><div><h1>Event <span>Title</span></h1></div></body></html>";
        Document doc = Jsoup.parse(html);
        Element eventCard = doc.selectFirst("div");

        String result = extractor.extractTitle(eventCard);

        assertThat(result).isEqualTo("Event Title");
    }

    @Test
    void extractTitle_withEmptyMetaAttribute_returnsDefaultTitle() {
        String html = "<html><head><meta property=\"og:title\" content=\"\"/></head><body><div>"
                + "<h1>  </h1>"
                + "</div></body></html>";
        Document doc = Jsoup.parse(html);
        Element eventCard = doc.selectFirst("div");

        String result = extractor.extractTitle(eventCard);

        assertThat(result).isEqualTo("Untitled Event");
    }
}
