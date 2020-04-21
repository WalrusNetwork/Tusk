package network.walrus.games.uhc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.parse.SimpleParser;
import network.walrus.utils.core.stage.Stage;
import network.walrus.utils.core.versioning.Version;
import network.walrus.utils.core.versioning.VersionInfo;
import network.walrus.utils.parsing.facet.parse.GlobalParser;
import network.walrus.utils.parsing.world.config.ConfigurationParseException;
import network.walrus.utils.parsing.world.library.WorldSource;

/**
 * Factory used to create {@link UHCWorldSource}s.
 *
 * @author Austin Mayes
 */
public class UHCWorldFactory extends GlobalParser<UHCWorldSource> {

  /** @param logger used to log parsing errors and info to */
  public UHCWorldFactory(Logger logger) {
    super(logger);
  }

  @Override
  public UHCWorldSource parseInternal(UHCWorldSource source) throws ConfigurationParseException {
    return source;
  }

  @Override
  public UHCWorldSource construct(WorldSource source) throws ConfigurationParseException {
    Node<?> node = createNode(source);
    SimpleParser<Version> versionParser = BukkitParserRegistry.versionParser();
    Version proto = versionParser.parseRequired(node.attribute("proto"));
    Version version = new Version(1, 0, 0);
    Stage stage =
        BukkitParserRegistry.ofEnum(Stage.class)
            .parse(node.attribute("stage"))
            .orElse(Stage.PRODUCTION);
    VersionInfo info =
        new VersionInfo() {
          @Override
          public Version getProto() {
            return proto;
          }

          @Override
          public Version getVersion() {
            return version;
          }

          @Override
          public Stage stage() {
            return stage;
          }
        };
    List<LocalizedConfigurationProperty> rules = new ArrayList<>();
    for (Node<?> n : node.childRequired("rules").children()) {
      LocalizedConfigurationProperty localizedConfigurationProperty =
          BukkitParserRegistry.localizedPropertyParser().parseRequired(n.text());
      rules.add(localizedConfigurationProperty);
    }
    return new UHCWorldSource(source, node, info, rules);
  }
}
