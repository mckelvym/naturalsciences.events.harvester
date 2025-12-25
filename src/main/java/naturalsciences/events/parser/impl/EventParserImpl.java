package naturalsciences.events.parser.impl;


import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Optional;
import naturalsciences.events.domain.EventItem;
import naturalsciences.events.parser.EventParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses and extracts event fields.
 */
public final class EventParserImpl implements EventParser {

    private static final Logger LOG =
        LoggerFactory.getLogger(EventParserImpl.class);

    private final EventCardFinder cardFinder;
    private final DateExtractor dateExtractor;
    private final DescriptionExtractor descriptionExtractor;
    private final ImageExtractor imageExtractor;
    private final TitleExtractor titleExtractor;

    /**
     * Creates a new EventParserImpl.
     */
    public EventParserImpl() {
        this.cardFinder = new EventCardFinder();
        this.titleExtractor = new TitleExtractor();
        this.dateExtractor = new DateExtractor();
        this.descriptionExtractor = new DescriptionExtractor();
        this.imageExtractor = new ImageExtractor();
    }

    private String extractIdFromUrl(final String url) {
        final int lastSlash = url.lastIndexOf('/');
        final int secondLastSlash = url.lastIndexOf('/', lastSlash - 1);

        if (secondLastSlash >= 0) {
            String segment = url.substring(secondLastSlash + 1, lastSlash);
            if (segment.isEmpty()) {
                segment = url.substring(secondLastSlash + 1);
            }
            return segment;
        }

        return String.valueOf(url.hashCode());
    }

    @Override
    public Optional<EventItem> parseEvent(final Document doc, final String eventUrl) {
        requireNonNull(doc, "doc must not be null");
        requireNonNull(eventUrl, "eventUrl must not be null");
        final Element eventCard = cardFinder.findEventCard(doc);

        final String title = titleExtractor.extractTitle(eventCard);
        if (title.isEmpty()) {
            LOG.warn("Skipping event with missing title: {}", eventUrl);
            return Optional.empty();
        }

        final LocalDate eventDate = dateExtractor.extractLocalDateTime(eventCard).toLocalDate();
        final String description =
            descriptionExtractor.extract(eventCard);
        final String imageUrl = imageExtractor.extractImageUrl(eventCard);

        final String id = extractIdFromUrl(eventUrl);

        final EventItem event = new EventItem(
            id,
            title,
            eventUrl,
            description,
            eventDate,
            null,
            imageUrl,
            null
        );

        return Optional.of(event);
    }
}
