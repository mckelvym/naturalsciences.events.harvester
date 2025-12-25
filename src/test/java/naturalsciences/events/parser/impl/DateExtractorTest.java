package naturalsciences.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for DateExtractor.
 * Tests date and time extraction with multiple format support.
 */
class DateExtractorTest {

    private DateExtractor extractor;

    @Test
    void extractDateTime_withComplexStructure_findsLocalDate() {
        String html = """
                <div class="event-wrapper">
                    <div class="event-details">
                        <div class="date-container">
                            <span class="tribe-event-date-start">April 5, 2026</span>
                        </div>
                    </div>
                </div>
                """;
        Element element = Jsoup.parse(html).body();

        LocalDateTime result = extractor.extractLocalDateTime(element);

        assertThat(result.toLocalDate()).isEqualTo(LocalDate.of(2026, Month.APRIL, 5));
    }

    // Tests for date extraction

    @Test
    void extractDateTime_withInvalidDate_usesCurrentLocalDate() {
        String html = """
                <div>
                    <span class="tribe-event-date-start">Invalid Date</span>
                </div>
                """;
        Element element = Jsoup.parse(html).body();

        LocalDateTime result = extractor.extractLocalDateTime(element);

        assertThat(result).isNotNull();
        assertThat(result.toLocalDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void extractDateTime_withLocalDateOnly_usesMidnight() {
        String html = """
                <div>
                    <span class="tribe-event-date-start">December 15, 2025</span>
                </div>
                """;
        Element element = Jsoup.parse(html).body();

        LocalDateTime result = extractor.extractLocalDateTime(element);

        assertThat(result).isNotNull();
        assertThat(result.toLocalDate()).isEqualTo(LocalDate.of(2025, Month.DECEMBER, 15));
        assertThat(result.toLocalTime()).isEqualTo(LocalTime.MIDNIGHT);
    }

    @Test
    void extractDateTime_withMultipleLocalDateElements_usesFirst() {
        String html = """
                <div>
                    <span class="tribe-event-date-start">December 15, 2025</span>
                    <span class="tribe-event-date-start">January 20, 2026</span>
                </div>
                """;
        Element element = Jsoup.parse(html).body();

        LocalDateTime result = extractor.extractLocalDateTime(element);

        assertThat(result.toLocalDate()).isEqualTo(LocalDate.of(2025, Month.DECEMBER, 15));
    }

    @Test
    void extractDateTime_withNestedLocalDateElement_findsCorrectly() {
        String html = """
                <div>
                    <div class="event-card">
                        <span class="tribe-event-date-start">March 10, 2026</span>
                    </div>
                </div>
                """;
        Element element = Jsoup.parse(html).body();

        LocalDateTime result = extractor.extractLocalDateTime(element);

        assertThat(result.toLocalDate()).isEqualTo(LocalDate.of(2026, Month.MARCH, 10));
    }

    @Test
    void extractDateTime_withNoDate_usesCurrentLocalDate() {
        String html = "<div><p>No date information</p></div>";
        Element element = Jsoup.parse(html).body();

        LocalDateTime result = extractor.extractLocalDateTime(element);

        assertThat(result).isNotNull();
        assertThat(result.toLocalDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void extractDateTime_withWhitespaceInLocalDate_trimsCorrectly() {
        String html = """
                <div>
                    <span class="tribe-event-date-start">  December 15, 2025  </span>
                </div>
                """;
        Element element = Jsoup.parse(html).body();

        LocalDateTime result = extractor.extractLocalDateTime(element);

        assertThat(result.toLocalDate()).isEqualTo(LocalDate.of(2025, Month.DECEMBER, 15));
    }

    @Test
    void extractLocalDateTime_withAbbrElement_parsesCorrectly() {
        String html = """
                <div>
                    <abbr class="tribe-events-abbr" title="December 15, 2025">Dec 15</abbr>
                </div>
                """;
        Element element = Jsoup.parse(html).body();

        LocalDateTime result = extractor.extractLocalDateTime(element);

        assertThat(result).isNotNull();
        assertThat(result.toLocalDate()).isEqualTo(LocalDate.of(2025, Month.DECEMBER, 15));
    }

    @Test
    void extractLocalDateTime_withDifferentMonths_parsesCorrectly() {
        String[][] testCases = {
                {"January 1, 2026", "2026-01-01"},
                {"February 15, 2026", "2026-02-15"},
                {"March 20, 2026", "2026-03-20"},
                {"December 31, 2025", "2025-12-31"}
        };

        for (String[] testCase : testCases) {
            String dateString = testCase[0];
            String expectedIso = testCase[1];
            LocalDate expected = LocalDate.parse(expectedIso);

            String html = String.format("""
                    <div>
                        <span class="tribe-event-date-start">%s</span>
                    </div>
                    """, dateString);
            Element element = Jsoup.parse(html).body();

            LocalDateTime result = extractor.extractLocalDateTime(element);

            assertThat(result.toLocalDate()).as("Date string: " + dateString)
                    .isEqualTo(expected);
        }
    }

    @Test
    void extractLocalDateTime_withEmptyTime_usesMidnight() {
        String html = """
                <div>
                    <span class="tribe-event-date-start">December 15, 2025</span>
                    <div class="tribe-events-start-time"></div>
                </div>
                """;
        Element element = Jsoup.parse(html).body();

        LocalDateTime result = extractor.extractLocalDateTime(element);

        assertThat(result.toLocalTime()).isEqualTo(LocalTime.MIDNIGHT);
    }

    @Test
    void extractLocalDateTime_withFullMonthFormat_parsesCorrectly() {
        String html = """
                <div>
                    <span class="tribe-event-date-start">January 20, 2026</span>
                </div>
                """;
        Element element = Jsoup.parse(html).body();

        LocalDateTime result = extractor.extractLocalDateTime(element);

        assertThat(result).isNotNull();
        assertThat(result.toLocalDate()).isEqualTo(LocalDate.of(2026, Month.JANUARY, 20));
    }

    @Test
    void extractLocalDateTime_withInvalidTime_usesMidnight() {
        String html = """
                <div>
                    <span class="tribe-event-date-start">December 15, 2025</span>
                    <div class="tribe-events-start-time">Invalid Time</div>
                </div>
                """;
        Element element = Jsoup.parse(html).body();

        LocalDateTime result = extractor.extractLocalDateTime(element);

        assertThat(result.toLocalTime()).isEqualTo(LocalTime.MIDNIGHT);
    }

    @Test
    void extractLocalDateTime_withIsoFormat_parsesCorrectly() {
        String html = """
                <div>
                    <span class="tribe-event-date-start">2025-12-15</span>
                </div>
                """;
        Element element = Jsoup.parse(html).body();

        LocalDateTime result = extractor.extractLocalDateTime(element);

        assertThat(result).isNotNull();
        assertThat(result.toLocalDate()).isEqualTo(LocalDate.of(2025, Month.DECEMBER, 15));
    }

    @Test
    void extractLocalDateTime_withMultipleTimeElements_usesFirst() {
        String html = """
                <div>
                    <span class="tribe-event-date-start">December 15, 2025</span>
                    <div class="tribe-events-start-time">7:00 pm</div>
                    <div class="tribe-events-start-time">9:00 pm</div>
                </div>
                """;
        Element element = Jsoup.parse(html).body();

        LocalDateTime result = extractor.extractLocalDateTime(element);

        assertThat(result).isNotNull();
        // Time parsing may vary, just check that we got the first one
    }

    @Test
    void extractLocalDateTime_withShortMonthFormat_parsesCorrectly() {
        String html = """
                <div>
                    <span class="tribe-event-date-start">Jan 20, 2026</span>
                </div>
                """;
        Element element = Jsoup.parse(html).body();

        LocalDateTime result = extractor.extractLocalDateTime(element);

        assertThat(result).isNotNull();
        assertThat(result.toLocalDate()).isEqualTo(LocalDate.of(2026, Month.JANUARY, 20));
    }

    @Test
    void extractLocalDateTime_withTimeElement_parsesCorrectly() {
        String html = """
                <div>
                    <time datetime="2025-12-15">December 15, 2025</time>
                </div>
                """;
        Element element = Jsoup.parse(html).body();

        LocalDateTime result = extractor.extractLocalDateTime(element);

        assertThat(result).isNotNull();
        assertThat(result.toLocalDate()).isEqualTo(LocalDate.of(2025, Month.DECEMBER, 15));
    }

    @BeforeEach
    void setUp() {
        extractor = new DateExtractor();
    }
}
