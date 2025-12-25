package naturalsciences.events.config.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ScraperConfigurationImpl.
 */
class ScraperConfigurationImplTest {

    private ScraperConfigurationImpl config;

    @BeforeEach
    void setUp() {
        config = new ScraperConfigurationImpl();
    }

    @Test
    void testGetBaseUrl() {
        assertThat(config.getBaseUrl())
                .contains("https://naturalsciences.org/calendar/events/")
                .contains("science%20cafe");
    }

    @Test
    void testGetPageLoadSelector() {
        assertThat(config.getPageLoadSelector())
                .isEqualTo("div.tribe-events-loop");
    }

    @Test
    void testGetPageLoadTimeout() {
        assertThat(config.getPageLoadTimeout().toSecondsPart())
                .isEqualTo(10);
    }

    @Test
    void testGetRetentionDays() {
        assertThat(config.getRetentionDays())
                .isEqualTo(7);
    }

    @Test
    void testGetUserAgent() {
        assertThat(config.getUserAgent())
                .contains("Mozilla")
                .contains("Chrome");
    }

    @Test
    void testGetUrlForPage() {
        assertThat(config.getUrlForPage(1))
                .doesNotContain("tribe_paged");
        assertThat(config.getUrlForPage(2))
                .contains("tribe_paged=2");
        assertThat(config.getUrlForPage(5))
                .contains("tribe_paged=5");
    }
}
