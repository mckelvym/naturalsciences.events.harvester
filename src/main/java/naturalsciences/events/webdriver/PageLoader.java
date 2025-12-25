package naturalsciences.events.webdriver;

import static java.util.Objects.requireNonNull;
import static naturalsciences.events.parser.impl.CssSelectors.EVENT_DETAIL_PAGE_SELECTOR;

import java.time.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads web pages using Selenium WebDriver and converts to JSoup documents.
 * Handles waiting for JavaScript-rendered content.
 */
public record PageLoader(WebDriver driver, Duration timeout) {

    private static final Logger LOG = LoggerFactory.getLogger(PageLoader.class);

    /**
     * Creates a new PageLoader.
     *
     * @param driver  the WebDriver to use
     * @param timeout the page load timeout
     */
    public PageLoader(final WebDriver driver, final Duration timeout) {
        this.driver = requireNonNull(driver);
        this.timeout = requireNonNull(timeout);
    }

    /**
     * Determines the appropriate CSS selector based on the page type.
     *
     * @param url      the URL being loaded
     * @param selector the default selector to use
     * @return CSS selector to wait for
     */
    private String determinePageLoadSelector(final String url,
                                             final String selector) {
        if (isEventDetailPage(url)) {
            return EVENT_DETAIL_PAGE_SELECTOR;
        }
        return selector;
    }

    /**
     * Checks if the URL is an event detail page.
     *
     * @param url the URL to check
     * @return true if this is an event detail page
     */
    private boolean isEventDetailPage(final String url) {
        return url != null && url.contains("/calendar/event/");
    }

    /**
     * Loads a page and returns it as a JSoup Document.
     *
     * @param url      the URL to load
     * @param selector the CSS selector to wait for (used for non-detail pages)
     * @return JSoup Document containing the page HTML
     */
    public Document loadPage(final String url, final String selector) {
        requireNonNull(url, "url must not be null");
        requireNonNull(selector, "selector must not be null");
        LOG.info("Loading page: {}", url);
        driver.get(url);

        final String selectorToUse = determinePageLoadSelector(url, selector);
        final WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(selectorToUse)));

        final String pageSource = driver.getPageSource();
        return Jsoup.parse(requireNonNull(pageSource));
    }
}
