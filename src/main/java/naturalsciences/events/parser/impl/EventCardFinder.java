package naturalsciences.events.parser.impl;

import static naturalsciences.events.parser.impl.CssSelectors.EVENT_CARD_ARTICLE;
import static naturalsciences.events.parser.impl.CssSelectors.EVENT_CARD_DIV_ID;
import static naturalsciences.events.parser.impl.CssSelectors.EVENT_CARD_TRIBE_SINGLE;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Finds the main event card container in Natural Sciences event pages.
 * Searches for specific class patterns and element types.
 */
public final class EventCardFinder {

    private static final Logger LOG = LoggerFactory.getLogger(EventCardFinder.class);

    /**
     * Finds the event card container element.
     *
     * @param doc JSoup document of the event page
     * @return event card Element or the document body if not found
     */
    public Element findEventCard(final Document doc) {
        Element candidate = doc.selectFirst(EVENT_CARD_TRIBE_SINGLE);

        if (candidate == null) {
            candidate = doc.selectFirst(EVENT_CARD_ARTICLE);
        }

        if (candidate == null) {
            candidate = doc.selectFirst(EVENT_CARD_DIV_ID);
        }

        if (candidate == null) {
            LOG.warn("Could not find specific event container, using body");
            return doc.body();
        }

        return candidate;
    }
}
