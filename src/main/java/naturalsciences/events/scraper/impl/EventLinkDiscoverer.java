package naturalsciences.events.scraper.impl;

import static naturalsciences.events.parser.impl.CssSelectors.EVENT_LINK_TITLE;
import static naturalsciences.events.parser.impl.HtmlConstants.ABS_HREF_ATTR;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Discovers event links on Natural Sciences event listing pages.
 * Filters links to ensure they match expected event URL patterns.
 */
public final class EventLinkDiscoverer {

    private static final Pattern EVENT_URL_PATTERN =
        Pattern.compile("^https://(www\\.)?naturalsciences\\.org/calendar/event/[^/]+/?.*$");
    private static final Logger LOG =
        LoggerFactory.getLogger(EventLinkDiscoverer.class);

    /**
     * Discovers all event links on a page.
     *
     * @param doc JSoup document of the event listing page
     * @return list of event URLs
     */
    public List<String> discoverEventLinks(final Document doc) {
        final List<String> links = new ArrayList<>();

        final Elements eventTitles =
            doc.select(EVENT_LINK_TITLE);

        for (final Element link : eventTitles) {
            final String href = link.attr(ABS_HREF_ATTR);
            if (isValidEventUrl(href)) {
                links.add(href);
            }
        }

        LOG.info("Discovered {} event link(s)", links.size());
        return links;
    }

    private boolean isValidEventUrl(final String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        return EVENT_URL_PATTERN.matcher(url).matches();
    }
}
