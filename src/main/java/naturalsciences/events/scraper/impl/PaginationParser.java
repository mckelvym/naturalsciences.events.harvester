package naturalsciences.events.scraper.impl;

import static naturalsciences.events.parser.impl.CssSelectors.PAGINATION_NAV_LINKS;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses pagination information from Natural Sciences event pages.
 * Determines total number of pages available.
 */
public final class PaginationParser {

    private static final Logger LOG = LoggerFactory.getLogger(PaginationParser.class);
    private static final String TRIBE_PAGED = "tribe_paged=";

    private int extractPageNumber(final String url) {
        try {
            final String pageParam = url.substring(
                url.indexOf(TRIBE_PAGED) + TRIBE_PAGED.length());
            final int ampIndex = pageParam.indexOf('&');
            final String pageNumStr =
                ampIndex > 0 ? pageParam.substring(0, ampIndex) : pageParam;
            return Integer.parseInt(pageNumStr);
        } catch (final Exception e) {
            LOG.warn("Failed to extract page number from URL: {}", url, e);
            return 1;
        }
    }

    /**
     * Determines the total number of pages from the first page document.
     *
     * @param firstPageDoc JSoup document of the first page
     * @return total number of pages (minimum 1)
     */
    public int getTotalPages(final Document firstPageDoc) {
        int maxPage = 1;

        final Elements navLinks =
            firstPageDoc.select(PAGINATION_NAV_LINKS);

        for (final Element link : navLinks) {
            final String href = link.attr("href");
            if (href.contains(TRIBE_PAGED)) {
                final int pageNum = extractPageNumber(href);
                if (pageNum > maxPage) {
                    maxPage = pageNum;
                }
            }
        }

        LOG.info("Detected {} total page(s)", maxPage);
        return maxPage;
    }
}
