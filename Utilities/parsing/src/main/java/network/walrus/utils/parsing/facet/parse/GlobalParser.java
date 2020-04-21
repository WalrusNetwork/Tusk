package network.walrus.utils.parsing.facet.parse;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URLConnection;
import java.util.logging.Logger;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.xml.XmlElement;
import network.walrus.utils.parsing.facet.ProtoVersionHistory;
import network.walrus.utils.parsing.facet.parse.include.IncludeProcessor;
import network.walrus.utils.parsing.facet.parse.include.XmlIncludeProcessor;
import network.walrus.utils.parsing.world.config.ConfigurationParseException;
import network.walrus.utils.parsing.world.config.FacetConfigurationSource;
import network.walrus.utils.parsing.world.library.WorldSource;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;

/**
 * Parser responsible for parsing every loaded {@link FacetConfigurationSource}'s configuration
 * file. This is used for basic pre-holder parsing, and the data retained from this process will
 * stay around as long as the game management system is loaded. This is only used to determine base
 * data, and to perform some basic validations.
 *
 * @param <S> type of source this parser will be creating
 * @author Austin Mayes
 */
public abstract class GlobalParser<S extends FacetConfigurationSource> {

  private final Logger logger;

  /**
   * Constructor.
   *
   * @param logger used to log parsing errors and info to
   */
  public GlobalParser(Logger logger) {
    this.logger = logger;
  }

  /** @return the logger used to log parsing errors to */
  public Logger logger() {
    return this.logger;
  }

  /**
   * Parse a {@link S souce} from a {@link WorldSource} using configuration files.
   *
   * @param source to get configuration and other files from
   * @return a parsed {@link S}
   * @throws ConfigurationParseException if the configuration is malformed or contains invalid base
   *     data
   */
  public S parse(WorldSource source) throws ConfigurationParseException {
    S created = construct(source);
    validateProto(created);
    return parseInternal(created);
  }

  /**
   * The main parse method called after the configuration proto has been verified and the holder has
   * been constructed.
   *
   * @param source to add data to
   * @return a parsed form of {@link S} with no errors
   * @throws ConfigurationParseException if the configuration contains errors
   */
  public abstract S parseInternal(S source) throws ConfigurationParseException;

  private void validateProto(S source) throws ConfigurationParseException {
    if (ProtoVersionHistory.MIN_PROTO.greater(source.versionInfo().getProto())) {
      throw new ConfigurationParseException(
          "Proto "
              + source.versionInfo().getProto().toString()
              + " is no longer supported! Configurations must have a proto of at least "
              + ProtoVersionHistory.MIN_PROTO.toString()
              + ".");
    } else if (source.versionInfo().getProto().greater(ProtoVersionHistory.CURRENT)) {
      throw new ConfigurationParseException(
          "Proto "
              + source.versionInfo().getProto().toString()
              + " is not supported yet! Configurations must have a proto of no greater than "
              + ProtoVersionHistory.CURRENT.toString()
              + ".");
    }
  }

  /**
   * Create an instance of {@link S} using data from a source.
   *
   * @param source to construct the configuration from
   * @return an instance of {@link S} from the source
   * @throws ConfigurationParseException if the configuration is malformed or contains invalid base
   *     data
   */
  public abstract S construct(WorldSource source) throws ConfigurationParseException;

  /**
   * Attempt to create a generic {@link Node} from the {@link WorldSource#getConfig()} file. This
   * will make sure the file is syntactically correct, but makes no guarantees about proper
   * semantics. If the source's config format is not supported, an error will be thrown. This is the
   * last step in the parsing process before the generic node configuration system is used
   * exclusively. Anything after this should have no idea what format the config was written in,
   * since everything is handled by the generic configuration system. This makes adding more
   * configuration types down the line a lot easier.
   *
   * @param source to pull configuration data from
   * @return a generic node from the source configuration data
   * @throws ConfigurationParseException if the configuration document is invalid
   */
  public Node createNode(WorldSource source) throws ConfigurationParseException {
    try {
      String mimeType = URLConnection.guessContentTypeFromStream(source.getConfig());
      if (mimeType == null) {
        mimeType = "application/xml";
      }
      switch (mimeType) {
        case "application/xml":
          return fromXML(source);
        default:
          throw new RuntimeException(
              "File format for " + source.getName() + "'s config is not supported!");
      }
    } catch (Exception e) {
      throw new ConfigurationParseException(
          "An exception occurred while parsing config file at '"
              + source.getName()
              + '\''
              + ": "
              + e.getMessage(),
          e);
    }
  }

  /**
   * Handle inclusions for a document type.
   *
   * @param source of the file structure
   * @param baseDocument containing the raw configuration data, before it is wrapped in a {@link
   *     Node}
   * @param builder used to create the configuration structure from the file
   * @throws ConfigurationParseException if any inclusion definition contains errors
   */
  private void parseInclusions(WorldSource source, Serializable baseDocument, Object builder)
      throws ConfigurationParseException {
    IncludeProcessor processor;
    if (baseDocument instanceof Document) {
      processor = new XmlIncludeProcessor(source, (Document) baseDocument, (SAXBuilder) builder);
    } else {
      throw new RuntimeException(
          "Document format of " + source.getName() + "'s config does not support inclusions!");
    }

    while (processor.shouldProcess()) processor.process();
  }

  private Node fromXML(WorldSource source) throws Exception {
    final SAXBuilder sax = new SAXBuilder();
    sax.setJDOMFactory(new LocatedJDOMFactory());

    final Document document;

    try (final InputStream is = source.getConfig()) {
      document = sax.build(is);
    }

    parseInclusions(source, document, sax);

    return new XmlElement(document.getRootElement());
  }
}
