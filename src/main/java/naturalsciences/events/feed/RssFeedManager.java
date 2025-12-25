package naturalsciences.events.feed;

import java.util.List;
import java.util.Set;
import naturalsciences.events.domain.EventItem;

/**
 * Interface for managing RSS feed generation and persistence.
 * Handles loading existing feeds and generating updated feeds.
 */
public interface RssFeedManager {

    /**
     * Generates and writes an RSS feed with new and existing events.
     *
     * @param feedFilePath     path to write the RSS feed file
     * @param newEvents        list of new events to add
     * @param existingFeedPath path to existing feed to import from
     * @throws Exception if generation fails
     */
    void generateFeed(
        String feedFilePath,
        List<EventItem> newEvents,
        String existingFeedPath
    )
        throws Exception;

    /**
     * Loads existing GUIDs from the current RSS feed file.
     *
     * @param feedFilePath path to the existing RSS feed file
     * @return set of GUIDs from existing events
     * @throws Exception if loading fails
     */
    Set<String> loadExistingGuids(String feedFilePath)
        throws Exception;
}
