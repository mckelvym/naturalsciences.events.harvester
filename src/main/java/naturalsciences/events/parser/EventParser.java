package naturalsciences.events.parser;


import java.util.Optional;
import naturalsciences.events.domain.EventItem;
import org.jsoup.nodes.Document;

/**
 * Interface for parsing event information from HTML elements.
 */
public interface EventParser {

    /**
     * Parses an event from an HTML document.
     *
     * @param doc      JSoup document of the event detail page
     * @param eventUrl URL of the event (used as GUID)
     * @return Optional containing EventItem if parsing succeeds, empty otherwise
     */
    Optional<EventItem> parseEvent(Document doc, String eventUrl);
}
