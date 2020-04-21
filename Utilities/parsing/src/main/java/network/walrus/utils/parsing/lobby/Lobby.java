package network.walrus.utils.parsing.lobby;

import java.util.logging.Logger;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.world.PlayerContainer;
import network.walrus.utils.parsing.world.WorldProvider;
import network.walrus.utils.parsing.world.config.FacetConfigurationSource;
import org.bukkit.plugin.Plugin;

/**
 * Class which holds all of the parsed {@link Facet}s for the lobby.
 *
 * <p>The base system this uses has logic to support multiple instances of these running in
 * parallel, as well as the option to have more than one of these be created in a row. We don't use
 * any of this for lobbies so all implementors are safe to assume that this object lives as long as
 * the parser does.
 *
 * @author Austin Mayes
 */
public class Lobby extends FacetHolder {

  /**
   * Constructor.
   *
   * @param source that the lobby was created from
   * @param worldProvider used to perform world configuration
   */
  public Lobby(
      Plugin plugin,
      Logger logger,
      FacetConfigurationSource source,
      WorldProvider<? extends PlayerContainer> worldProvider) {
    super(plugin, logger, source, worldProvider);
  }
}
