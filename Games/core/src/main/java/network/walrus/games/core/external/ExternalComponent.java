package network.walrus.games.core.external;

import network.walrus.games.core.GamesPlugin;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Class to represent a jar that is loaded externally.
 *
 * @author Avicus Network
 */
public abstract class ExternalComponent {

  private final GamesPlugin plugin;

  /**
   * Constructor.
   *
   * @param plugin which owns the facet
   */
  public ExternalComponent(GamesPlugin plugin) {
    this.plugin = plugin;
  }

  /** Called when main bukkit plugin is enabled. */
  public void onEnable() {}

  /** Called when main bukkit plugin is disabled. */
  public void onDisable() {}

  /**
   * Load in config data specific to the component from the main config.
   *
   * @param section containing all configuration data for the component
   */
  public void loadConfig(ConfigurationSection section) {}

  public GamesPlugin getPlugin() {
    return plugin;
  }
}
