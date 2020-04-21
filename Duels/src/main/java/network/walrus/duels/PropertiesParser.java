package network.walrus.duels;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.special.EverywhereRegion;
import network.walrus.utils.bukkit.region.special.NowhereRegion;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.xml.XmlElement;
import network.walrus.utils.core.parse.CoreParserRegistry;
import network.walrus.utils.core.registry.Registry;
import network.walrus.utils.parsing.facet.facets.region.RegionsFacetParser;
import network.walrus.utils.parsing.world.config.ConfigurationParseException;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;

/**
 * Parses {@link ArenaProperties} from {@link Node}s.
 *
 * @author Austin Mayes
 */
class PropertiesParser {

  private final File worldFile;

  PropertiesParser(File worldFile) {
    this.worldFile = worldFile;
  }

  Multimap<ArenaType, ArenaProperties> parse() throws Exception {
    Multimap<ArenaType, ArenaProperties> properties = HashMultimap.create();

    RegionsFacetParser parser = new RegionsFacetParser();
    File config = new File(worldFile, "config.xml");
    Node<?> node = createNode(new FileInputStream(config));
    Registry registry = new Registry();
    parseRegions(node, registry, parser);

    for (Node arena : node.children("arena")) {
      ArenaType type =
          CoreParserRegistry.ofEnum(Type.class).parseRequired(arena.attribute("type")).type;
      BoundedRegion arenaArea =
          parser.resolveRequiredRegionAs(
              BoundedRegion.class, registry, arena.attribute("area"), arena.child("area"));
      BoundedRegion specSpawn =
          parser.resolveRequiredRegionAs(
              BoundedRegion.class,
              registry,
              arena.attribute("spec-spawn"),
              arena.child("spec-spawn"));
      BoundedRegion spawn1 =
          parser.resolveRequiredRegionAs(
              BoundedRegion.class,
              registry,
              arena.attribute("spawn-one"),
              arena.child("spawn-one"));
      BoundedRegion spawn2 =
          parser.resolveRequiredRegionAs(
              BoundedRegion.class,
              registry,
              arena.attribute("spawn-two"),
              arena.child("spawn-two"));
      ArenaProperties props = new ArenaProperties(type, arenaArea, specSpawn, spawn1, spawn2);
      properties.put(type, props);
    }
    return properties;
  }

  private void parseRegions(Node node, Registry registry, RegionsFacetParser parser) {
    registry.add("everywhere", new EverywhereRegion());
    registry.add("nowhere", new NowhereRegion());

    if (node.hasChild("regions")) {
      for (Node<?> regionNode : ((Node<?>) node).children("regions")) {
        for (Node<?> c : regionNode.children()) {
          parser.parseRegion(c, registry);
        }
      }
    }
  }

  private Node createNode(FileInputStream file) throws ConfigurationParseException {
    try {
      String mimeType = URLConnection.guessContentTypeFromStream(file);
      if (mimeType == null) {
        mimeType = "application/xml";
      }
      switch (mimeType) {
        case "application/xml":
          return fromXML(file);
        default:
          throw new RuntimeException("File format for config is not supported!");
      }
    } catch (Exception e) {
      throw new ConfigurationParseException("An exception occurred while parsing config file.", e);
    }
  }

  private Node fromXML(FileInputStream source) throws Exception {
    final SAXBuilder sax = new SAXBuilder();
    sax.setJDOMFactory(new LocatedJDOMFactory());

    final Document document;

    try (final InputStream is = source) {
      document = sax.build(is);
    }

    return new XmlElement(document.getRootElement());
  }

  enum Type {
    ;
    private final ArenaType type;

    Type(ArenaType type) {
      this.type = type;
    }
  }
}
