package naturalsciences.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for DateParser.
 * Verifies date parsing with multiple format strategies.
 */
class DateParserTest {

    private DateParser parser;

    @BeforeEach
    void setUp() {
        parser = new DateParser();
    }

    // Tests for parse() method

    @Test
    void parse_withFullMonthSingleDigitDay_returnsLocalDate() {
        LocalDate result = parser.parse("January 5, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 1, 5));
    }

    @Test
    void parse_withFullMonthDoubleDigitDay_returnsLocalDate() {
        LocalDate result = parser.parse("December 15, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withAbbreviatedMonthSingleDigitDay_returnsLocalDate() {
        LocalDate result = parser.parse("Jan 5, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 1, 5));
    }

    @Test
    void parse_withAbbreviatedMonthDoubleDigitDay_returnsLocalDate() {
        LocalDate result = parser.parse("Dec 15, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withSingleDigitSlashFormat_returnsLocalDate() {
        LocalDate result = parser.parse("1/5/2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 1, 5));
    }

    @Test
    void parse_withDoubleDigitSlashFormat_returnsLocalDate() {
        LocalDate result = parser.parse("12/15/2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withMixedDigitSlashFormat_returnsLocalDate() {
        LocalDate result = parser.parse("3/25/2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 3, 25));
    }

    @Test
    void parse_withIsoFormat_returnsLocalDate() {
        LocalDate result = parser.parse("2025-12-15");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withIsoLocalDateFormatter_returnsLocalDate() {
        LocalDate result = parser.parse("2025-01-05");

        assertThat(result).isEqualTo(LocalDate.of(2025, 1, 5));
    }

    @Test
    void parse_withRfc1123Format_returnsLocalDate() {
        LocalDate result = parser.parse("Mon, 15 Dec 2025 10:00:00 GMT");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withRfc1123DifferentDay_returnsLocalDate() {
        LocalDate result = parser.parse("Tue, 16 Dec 2025 14:30:00 GMT");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 16));
    }

    @Test
    void parse_withNull_returnsNull() {
        LocalDate result = parser.parse(null);

        assertThat(result).isNull();
    }

    @Test
    void parse_withEmptyString_returnsNull() {
        LocalDate result = parser.parse("");

        assertThat(result).isNull();
    }

    @Test
    void parse_withBlankString_returnsNull() {
        LocalDate result = parser.parse("   ");

        assertThat(result).isNull();
    }

    @Test
    void parse_withTabsAndSpaces_returnsNull() {
        LocalDate result = parser.parse(" \t  \t ");

        assertThat(result).isNull();
    }

    @Test
    void parse_withInvalidFormat_returnsNull() {
        LocalDate result = parser.parse("not a date");

        assertThat(result).isNull();
    }

    @Test
    void parse_withPartialDate_returnsNull() {
        LocalDate result = parser.parse("December 2025");

        assertThat(result).isNull();
    }

    @Test
    void parse_withInvalidMonth_returnsNull() {
        LocalDate result = parser.parse("Decembar 15, 2025");

        assertThat(result).isNull();
    }

    @Test
    void parse_withWhitespace_trimsAndParses() {
        LocalDate result = parser.parse("  December 15, 2025  ");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withLeadingWhitespace_trimsAndParses() {
        LocalDate result = parser.parse("  12/15/2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withTrailingWhitespace_trimsAndParses() {
        LocalDate result = parser.parse("12/15/2025  ");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withMultipleSpaces_trimsAndParses() {
        LocalDate result = parser.parse("    January 1, 2025    ");

        assertThat(result).isEqualTo(LocalDate.of(2025, 1, 1));
    }

    // Edge cases with different months

    @Test
    void parse_withJanuary_returnsLocalDate() {
        LocalDate result = parser.parse("January 1, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 1, 1));
    }

    @Test
    void parse_withFebruary_returnsLocalDate() {
        LocalDate result = parser.parse("February 14, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 2, 14));
    }

    @Test
    void parse_withMarch_returnsLocalDate() {
        LocalDate result = parser.parse("March 17, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 3, 17));
    }

    @Test
    void parse_withApril_returnsLocalDate() {
        LocalDate result = parser.parse("April 1, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 4, 1));
    }

    @Test
    void parse_withMay_returnsLocalDate() {
        LocalDate result = parser.parse("May 5, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 5, 5));
    }

    @Test
    void parse_withJune_returnsLocalDate() {
        LocalDate result = parser.parse("June 21, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 6, 21));
    }

    @Test
    void parse_withJuly_returnsLocalDate() {
        LocalDate result = parser.parse("July 4, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 7, 4));
    }

    @Test
    void parse_withAugust_returnsLocalDate() {
        LocalDate result = parser.parse("August 15, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 8, 15));
    }

    @Test
    void parse_withSeptember_returnsLocalDate() {
        LocalDate result = parser.parse("September 1, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 9, 1));
    }

    @Test
    void parse_withOctober_returnsLocalDate() {
        LocalDate result = parser.parse("October 31, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 10, 31));
    }

    @Test
    void parse_withNovember_returnsLocalDate() {
        LocalDate result = parser.parse("November 25, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 11, 25));
    }

    @Test
    void parse_withDecember_returnsLocalDate() {
        LocalDate result = parser.parse("December 25, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 25));
    }

    // Edge cases with abbreviated months

    @Test
    void parse_withAbbreviatedJan_returnsLocalDate() {
        LocalDate result = parser.parse("Jan 1, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 1, 1));
    }

    @Test
    void parse_withAbbreviatedFeb_returnsLocalDate() {
        LocalDate result = parser.parse("Feb 14, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 2, 14));
    }

    @Test
    void parse_withAbbreviatedMar_returnsLocalDate() {
        LocalDate result = parser.parse("Mar 17, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 3, 17));
    }

    @Test
    void parse_withAbbreviatedApr_returnsLocalDate() {
        LocalDate result = parser.parse("Apr 1, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 4, 1));
    }

    @Test
    void parse_withAbbreviatedSep_returnsLocalDate() {
        LocalDate result = parser.parse("Sep 1, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 9, 1));
    }

    @Test
    void parse_withAbbreviatedOct_returnsLocalDate() {
        LocalDate result = parser.parse("Oct 31, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 10, 31));
    }

    @Test
    void parse_withAbbreviatedNov_returnsLocalDate() {
        LocalDate result = parser.parse("Nov 25, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 11, 25));
    }

    @Test
    void parse_withAbbreviatedDec_returnsLocalDate() {
        LocalDate result = parser.parse("Dec 25, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 25));
    }

    // Edge cases with day boundaries

    @Test
    void parse_withFirstDayOfMonth_returnsLocalDate() {
        LocalDate result = parser.parse("March 1, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 3, 1));
    }

    @Test
    void parse_withLastDayOfMonth30_returnsLocalDate() {
        LocalDate result = parser.parse("April 30, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 4, 30));
    }

    @Test
    void parse_withLastDayOfMonth31_returnsLocalDate() {
        LocalDate result = parser.parse("May 31, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 5, 31));
    }

    @Test
    void parse_withLeapYearFebruary_returnsLocalDate() {
        LocalDate result = parser.parse("February 29, 2024");

        assertThat(result).isEqualTo(LocalDate.of(2024, 2, 29));
    }

    @Test
    void parse_withNonLeapYearFebruary_returnsLocalDate() {
        LocalDate result = parser.parse("February 28, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 2, 28));
    }

    // Edge cases with year boundaries

    @Test
    void parse_withNewYearsDay_returnsLocalDate() {
        LocalDate result = parser.parse("January 1, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 1, 1));
    }

    @Test
    void parse_withNewYearsEve_returnsLocalDate() {
        LocalDate result = parser.parse("December 31, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 31));
    }

    @Test
    void parse_withFutureYear_returnsLocalDate() {
        LocalDate result = parser.parse("June 15, 2030");

        assertThat(result).isEqualTo(LocalDate.of(2030, 6, 15));
    }

    @Test
    void parse_withPastYear_returnsLocalDate() {
        LocalDate result = parser.parse("March 10, 2020");

        assertThat(result).isEqualTo(LocalDate.of(2020, 3, 10));
    }
}
