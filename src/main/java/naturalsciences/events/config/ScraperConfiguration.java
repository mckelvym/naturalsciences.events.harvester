package naturalsciences.events.config;

import java.time.Duration;

/**
 * Configuration interface for event scraping operations.
 * Provides site-specific parameters and settings.
 */
public interface ScraperConfiguration {

    /**
     * Returns the base URL for event listings.
     *
     * @return base URL string
     */
    String getBaseUrl();

    /**
     * Returns the RSS feed description.
     *
     * @return feed description
     */
    String getFeedDescription();

    /**
     * Returns the RSS feed link.
     *
     * @return feed link URL
     */
    String getFeedLink();

    /**
     * Returns the RSS feed title.
     *
     * @return feed title
     */
    String getFeedTitle();

    /**
     * Gets the timeout duration for page loads.
     *
     * @return the timeout duration
     */
    Duration getPageLoadTimeout();

    /**
     * Returns the number of days to retain events in RSS feed.
     *
     * @return number of days to keep events
     */
    int getRetentionDays();

    /**
     * Returns the user agent string for HTTP requests.
     *
     * @return user agent string
     */
    String getUserAgent();

    /**
     * Returns the CSS selector used to confirm page load.
     *
     * @return CSS selector string
     */
    String getPageLoadSelector();

    /**
     * Returns the URL pattern with page number placeholder.
     *
     * @param pageNumber the page number to fetch
     * @return formatted URL for the specified page
     */
    String getUrlForPage(int pageNumber);
}
