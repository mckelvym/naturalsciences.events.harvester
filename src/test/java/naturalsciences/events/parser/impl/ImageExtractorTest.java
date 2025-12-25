package naturalsciences.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImageExtractorTest {

    private ImageExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new ImageExtractor();
    }

    @Test
    void extractImageUrl_withFeaturedImage_returnsAbsoluteUrl() {
        String html = "<div>"
                + "<img class=\"tribe-events-event-image\" src=\"/images/event.jpg\" />"
                + "</div>";
        Element element = Jsoup.parse(html, "https://naturalsciences.org").body();

        String result = extractor.extractImageUrl(element);

        assertThat(result).isEqualTo("https://naturalsciences.org/images/event.jpg");
    }

    @Test
    void extractImageUrl_withMetaOgImage_returnsContent() {
        String html = "<html><head>"
                + "<meta property=\"og:image\" content=\"https://naturalsciences.org/og-image.jpg\" />"
                + "</head><body></body></html>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractImageUrl(element);

        assertThat(result).isEqualTo("https://naturalsciences.org/og-image.jpg");
    }

    @Test
    void extractImageUrl_withFirstValidImage_returnsUrl() {
        String html = "<div>"
                + "<img src=\"https://naturalsciences.org/images/photo.jpg\" />"
                + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractImageUrl(element);

        assertThat(result).isEqualTo("https://naturalsciences.org/images/photo.jpg");
    }

    @Test
    void extractImageUrl_withIconOrLogo_returnsNull() {
        String html = "<div>"
                + "<img src=\"https://naturalsciences.org/icon.png\" />"
                + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractImageUrl(element);

        // Icon/logo filtering only applies in tryFirstImage, returns null if filtered
        assertThat(result).isNull();
    }

    @Test
    void extractImageUrl_withInvalidUrl_returnsNull() {
        String html = "<div>"
                + "<img src=\"not-an-image.txt\" />"
                + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractImageUrl(element);

        assertThat(result).isNull();
    }

    @Test
    void extractImageUrl_withQueryParams_acceptsUrl() {
        String html = "<div>"
                + "<img src=\"https://naturalsciences.org/image.jpg?size=large\" />"
                + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractImageUrl(element);

        assertThat(result).isEqualTo("https://naturalsciences.org/image.jpg?size=large");
    }

    @Test
    void extractImageUrl_withWebpFormat_acceptsUrl() {
        String html = "<div>"
                + "<img src=\"https://naturalsciences.org/image.webp\" />"
                + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractImageUrl(element);

        assertThat(result).isEqualTo("https://naturalsciences.org/image.webp");
    }

    @Test
    void extractImageUrl_withMultipleStrategies_prefersFeaturedImage() {
        String html = "<html><head>"
                + "<meta property=\"og:image\" content=\"https://naturalsciences.org/og.jpg\" />"
                + "</head><body>"
                + "<img class=\"tribe-events-event-image\" src=\"/featured.jpg\" />"
                + "<img src=\"/generic.jpg\" />"
                + "</body></html>";
        Element element = Jsoup.parse(html, "https://naturalsciences.org").body();

        String result = extractor.extractImageUrl(element);

        assertThat(result).isEqualTo("https://naturalsciences.org/featured.jpg");
    }

    @Test
    void extractImageUrl_withNoImages_returnsNull() {
        String html = "<div><p>No images</p></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractImageUrl(element);

        assertThat(result).isNull();
    }
}
