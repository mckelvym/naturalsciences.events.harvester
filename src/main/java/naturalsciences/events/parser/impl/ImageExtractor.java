package naturalsciences.events.parser.impl;

import static naturalsciences.events.parser.impl.CssSelectors.IMAGE_CLASS_EVENT;
import static naturalsciences.events.parser.impl.CssSelectors.IMAGE_SRC;
import static naturalsciences.events.parser.impl.CssSelectors.META_IMAGE;
import static naturalsciences.events.parser.impl.HtmlConstants.CONTENT_ATTR;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Extracts event image URL from Natural Sciences event pages.
 * Searches for featured images and fallback options.
 */
public final class ImageExtractor {

    private static final String ABS_SRC = "abs:src";

    /**
     * Extracts the event image URL from the event card.
     *
     * @param eventCard the event card container element
     * @return image URL or null if not found
     */
    public String extractImageUrl(final Element eventCard) {
        String imageUrl = tryFeaturedImage(eventCard);
        if (imageUrl != null) {
            return imageUrl;
        }

        imageUrl = tryMetaTag(eventCard);
        if (imageUrl != null) {
            return imageUrl;
        }

        imageUrl = tryFirstImage(eventCard);
        return imageUrl;
    }

    private boolean isIconOrLogo(final String url) {
        final String lower = url.toLowerCase();
        return lower.contains("icon") || lower.contains("logo")
            || lower.contains("favicon");
    }

    private boolean isValidImageUrl(final String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        final String lower = url.toLowerCase();
        return (lower.startsWith("http://") || lower.startsWith("https://"))
            && (lower.endsWith(".jpg") || lower.endsWith(".jpeg")
            || lower.endsWith(".png") || lower.endsWith(".webp")
            || lower.contains(".jpg?") || lower.contains(".jpeg?")
            || lower.contains(".png?") || lower.contains(".webp?"));
    }

    private String tryFeaturedImage(final Element eventCard) {
        final Element img = eventCard.selectFirst(IMAGE_CLASS_EVENT);
        if (img != null) {
            final String src = img.attr(ABS_SRC);
            if (isValidImageUrl(src)) {
                return src;
            }
        }
        return null;
    }

    private String tryFirstImage(final Element eventCard) {
        final Element img = eventCard.selectFirst(IMAGE_SRC);
        if (img != null) {
            final String src = img.attr(ABS_SRC);
            if (isValidImageUrl(src) && !isIconOrLogo(src)) {
                return src;
            }
        }
        return null;
    }

    private String tryMetaTag(final Element eventCard) {
        final Document ownerDocument = eventCard.ownerDocument();
        if (ownerDocument == null) {
            return null;
        }
        final Element metaImg = ownerDocument
            .selectFirst(META_IMAGE);
        if (metaImg != null) {
            final String content = metaImg.attr(CONTENT_ATTR);
            if (isValidImageUrl(content)) {
                return content;
            }
        }
        return null;
    }
}
