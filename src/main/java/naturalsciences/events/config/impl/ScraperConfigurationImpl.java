package naturalsciences.events.config.impl;

import static naturalsciences.events.parser.impl.CssSelectors.PAGE_LOAD_SELECTOR;

import java.time.Duration;
import naturalsciences.events.config.ScraperConfiguration;

/**
 * Configuration implementation for Natural Sciences Museum event scraping.
 * Provides site-specific parameters for naturalsciences.org.
 */
public class ScraperConfigurationImpl implements ScraperConfiguration {

    private static final String BASE_URL =
            "https://naturalsciences.org/calendar/events/"
                    + "?tribe_event_display=list&tribe-bar-search=science%20cafe";
    private static final String FEED_DESCRIPTION =
            "Science Café events from the North Carolina Museum of Natural Sciences";
    private static final String FEED_LINK = "https://naturalsciences.org/calendar/events/";
    private static final String FEED_TITLE = "Natural Sciences Museum Science Café Events";
    private static final Duration PAGE_LOAD_TIMEOUT = Duration.ofSeconds(10);
    private static final int RETENTION_DAYS = 7;
    private static final String USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) "
                    + "AppleWebKit/537.36 (KHTML, like Gecko) "
                    + "Chrome/120.0.0.0 Safari/537.36";

    @Override
    public String getBaseUrl() {
        return BASE_URL;
    }

    @Override
    public String getFeedDescription() {
        return FEED_DESCRIPTION;
    }

    @Override
    public String getFeedLink() {
        return FEED_LINK;
    }

    @Override
    public String getFeedTitle() {
        return FEED_TITLE;
    }

    @Override
    public String getPageLoadSelector() {
        return PAGE_LOAD_SELECTOR;
    }

    @Override
    public Duration getPageLoadTimeout() {
        return PAGE_LOAD_TIMEOUT;
    }

    @Override
    public int getRetentionDays() {
        return RETENTION_DAYS;
    }

    @Override
    public String getUrlForPage(final int pageNumber) {
        if (pageNumber <= 1) {
            return BASE_URL;
        }
        return BASE_URL + "&tribe_paged=" + pageNumber;
    }

    @Override
    public String getUserAgent() {
        return USER_AGENT;
    }
}
