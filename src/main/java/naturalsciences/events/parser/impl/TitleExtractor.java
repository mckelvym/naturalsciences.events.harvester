package naturalsciences.events.parser.impl;

import static naturalsciences.events.parser.impl.CssSelectors.META_TITLE;
import static naturalsciences.events.parser.impl.CssSelectors.TITLE_CLASS_ENTRY;
import static naturalsciences.events.parser.impl.CssSelectors.TITLE_CLASS_SINGLE_EVENT;
import static naturalsciences.events.parser.impl.HtmlConstants.CONTENT_ATTR;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extracts event title from Natural Sciences event pages.
 * Uses multiple fallback strategies for robustness.
 */
public final class TitleExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(TitleExtractor.class);

    /**
     * Extracts the event title from the event card.
     *
     * @param eventCard the event card container element
     * @return extracted title or "Untitled Event" if not found
     */
    public String extractTitle(final Element eventCard) {
        String title = tryHeadings(eventCard);
        if (title != null) {
            return title;
        }

        title = tryClassSelector(eventCard, TITLE_CLASS_SINGLE_EVENT);
        if (title != null) {
            return title;
        }

        title = tryClassSelector(eventCard, TITLE_CLASS_ENTRY);
        if (title != null) {
            return title;
        }

        title = tryMetaTag(eventCard);
        if (title != null) {
            return title;
        }

        LOG.warn("Could not extract title, using default");
        return "Untitled Event";
    }

    private String tryClassSelector(final Element eventCard, final String className) {
        final Element element = eventCard.selectFirst("." + className);
        if (element != null) {
            final String text = element.text().trim();
            if (!text.isEmpty()) {
                return text;
            }
        }
        return null;
    }

    private String tryHeadings(final Element eventCard) {
        for (int i = 1; i <= 3; i++) {
            final Element heading = eventCard.selectFirst("h" + i);
            if (heading != null) {
                final String text = heading.text().trim();
                if (!text.isEmpty()) {
                    return text;
                }
            }
        }
        return null;
    }

    private String tryMetaTag(final Element eventCard) {
        final Element metaTitle =
            eventCard.ownerDocument().selectFirst(META_TITLE);
        if (metaTitle != null) {
            final String content = metaTitle.attr(CONTENT_ATTR).trim();
            if (!content.isEmpty()) {
                return content;
            }
        }
        return null;
    }
}
