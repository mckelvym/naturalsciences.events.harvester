package naturalsciences.events.feed;

import static java.util.Objects.requireNonNull;
import static naturalsciences.events.feed.RssElementNames.CHANNEL;
import static naturalsciences.events.feed.RssElementNames.DESCRIPTION;
import static naturalsciences.events.feed.RssElementNames.ENCLOSURE;
import static naturalsciences.events.feed.RssElementNames.ENCODING_UTF8;
import static naturalsciences.events.feed.RssElementNames.EVENT_NAMESPACE_URI;
import static naturalsciences.events.feed.RssElementNames.EV_ENDDATE;
import static naturalsciences.events.feed.RssElementNames.EV_STARTDATE;
import static naturalsciences.events.feed.RssElementNames.GUID;
import static naturalsciences.events.feed.RssElementNames.IMAGE_JPEG_TYPE;
import static naturalsciences.events.feed.RssElementNames.INDENT_AMOUNT;
import static naturalsciences.events.feed.RssElementNames.IS_PERMALINK_ATTR;
import static naturalsciences.events.feed.RssElementNames.ITEM;
import static naturalsciences.events.feed.RssElementNames.LANGUAGE;
import static naturalsciences.events.feed.RssElementNames.LANGUAGE_VALUE;
import static naturalsciences.events.feed.RssElementNames.LAST_BUILD_DATE;
import static naturalsciences.events.feed.RssElementNames.LINK;
import static naturalsciences.events.feed.RssElementNames.PUB_DATE;
import static naturalsciences.events.feed.RssElementNames.RSS;
import static naturalsciences.events.feed.RssElementNames.RSS_VERSION;
import static naturalsciences.events.feed.RssElementNames.TITLE;
import static naturalsciences.events.feed.RssElementNames.TRUE_VALUE;
import static naturalsciences.events.feed.RssElementNames.TYPE_ATTR;
import static naturalsciences.events.feed.RssElementNames.URL_ATTR;
import static naturalsciences.events.feed.RssElementNames.VERSION_ATTR;
import static naturalsciences.events.feed.RssElementNames.XMLNS_EV_ATTR;
import static naturalsciences.events.feed.RssElementNames.XSLT_INDENT_PROPERTY;

import java.io.File;
import java.io.FileOutputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import naturalsciences.events.config.ScraperConfiguration;
import naturalsciences.events.domain.EventItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * RSS feed manager implementation.
 * Handles RSS 2.0 feed generation with security hardening.
 */
public class RssFeedManagerImpl implements RssFeedManager {

    // Anything outside XML 1.0's Char production: #x9 | #xA | #xD | [#x20-#xD7FF] |
    // [#xE000-#xFFFD] | [#x10000-#x10FFFF]
    private static final Pattern INVALID_XML_CHARACTERS = Pattern.compile(
        "[^\\x09\\x0A\\x0D\\x{20}-\\x{D7FF}\\x{E000}-\\x{FFFD}\\x{10000}-\\x{10FFFF}]");
    private static final Logger LOG =
        LoggerFactory.getLogger(RssFeedManagerImpl.class);
    private static final DateTimeFormatter RSS_DATE_FORMAT =
        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    private static final DateTimeFormatter TITLE_DATE_FORMAT =
        DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
    private final ScraperConfiguration config;
    private final EventFilter eventFilter;
    private final XmlSecurityConfigurer securityConfigurer;

    /**
     * Creates a new RssFeedManagerImpl.
     *
     * @param config the scraper configuration
     * @throws NullPointerException if config is null
     */
    public RssFeedManagerImpl(final ScraperConfiguration config) {
        this.config = requireNonNull(config, "config must not be null");
        this.eventFilter = new EventFilter(config);
        this.securityConfigurer = new XmlSecurityConfigurer();
    }

    private void addChannelMetadata(final Document doc, final Element channel) {
        addTextElement(doc, channel, TITLE, config.getFeedTitle());
        addTextElement(doc, channel, LINK, config.getFeedLink());
        addTextElement(doc, channel, DESCRIPTION, config.getFeedDescription());
        addTextElement(doc, channel, LANGUAGE, LANGUAGE_VALUE);
        addTextElement(doc, channel, LAST_BUILD_DATE,
            ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));
    }

    /**
     * Adds a description element wrapped in CDATA, omitting it when empty.
     *
     * @param doc         the XML document
     * @param item        the item element to add to
     * @param description the description HTML or text
     */
    private void addDescriptionElement(final Document doc, final Element item,
                                       final String description) {
        if (description.isEmpty()) {
            return;
        }
        final Element element = doc.createElement(DESCRIPTION);
        element.appendChild(doc.createCDATASection(description));
        item.appendChild(element);
    }

    /**
     * Adds the machine-readable event dates (RSS Event module) used for retention.
     *
     * @param doc   the XML document
     * @param item  the item element to add to
     * @param event the event whose dates to add
     */
    private void addEventDateElements(final Document doc, final Element item,
                                      final EventItem event) {
        final Element startDate = doc.createElement(EV_STARTDATE);
        startDate.setTextContent(event.eventDateStart().toString());
        item.appendChild(startDate);
        if (event.eventDateEnd() != null) {
            final Element endDate = doc.createElement(EV_ENDDATE);
            endDate.setTextContent(event.eventDateEnd().toString());
            item.appendChild(endDate);
        }
    }

    private void addEventItem(
        final Document doc,
        final Element channel,
        final EventItem event
    ) {
        final Element item = doc.createElement(ITEM);

        final String titleWithDate = formatTitleWithDate(event);
        addTextElement(doc, item, TITLE, titleWithDate);
        addTextElement(doc, item, LINK, event.link());
        addGuidElement(doc, item, event);
        addEventDateElements(doc, item, event);

        addDescriptionElement(doc, item, event.sanitizedDescription());

        final String pubDate = ZonedDateTime.now()
            .format(RSS_DATE_FORMAT);
        addTextElement(doc, item, PUB_DATE, pubDate);

        if (event.hasImage()) {
            final Element enclosure = doc.createElement(ENCLOSURE);
            enclosure.setAttribute(URL_ATTR, event.imageUrl());
            enclosure.setAttribute(TYPE_ATTR, IMAGE_JPEG_TYPE);
            item.appendChild(enclosure);
        }

        channel.appendChild(item);
    }

    /**
     * Adds the item GUID, which is always the event URL and therefore a permalink.
     *
     * @param doc   the XML document
     * @param item  the item element to add to
     * @param event the event whose GUID to add
     */
    private void addGuidElement(final Document doc, final Element item, final EventItem event) {
        final Element guid = doc.createElement(GUID);
        guid.setAttribute(IS_PERMALINK_ATTR, TRUE_VALUE);
        guid.setTextContent(event.guid());
        item.appendChild(guid);
    }

    private void addTextElement(
        final Document doc,
        final Element parent,
        final String tagName,
        final String textContent
    ) {
        final Element element = doc.createElement(tagName);
        element.setTextContent(textContent);
        parent.appendChild(element);
    }

    /**
     * Formats the event title with date in parentheses.
     *
     * @param event the event to format
     * @return title with date appended, e.g., "Event Title (Nov 20, 2025)"
     */
    private String formatTitleWithDate(final EventItem event) {
        final String dateStr = event.eventDateStart().format(TITLE_DATE_FORMAT);
        return event.title() + " (" + dateStr + ")";
    }

    @Override
    public void generateFeed(
        final String feedFilePath,
        final List<EventItem> newEvents,
        final String existingFeedPath
    )
        throws Exception {
        requireNonNull(feedFilePath, "feedFilePath must not be null");
        requireNonNull(newEvents, "newEvents must not be null");
        requireNonNull(existingFeedPath, "existingFeedPath must not be null");

        final DocumentBuilderFactory factory =
            securityConfigurer.createSecureDocumentBuilderFactory();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document doc = builder.newDocument();

        final Element rss = doc.createElement(RSS);
        rss.setAttribute(VERSION_ATTR, RSS_VERSION);
        rss.setAttribute(XMLNS_EV_ATTR, EVENT_NAMESPACE_URI);
        doc.appendChild(rss);

        final Element channel = doc.createElement(CHANNEL);
        rss.appendChild(channel);

        addChannelMetadata(doc, channel);

        // Add new events (sorted by eventDateStart descending), skipping any past retention
        final List<EventItem> sortedEvents = new ArrayList<>(newEvents);
        sortedEvents.sort(Comparator.comparing(EventItem::eventDateStart).reversed());

        LOG.info("Adding {} new event(s) to feed", newEvents.size());
        for (final EventItem event : sortedEvents) {
            if (eventFilter.shouldKeep(event)) {
                addEventItem(doc, channel, event);
            }
        }

        importExistingEvents(doc, channel, new File(existingFeedPath));

        writeXmlToFile(doc, feedFilePath);

        LOG.info("RSS feed written to {}", feedFilePath);
    }

    /**
     * Imports items from the existing feed, dropping those past the retention period.
     *
     * <p>Errors are logged rather than thrown so a scheduled run still publishes new events.
     *
     * @param doc              the new feed document
     * @param channel          the channel to append items to
     * @param existingFeedFile the existing feed file (may not exist)
     */
    private void importExistingEvents(final Document doc, final Element channel,
                                      final File existingFeedFile) {
        if (!existingFeedFile.exists()) {
            return;
        }
        try {
            final DocumentBuilder builder =
                securityConfigurer.createSecureDocumentBuilderFactory().newDocumentBuilder();
            final NodeList items = builder.parse(existingFeedFile).getElementsByTagName(ITEM);
            int imported = 0;
            for (int i = 0; i < items.getLength(); i++) {
                final Element item = (Element) items.item(i);
                if (eventFilter.shouldKeep(item)) {
                    final Node importedNode = doc.importNode(item, true);
                    removeWhitespaceNodes(importedNode);
                    channel.appendChild(importedNode);
                    imported++;
                }
            }
            LOG.info("Imported {} existing events, dropped {} past retention",
                imported, items.getLength() - imported);
        } catch (final Exception e) {
            LOG.error("Failed to import existing events from {}: {}",
                existingFeedFile, e.getMessage(), e);
        }
    }

    @Override
    public Set<String> loadExistingGuids(final String feedFilePath)
        throws Exception {
        requireNonNull(feedFilePath, "feedFilePath must not be null");
        final Set<String> guids = new HashSet<>();
        final File feedFile = new File(feedFilePath);

        if (!feedFile.exists()) {
            LOG.info("No existing feed file found at {}", feedFilePath);
            return guids;
        }

        final DocumentBuilderFactory factory =
            securityConfigurer.createSecureDocumentBuilderFactory();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document doc = builder.parse(feedFile);

        final NodeList guidNodes = doc.getElementsByTagName(GUID);
        for (int i = 0; i < guidNodes.getLength(); i++) {
            final String guid = guidNodes.item(i).getTextContent();
            guids.add(guid);
        }

        LOG.info("Loaded {} existing GUID(s) from feed", guids.size());
        return guids;
    }

    /**
     * Removes characters that are not allowed in XML 1.0 from all text, CDATA and attributes.
     *
     * <p>Scraped text can contain control characters (e.g. U+0002). The serializer writes them
     * as character references such as {@code &#2;}, which no XML parser accepts, so the next
     * run could not read the feed.
     *
     * @param root the root node to clean
     */
    private void removeInvalidXmlCharacters(final Node root) {
        final Deque<Node> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            final Node current = stack.pop();
            final short type = current.getNodeType();
            if (type == Node.TEXT_NODE || type == Node.CDATA_SECTION_NODE) {
                stripInvalidXmlCharacters(current);
            }
            final NamedNodeMap attributes = current.getAttributes();
            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    stripInvalidXmlCharacters(attributes.item(i));
                }
            }
            final NodeList children = current.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                stack.push(children.item(i));
            }
        }
    }

    /**
     * Removes whitespace-only text nodes from a DOM tree.
     *
     * <p>This is necessary to ensure proper indentation when writing XML.
     *
     * @param node The root node to clean
     */
    private void removeWhitespaceNodes(final Node node) {
        final Deque<Node> stack = new ArrayDeque<>();
        stack.push(node);

        while (!stack.isEmpty()) {
            final Node current = stack.pop();
            final NodeList children = current.getChildNodes();

            for (int i = children.getLength() - 1; i >= 0; i--) {
                final Node child = children.item(i);
                if (child.getNodeType() == Node.TEXT_NODE) {
                    if (child.getTextContent().trim().isEmpty()) {
                        current.removeChild(child);
                    }
                } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                    stack.push(child);
                }
            }
        }
    }

    private void stripInvalidXmlCharacters(final Node node) {
        final String value = node.getNodeValue();
        final String cleaned = INVALID_XML_CHARACTERS.matcher(value).replaceAll("");
        if (!cleaned.equals(value)) {
            node.setNodeValue(cleaned);
        }
    }

    private void writeXmlToFile(final Document doc, final String filePath)
        throws Exception {
        removeInvalidXmlCharacters(doc);

        final TransformerFactory transformerFactory =
            securityConfigurer.createSecureTransformerFactory();
        final Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, ENCODING_UTF8);
        transformer.setOutputProperty(XSLT_INDENT_PROPERTY, INDENT_AMOUNT);

        final DOMSource source = new DOMSource(doc);
        final StreamResult result =
            new StreamResult(new FileOutputStream(filePath));

        transformer.transform(source, result);
    }
}
