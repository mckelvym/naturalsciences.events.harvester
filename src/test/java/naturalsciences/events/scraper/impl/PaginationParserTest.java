package naturalsciences.events.scraper.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaginationParserTest {

    private PaginationParser parser;

    @Test
    void getTotalPages_withAdditionalQueryParameters_extractsPageNumber() {
        String html = "<html><body>"
            + "<ul class=\"tribe-events-sub-nav\">"
            + "<a href=\"https://naturalsciences"
            + ".org/calendar/events/?foo=bar&tribe_paged=5&baz=qux\">5</a>"
            + "</ul>"
            + "</body></html>";
        Document doc = Jsoup.parse(html);

        int result = parser.getTotalPages(doc);

        assertThat(result).isEqualTo(5);
    }

    @Test
    void getTotalPages_withEmptyDocument_returnsOne() {
        String html = "<html><body></body></html>";
        Document doc = Jsoup.parse(html);

        int result = parser.getTotalPages(doc);

        assertThat(result).isEqualTo(1);
    }

    @Test
    void getTotalPages_withLinksMissingHrefAttribute_ignoresThem() {
        String html = "<html><body>"
            + "<ul class=\"tribe-events-sub-nav\">"
            + "<a>No href</a>"
            + "<a href=\"https://naturalsciences.org/calendar/events/?tribe_paged=3\">3</a>"
            + "</ul>"
            + "</body></html>";
        Document doc = Jsoup.parse(html);

        int result = parser.getTotalPages(doc);

        assertThat(result).isEqualTo(3);
    }

    @Test
    void getTotalPages_withLinksOutsideNavList_ignoresThem() {
        String html = "<html><body>"
            + "<a href=\"https://naturalsciences.org/calendar/events/?tribe_paged=5\">Outside "
            + "Nav</a>"
            + "<ul class=\"tribe-events-sub-nav\">"
            + "<a href=\"https://naturalsciences.org/calendar/events/?tribe_paged=2\">2</a>"
            + "</ul>"
            + "</body></html>";
        Document doc = Jsoup.parse(html);

        int result = parser.getTotalPages(doc);

        assertThat(result).isEqualTo(2);
    }

    @Test
    void getTotalPages_withMalformedPageNumber_returnsOne() {
        String html = "<html><body>"
            + "<ul class=\"tribe-events-sub-nav\">"
            + "<a href=\"https://naturalsciences.org/calendar/events/?tribe_paged=abc\">Invalid</a>"
            + "</ul>"
            + "</body></html>";
        Document doc = Jsoup.parse(html);

        int result = parser.getTotalPages(doc);

        assertThat(result).isEqualTo(1);
    }

    @Test
    void getTotalPages_withMultiplePaginationLinks_returnsMaxPageNumber() {
        String html = "<html><body>"
            + "<ul class=\"tribe-events-sub-nav\">"
            + "<a href=\"https://naturalsciences.org/calendar/events/?tribe_paged=2\">2</a>"
            + "<a href=\"https://naturalsciences.org/calendar/events/?tribe_paged=3\">3</a>"
            + "<a href=\"https://naturalsciences.org/calendar/events/?tribe_paged=4\">4</a>"
            + "</ul>"
            + "</body></html>";
        Document doc = Jsoup.parse(html);

        int result = parser.getTotalPages(doc);

        assertThat(result).isEqualTo(4);
    }

    @Test
    void getTotalPages_withNoTribePagedParam_returnsOne() {
        String html = "<html><body>"
            + "<ul class=\"tribe-events-sub-nav\">"
            + "<a href=\"https://naturalsciences.org/calendar/events/\">Events</a>"
            + "</ul>"
            + "</body></html>";
        Document doc = Jsoup.parse(html);

        int result = parser.getTotalPages(doc);

        assertThat(result).isEqualTo(1);
    }

    @Test
    void getTotalPages_withNonSequentialPages_returnsMaxPageNumber() {
        String html = "<html><body>"
            + "<ul class=\"tribe-events-sub-nav\">"
            + "<a href=\"https://naturalsciences.org/calendar/events/?tribe_paged=2\">2</a>"
            + "<a href=\"https://naturalsciences.org/calendar/events/?tribe_paged=7\">7</a>"
            + "<a href=\"https://naturalsciences.org/calendar/events/?tribe_paged=3\">3</a>"
            + "</ul>"
            + "</body></html>";
        Document doc = Jsoup.parse(html);

        int result = parser.getTotalPages(doc);

        assertThat(result).isEqualTo(7);
    }

    @Test
    void getTotalPages_withPageOneExplicit_returnsOne() {
        String html = "<html><body>"
            + "<ul class=\"tribe-events-sub-nav\">"
            + "<a href=\"https://naturalsciences.org/calendar/events/?tribe_paged=1\">1</a>"
            + "</ul>"
            + "</body></html>";
        Document doc = Jsoup.parse(html);

        int result = parser.getTotalPages(doc);

        assertThat(result).isEqualTo(1);
    }

    @Test
    void getTotalPages_withSinglePage_returnsOne() {
        String html = "<html><body><div>Single page content</div></body></html>";
        Document doc = Jsoup.parse(html);

        int result = parser.getTotalPages(doc);

        assertThat(result).isEqualTo(1);
    }

    @Test
    void getTotalPages_withTrailingAmpersand_extractsPageNumber() {
        String html = "<html><body>"
            + "<ul class=\"tribe-events-sub-nav\">"
            + "<a href=\"https://naturalsciences.org/calendar/events/?tribe_paged=4&\">4</a>"
            + "</ul>"
            + "</body></html>";
        Document doc = Jsoup.parse(html);

        int result = parser.getTotalPages(doc);

        assertThat(result).isEqualTo(4);
    }

    @BeforeEach
    void setUp() {
        parser = new PaginationParser();
    }
}
