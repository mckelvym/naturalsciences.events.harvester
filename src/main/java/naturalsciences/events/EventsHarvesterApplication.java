package naturalsciences.events;

import java.util.List;
import java.util.Set;
import naturalsciences.events.config.ScraperConfiguration;
import naturalsciences.events.config.impl.ScraperConfigurationImpl;
import naturalsciences.events.domain.EventItem;
import naturalsciences.events.feed.RssFeedManager;
import naturalsciences.events.feed.RssFeedManagerImpl;
import naturalsciences.events.parser.EventParser;
import naturalsciences.events.parser.impl.EventParserImpl;
import naturalsciences.events.scraper.EventScraper;
import naturalsciences.events.scraper.impl.EventLinkDiscoverer;
import naturalsciences.events.scraper.impl.EventScraperImpl;
import naturalsciences.events.scraper.impl.PaginationParser;
import naturalsciences.events.webdriver.ChromeDriverManager;
import naturalsciences.events.webdriver.PageLoader;
import naturalsciences.events.webdriver.WebDriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Main application for scraping and generating RSS feed.
 */
public final class EventsHarvesterApplication {

    private static final String DEFAULT_OUTPUT_FILE = "events.xml";
    private static final Logger LOG =
        LoggerFactory.getLogger(EventsHarvesterApplication.class);

    private EventsHarvesterApplication() {
        // utility
    }

    private static void configureLogging() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    /**
     * Main entry point for the application.
     *
     * @param args optional output file path (defaults to events.xml)
     */
    public static void main(final String[] args) {
        configureLogging();

        final String outputFile = args.length > 0 ? args[0] : DEFAULT_OUTPUT_FILE;

        LOG.info("Starting Natural Sciences Events Harvester");
        LOG.info("Output file: {}", outputFile);

        final EventsHarvesterApplication app =
            new EventsHarvesterApplication();
        app.run(outputFile);

        LOG.info("Harvesting completed successfully");
    }

    private EventParser createEventParser() {
        return new EventParserImpl();
    }

    private EventScraper createEventScraper(
        final ScraperConfiguration config,
        final WebDriverManager webDriverManager
    ) {
        final PageLoader pageLoader = new PageLoader(
            webDriverManager.getDriver(),
            config.getPageLoadTimeout()
        );

        final EventParser eventParser = createEventParser();

        return new EventScraperImpl(
            config,
            pageLoader,
            eventParser,
            new PaginationParser(),
            new EventLinkDiscoverer()
        );
    }

    /**
     * Runs the event harvesting workflow.
     *
     * @param outputFile path to output RSS file
     */
    public void run(final String outputFile) {
        final ScraperConfiguration config =
            new ScraperConfigurationImpl();

        final RssFeedManager feedManager = new RssFeedManagerImpl(config);

        try (WebDriverManager webDriverManager = new ChromeDriverManager(config)) {
            LOG.info("Loading existing feed");
            final Set<String> existingGuids = feedManager.loadExistingGuids(outputFile);
            LOG.info("Found {} existing events", existingGuids.size());

            final EventScraper scraper = createEventScraper(config, webDriverManager);

            LOG.info("Scraping events from {}", config.getBaseUrl());
            final List<EventItem> newEvents = scraper.scrapeEvents(existingGuids);

            LOG.info("Generating RSS feed");
            feedManager.generateFeed(
                outputFile,
                newEvents,
                outputFile
            );

        } catch (final Exception e) {
            LOG.error("Application failed", e);
            throw new RuntimeException("Event harvesting failed", e);
        }
    }
}
