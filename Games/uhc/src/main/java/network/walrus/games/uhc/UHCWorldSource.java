package network.walrus.games.uhc;

import java.util.List;
import network.walrus.games.core.GamesPlugin;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.stage.Stage;
import network.walrus.utils.core.versioning.VersionInfo;
import network.walrus.utils.parsing.world.config.FacetConfigurationSource;
import network.walrus.utils.parsing.world.library.WorldSource;

/**
 * Source of UHC configuration data used for all worlds.
 *
 * @author Austin Mayes
 */
public class UHCWorldSource implements FacetConfigurationSource {

  private final WorldSource source;
  private final Node parent;
  private final VersionInfo info;
  private final List<LocalizedConfigurationProperty> rules;

  /**
   * @param source containing the config file
   * @param parent node of the config
   * @param info about the uhc
   * @param rules of the uhc
   */
  public UHCWorldSource(
      WorldSource source,
      Node parent,
      VersionInfo info,
      List<LocalizedConfigurationProperty> rules) {
    this.source = source;
    this.parent = parent;
    this.info = info;
    this.rules = rules;
  }

  @Override
  public Node parent() {
    return this.parent;
  }

  @Override
  public boolean playable() {
    return true;
  }

  @Override
  public VersionInfo versionInfo() {
    return this.info;
  }

  @Override
  public Stage environment() {
    return GamesPlugin.getStage();
  }

  @Override
  public String name() {
    return "UHC";
  }

  @Override
  public String slugify() {
    return "uhc";
  }

  @Override
  public WorldSource source() {
    return this.source;
  }

  /** @return rules of the UHC */
  public List<LocalizedConfigurationProperty> rules() {
    return rules;
  }
}
