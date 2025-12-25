package naturalsciences.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventCardFinderTest {

    private EventCardFinder finder;

    @Test
    void findEventCard_prioritizesArticleOverDivId() {
        String html = "<body>"
            + "<div id='tribe-events-content'>"
            + "<h1>Div Content</h1>"
            + "</div>"
            + "<article class='tribe_events'>"
            + "<h1>Article Content</h1>"
            + "</article>"
            + "</body>";
        Document doc = Jsoup.parse(html);

        Element card = finder.findEventCard(doc);

        assertThat(card.tagName()).isEqualTo("article");
        assertThat(card.className()).contains("tribe_events");
    }

    @Test
    void findEventCard_prioritizesTribeSingleOverArticle() {
        String html = "<body>"
            + "<article class='tribe_events'>"
            + "<h1>Article Content</h1>"
            + "</article>"
            + "<div class='tribe-events-single'>"
            + "<h1>Single Event Content</h1>"
            + "</div>"
            + "</body>";
        Document doc = Jsoup.parse(html);

        Element card = finder.findEventCard(doc);

        assertThat(card.className()).contains("tribe-events-single");
    }

    @Test
    void findEventCard_withComplexNestedStructure() {
        String html = "<body>"
            + "<div class='page-wrapper'>"
            + "<div class='content-area'>"
            + "<article class='tribe_events'>"
            + "<div class='entry-content'>"
            + "<h1>Event Title</h1>"
            + "<p>Description</p>"
            + "</div>"
            + "</article>"
            + "</div>"
            + "</div>"
            + "</body>";
        Document doc = Jsoup.parse(html);

        Element card = finder.findEventCard(doc);

        assertThat(card.tagName()).isEqualTo("article");
        assertThat(card.className()).contains("tribe_events");
    }

    @Test
    void findEventCard_withEmptyDocument_returnsBody() {
        Document doc = Jsoup.parse("");

        Element card = finder.findEventCard(doc);

        assertThat(card.tagName()).isEqualTo("body");
    }

    @Test
    void findEventCard_withMixedClassAndId_prioritizesClass() {
        String html = "<body>"
            + "<div id='tribe-events-content'>"
            + "<h1>ID Content</h1>"
            + "</div>"
            + "<div class='tribe-events-single'>"
            + "<h1>Class Content</h1>"
            + "</div>"
            + "</body>";
        Document doc = Jsoup.parse(html);

        Element card = finder.findEventCard(doc);

        assertThat(card.className()).contains("tribe-events-single");
    }

    @Test
    void findEventCard_withMultipleArticles_findsFirstTribeEvents() {
        String html = "<body>"
            + "<article class='regular-article'>"
            + "<h1>Regular Article</h1>"
            + "</article>"
            + "<article class='tribe_events'>"
            + "<h1>Event Article</h1>"
            + "</article>"
            + "</body>";
        Document doc = Jsoup.parse(html);

        Element card = finder.findEventCard(doc);

        assertThat(card.className()).contains("tribe_events");
    }

    @Test
    void findEventCard_withMultipleTribeSingleDivs_findsFirst() {
        String html = "<body>"
            + "<div class='tribe-events-single' id='first'>"
            + "<h1>First Event</h1>"
            + "</div>"
            + "<div class='tribe-events-single' id='second'>"
            + "<h1>Second Event</h1>"
            + "</div>"
            + "</body>";
        Document doc = Jsoup.parse(html);

        Element card = finder.findEventCard(doc);

        assertThat(card.id()).isEqualTo("first");
    }

    @Test
    void findEventCard_withNestedTribeSingle_findsOuterContainer() {
        String html = "<div class='tribe-events-single'>"
            + "<div class='header'>"
            + "<h1>Event Title</h1>"
            + "</div>"
            + "<div class='content'>"
            + "<p>Event description</p>"
            + "</div>"
            + "</div>";
        Document doc = Jsoup.parse(html);

        Element card = finder.findEventCard(doc);

        assertThat(card.className()).contains("tribe-events-single");
    }

    @Test
    void findEventCard_withNoMatchingSelectors_returnsBody() {
        String html = "<div class='container'>"
            + "<p>Regular content</p>"
            + "</div>";
        Document doc = Jsoup.parse(html);

        Element card = finder.findEventCard(doc);

        assertThat(card.tagName()).isEqualTo("body");
    }

    @Test
    void findEventCard_withOnlyBodyContent_returnsBody() {
        String html = "<p>Simple paragraph</p>";
        Document doc = Jsoup.parse(html);

        Element card = finder.findEventCard(doc);

        assertThat(card.tagName()).isEqualTo("body");
    }

    @Test
    void findEventCard_withPartialIdMatch_findsContainer() {
        String html = "<div id='tribe-events-pg-template'>"
            + "<h1>Event Content</h1>"
            + "</div>";
        Document doc = Jsoup.parse(html);

        Element card = finder.findEventCard(doc);

        assertThat(card.id()).contains("tribe-events");
    }

    @Test
    void findEventCard_withRealWorldStructure() {
        String html = "<div class='tribe-events-single'>"
            + "<h1 class='tribe-events-single-event-title'>Science Café: Climate Change</h1>"
            + "<div class='tribe-events-schedule'>"
            + "<span class='tribe-event-date-start'>June 15, 2026</span>"
            + "</div>"
            + "<div class='tribe-events-single-event-description'>"
            + "<p>Join us for an engaging discussion about climate science.</p>"
            + "</div>"
            + "<img class='tribe-events-event-image' src='event.jpg'/>"
            + "</div>";
        Document doc = Jsoup.parse(html);

        Element card = finder.findEventCard(doc);

        assertThat(card.className()).contains("tribe-events-single");
        assertThat(card.select("h1").text()).contains("Science Café");
    }

    @Test
    void findEventCard_withTribeEventsArticle_findsContainer() {
        String html = "<article class='tribe_events'>"
            + "<h1>Event Title</h1>"
            + "<p>Event description</p>"
            + "</article>";
        Document doc = Jsoup.parse(html);

        Element card = finder.findEventCard(doc);

        assertThat(card.tagName()).isEqualTo("article");
        assertThat(card.className()).contains("tribe_events");
    }

    @Test
    void findEventCard_withTribeEventsIdDiv_findsContainer() {
        String html = "<div id='tribe-events-content'>"
            + "<h1>Event Title</h1>"
            + "<p>Event description</p>"
            + "</div>";
        Document doc = Jsoup.parse(html);

        Element card = finder.findEventCard(doc);

        assertThat(card.id()).contains("tribe-events");
    }

    @Test
    void findEventCard_withTribeSingleDiv_findsContainer() {
        String html = "<div class='tribe-events-single'>"
            + "<h1>Event Title</h1>"
            + "<p>Event description</p>"
            + "</div>";
        Document doc = Jsoup.parse(html);

        Element card = finder.findEventCard(doc);

        assertThat(card.className()).contains("tribe-events-single");
    }

    @BeforeEach
    void setUp() {
        finder = new EventCardFinder();
    }
}
