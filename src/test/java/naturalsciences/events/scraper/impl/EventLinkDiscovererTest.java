package naturalsciences.events.scraper.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventLinkDiscovererTest {

    private EventLinkDiscoverer discoverer;

    @Test
    void discoverEventLinks_withBlankHref_filtersThemOut() {
        String html = "<html><body>"
            + "<h2 class=\"tribe-events-list-event-title\">"
            + "<a class=\"url\" href=\"   \">Blank</a>"
            + "</h2>"
            + "<h2 class=\"tribe-events-list-event-title\">"
            + "<a class=\"url\" href=\"/calendar/event/valid/\">Valid</a>"
            + "</h2>"
            + "</body></html>";
        Document doc = Jsoup.parse(html, "https://naturalsciences.org");

        List<String> result = discoverer.discoverEventLinks(doc);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).contains("valid");
    }

    @Test
    void discoverEventLinks_withEmptyDocument_returnsEmptyList() {
        String html = "<html><body></body></html>";
        Document doc = Jsoup.parse(html);

        List<String> result = discoverer.discoverEventLinks(doc);

        assertThat(result).isEmpty();
    }

    @Test
    void discoverEventLinks_withEmptyHref_filtersThemOut() {
        String html = "<html><body>"
            + "<h2 class=\"tribe-events-list-event-title\">"
            + "<a class=\"url\" href=\"\">Empty</a>"
            + "</h2>"
            + "<h2 class=\"tribe-events-list-event-title\">"
            + "<a class=\"url\" href=\"/calendar/event/valid/\">Valid</a>"
            + "</h2>"
            + "</body></html>";
        Document doc = Jsoup.parse(html, "https://naturalsciences.org");

        List<String> result = discoverer.discoverEventLinks(doc);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).contains("valid");
    }

    @Test
    void discoverEventLinks_withInvalidEventUrls_filtersThemOut() {
        String html = "<html><body>"
            + "<h2 class=\"tribe-events-list-event-title\">"
            + "<a class=\"url\" href=\"/calendar/event/valid/\">Valid</a>"
            + "</h2>"
            + "<h2 class=\"tribe-events-list-event-title\">"
            + "<a class=\"url\" href=\"/about/info\">Invalid - wrong path</a>"
            + "</h2>"
            + "<h2 class=\"tribe-events-list-event-title\">"
            + "<a class=\"url\" href=\"https://other.com/calendar/event/other/\">Invalid - wrong "
            + "domain</a>"
            + "</h2>"
            + "</body></html>";
        Document doc = Jsoup.parse(html, "https://naturalsciences.org");

        List<String> result = discoverer.discoverEventLinks(doc);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).contains("valid");
    }

    @Test
    void discoverEventLinks_withLinksNotInH2_ignoresThem() {
        String html = "<html><body>"
            + "<h2 class=\"tribe-events-list-event-title\">"
            + "<a class=\"url\" href=\"/calendar/event/in-h2/\">In H2</a>"
            + "</h2>"
            + "<div>"
            + "<a class=\"url\" href=\"/calendar/event/not-in-h2/\">Not in H2</a>"
            + "</div>"
            + "</body></html>";
        Document doc = Jsoup.parse(html, "https://naturalsciences.org");

        List<String> result = discoverer.discoverEventLinks(doc);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).contains("in-h2");
    }

    @Test
    void discoverEventLinks_withLinksWithoutUrlClass_ignoresThem() {
        String html = "<html><body>"
            + "<h2 class=\"tribe-events-list-event-title\">"
            + "<a class=\"url\" href=\"/calendar/event/with-class/\">With url class</a>"
            + "</h2>"
            + "<h2 class=\"tribe-events-list-event-title\">"
            + "<a href=\"/calendar/event/no-class/\">No url class</a>"
            + "</h2>"
            + "</body></html>";
        Document doc = Jsoup.parse(html, "https://naturalsciences.org");

        List<String> result = discoverer.discoverEventLinks(doc);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).contains("with-class");
    }

    @Test
    void discoverEventLinks_withMultipleEvents_returnsAllUrls() {
        String html = "<html><body>"
            + "<h2 class=\"tribe-events-list-event-title\">"
            + "<a class=\"url\" href=\"/calendar/event/event-one/\">Event 1</a>"
            + "</h2>"
            + "<h2 class=\"tribe-events-list-event-title\">"
            + "<a class=\"url\" href=\"/calendar/event/event-two/\">Event 2</a>"
            + "</h2>"
            + "<h2 class=\"tribe-events-list-event-title\">"
            + "<a class=\"url\" href=\"/calendar/event/event-three/\">Event 3</a>"
            + "</h2>"
            + "</body></html>";
        Document doc = Jsoup.parse(html, "https://naturalsciences.org");

        List<String> result = discoverer.discoverEventLinks(doc);

        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(
            "https://naturalsciences.org/calendar/event/event-one/",
            "https://naturalsciences.org/calendar/event/event-two/",
            "https://naturalsciences.org/calendar/event/event-three/"
        );
    }

    @Test
    void discoverEventLinks_withWwwHost_acceptsThem() {
        String html = "<html><body>"
            + "<h2 class=\"tribe-events-list-event-title entry-title summary\">"
            + "<a class=\"url\" href=\"https://www.naturalsciences.org/calendar/event/"
            + "whiteville-teen-science-cafe-burning-for-quail/\">Teen Science Cafe</a>"
            + "</h2>"
            + "</body></html>";
        Document doc = Jsoup.parse(html, "https://naturalsciences.org");

        List<String> result = discoverer.discoverEventLinks(doc);

        assertThat(result).containsExactly("https://www.naturalsciences.org/calendar/event/"
            + "whiteville-teen-science-cafe-burning-for-quail/");
    }

    @Test
    void discoverEventLinks_withNoEvents_returnsEmptyList() {
        String html = "<html><body>"
            + "<h2>Regular Heading</h2>"
            + "<a href=\"/about/\">About</a>"
            + "</body></html>";
        Document doc = Jsoup.parse(html, "https://naturalsciences.org");

        List<String> result = discoverer.discoverEventLinks(doc);

        assertThat(result).isEmpty();
    }

    @Test
    void discoverEventLinks_withRelativeUrls_convertsToAbsolute() {
        String html = "<html><body>"
            + "<h2 class=\"tribe-events-list-event-title\">"
            + "<a class=\"url\" href=\"/calendar/event/local-event/\">Local Event</a>"
            + "</h2>"
            + "</body></html>";
        Document doc = Jsoup.parse(html, "https://naturalsciences.org");

        List<String> result = discoverer.discoverEventLinks(doc);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo("https://naturalsciences"
            + ".org/calendar/event/local-event/");
    }

    @Test
    void discoverEventLinks_withSingleValidEvent_returnsOneUrl() {
        String html = "<html><body>"
            + "<h2 class=\"tribe-events-list-event-title\">"
            + "<a class=\"url\" href=\"/calendar/event/science-cafe/\">Science Cafe</a>"
            + "</h2>"
            + "</body></html>";
        Document doc = Jsoup.parse(html, "https://naturalsciences.org");

        List<String> result = discoverer.discoverEventLinks(doc);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo("https://naturalsciences"
            + ".org/calendar/event/science-cafe/");
    }

    @Test
    void discoverEventLinks_withTrailingSlashVariations_acceptsBoth() {
        String html = "<html><body>"
            + "<h2 class=\"tribe-events-list-event-title\">"
            + "<a class=\"url\" href=\"/calendar/event/with-slash/\">With slash</a>"
            + "</h2>"
            + "<h2 class=\"tribe-events-list-event-title\">"
            + "<a class=\"url\" href=\"/calendar/event/without-slash\">Without slash</a>"
            + "</h2>"
            + "</body></html>";
        Document doc = Jsoup.parse(html, "https://naturalsciences.org");

        List<String> result = discoverer.discoverEventLinks(doc);

        assertThat(result).hasSize(2);
    }

    @BeforeEach
    void setUp() {
        discoverer = new EventLinkDiscoverer();
    }
}
