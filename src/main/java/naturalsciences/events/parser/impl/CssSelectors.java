package naturalsciences.events.parser.impl;

/**
 * Constants for CSS selectors used in HTML parsing.
 *
 * <p>This class centralizes all CSS selector strings used throughout the
 * parser implementation to avoid magic strings and improve maintainability.
 */
public final class CssSelectors {

    // Event card container selectors
    public static final String EVENT_CARD_TRIBE_SINGLE = "div.tribe-events-single";
    public static final String EVENT_CARD_ARTICLE = "article.tribe_events";
    public static final String EVENT_CARD_DIV_ID = "div[id*=tribe-events]";

    // Title selectors
    public static final String TITLE_CLASS_SINGLE_EVENT = "tribe-events-single-event-title";
    public static final String TITLE_CLASS_ENTRY = "entry-title";
    public static final String META_TITLE = "meta[property=og:title]";

    // Date and time selectors
    public static final String DATE_SPAN_START = "span.tribe-event-date-start";
    public static final String DATE_TIME_DATETIME = "time[datetime]";
    public static final String DATE_ABBR = "abbr.tribe-events-abbr";
    public static final String TIME_DIV_START = "div.tribe-events-start-time";

    // Description selectors
    public static final String DESC_CLASS_SINGLE_EVENT = "tribe-events-single-event-description";
    public static final String DESC_CLASS_CONTENT = "tribe-events-content";
    public static final String DESC_CLASS_ENTRY = "entry-content";
    public static final String DESC_PARAGRAPH = "p";
    public static final String META_DESCRIPTION = "meta[property=og:description]";

    // Image selectors
    public static final String IMAGE_CLASS_EVENT = "img.tribe-events-event-image";
    public static final String IMAGE_SRC = "img[src]";
    public static final String META_IMAGE = "meta[property=og:image]";

    // Pagination selectors (scraper package)
    public static final String PAGINATION_NAV_LINKS = "ul.tribe-events-sub-nav a[href]";

    // Event link discovery selectors (scraper package)
    public static final String EVENT_LINK_TITLE = "h2.tribe-events-list-event-title a.url";

    // Page loading selectors
    public static final String PAGE_LOAD_SELECTOR = "div.tribe-events-loop";

    /** Selector for event detail page content. */
    public static final String EVENT_DETAIL_PAGE_SELECTOR =
            "div.tribe-events-single, article.tribe_events, main#tribe-events-pg-template";

    private CssSelectors() {
        // Utility class - prevent instantiation
    }
}
