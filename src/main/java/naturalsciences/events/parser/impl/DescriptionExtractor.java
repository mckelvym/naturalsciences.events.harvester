package naturalsciences.events.parser.impl;

import static java.util.Objects.requireNonNull;
import static naturalsciences.events.parser.impl.CssSelectors.DESC_CLASS_CONTENT;
import static naturalsciences.events.parser.impl.CssSelectors.DESC_CLASS_ENTRY;
import static naturalsciences.events.parser.impl.CssSelectors.DESC_CLASS_SINGLE_EVENT;
import static naturalsciences.events.parser.impl.CssSelectors.DESC_PARAGRAPH;
import static naturalsciences.events.parser.impl.CssSelectors.META_DESCRIPTION;
import static naturalsciences.events.parser.impl.HtmlConstants.CONTENT_ATTR;
import static naturalsciences.events.parser.impl.HtmlConstants.EMPTY;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extracts event description from event pages.
 * Handles various description container formats.
 */
public final class DescriptionExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(DescriptionExtractor.class);
    private static final int MAX_DESCRIPTION_LENGTH = 500;

    /**
     * Extracts the event description from the event card.
     *
     * @param element the event card container element
     * @return extracted description or empty string if not found
     */
    public String extract(final Element element) {
        requireNonNull(element, "element must not be null");
        String description = tryClassSelector(element, DESC_CLASS_SINGLE_EVENT);
        if (description != null) {
            return description;
        }

        description = tryClassSelector(element, DESC_CLASS_CONTENT);
        if (description != null) {
            return description;
        }

        description = tryClassSelector(element, DESC_CLASS_ENTRY);
        if (description != null) {
            return description;
        }

        description = tryMetaTag(element);
        if (description != null) {
            return description;
        }

        LOG.warn("No description found");
        return EMPTY;
    }

    private String extractTextFromElement(final Element element) {
        final Elements paragraphs = element.select(DESC_PARAGRAPH);
        if (!paragraphs.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (final Element p : paragraphs) {
                final String pText = p.text().trim();
                if (!pText.isEmpty()) {
                    if (!sb.isEmpty()) {
                        sb.append(" ");
                    }
                    sb.append(pText);
                }
            }
            return sb.toString();
        }

        return element.text().trim();
    }

    private String truncateDescription(final String description) {
        if (description.length() <= MAX_DESCRIPTION_LENGTH) {
            return description;
        }
        return description.substring(0, MAX_DESCRIPTION_LENGTH) + "...";
    }

    private String tryClassSelector(final Element eventCard, final String className) {
        final Element element = eventCard.selectFirst("." + className);
        if (element != null) {
            String text = extractTextFromElement(element);
            if (!text.isEmpty()) {
                return truncateDescription(text);
            }
        }
        return null;
    }

    private String tryMetaTag(final Element eventCard) {
        final Element metaDesc =
            requireNonNull(eventCard.ownerDocument()).selectFirst(META_DESCRIPTION);
        if (metaDesc != null) {
            final String content = metaDesc.attr(CONTENT_ATTR).trim();
            if (!content.isEmpty()) {
                return truncateDescription(content);
            }
        }
        return null;
    }
}
