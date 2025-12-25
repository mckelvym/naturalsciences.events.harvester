package naturalsciences.events.parser.impl;

import static naturalsciences.events.parser.impl.CssSelectors.DATE_ABBR;
import static naturalsciences.events.parser.impl.CssSelectors.DATE_SPAN_START;
import static naturalsciences.events.parser.impl.CssSelectors.DATE_TIME_DATETIME;
import static naturalsciences.events.parser.impl.CssSelectors.TIME_DIV_START;
import static naturalsciences.events.parser.impl.HtmlConstants.DATETIME_ATTR;
import static naturalsciences.events.parser.impl.HtmlConstants.EMPTY;
import static naturalsciences.events.parser.impl.HtmlConstants.TITLE_ATTR;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extracts event date and time from Natural Sciences event pages.
 * Handles various date/time formats and fallback strategies.
 */
public final class DateExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(DateExtractor.class);
    private static final DateTimeFormatter[] TIME_FORMATTERS = {
        DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("h a", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("ha", Locale.ENGLISH)
    };
    private final DateParser dateParser;

    /**
     * Creates a DateExtractor with default DateParser.
     */
    public DateExtractor() {
        this.dateParser = new DateParser();
    }

    private String extractDateString(final Element eventElement) {
        Element dateElement =
            eventElement.selectFirst(DATE_SPAN_START);
        if (dateElement != null) {
            return dateElement.text().trim();
        }

        dateElement = eventElement.selectFirst(DATE_TIME_DATETIME);
        if (dateElement != null) {
            return dateElement.attr(DATETIME_ATTR);
        }

        dateElement = eventElement.selectFirst(DATE_ABBR);
        if (dateElement != null) {
            return dateElement.attr(TITLE_ATTR);
        }

        return "";
    }

    /**
     * Extracts the event date and time from the event card.
     *
     * @param eventElement the event card container element
     * @return LocalDateTime of the event or current time if not found
     */
    public LocalDateTime extractLocalDateTime(final Element eventElement) {
        final String dateStr = extractDateString(eventElement);
        final String timeStr = extractTimeString(eventElement);

        LocalDate date = parseDate(dateStr);
        if (date == null) {
            LOG.warn("Could not parse date, using current date");
            date = LocalDate.now();
        }

        LocalTime time = parseTime(timeStr);
        if (time == null) {
            time = LocalTime.MIDNIGHT;
        }

        return LocalDateTime.of(date, time);
    }

    private String extractTimeString(final Element eventElement) {
        final Element timeElement =
            eventElement.selectFirst(TIME_DIV_START);
        if (timeElement != null) {
            return timeElement.text().trim();
        }

        return "";
    }

    private LocalDate parseDate(final String dateStr) {
        return dateParser.parse(dateStr);
    }

    private LocalTime parseTime(final String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return null;
        }

        final String cleanTime = timeStr
            .trim()
            .replaceAll("\\s+", " ")
            .replace(".", EMPTY)
            .toLowerCase();

        for (final DateTimeFormatter formatter : TIME_FORMATTERS) {
            try {
                return LocalTime.parse(cleanTime, formatter);
            } catch (final DateTimeParseException e) {
                // Try next formatter
            }
        }

        return null;
    }
}
