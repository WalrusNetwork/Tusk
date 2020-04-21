package network.walrus.games.uhc;

import java.util.logging.Logger;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.parsing.world.ActionlessWorldProvider;
import network.walrus.utils.parsing.world.config.FacetConfigurationSource;
import org.bukkit.plugin.Plugin;

/**
 * A single round of a game of UHC.
 *
 * @author Austin Mayes
 */
public class UHCRound extends GameRound {

  /**
   * @param plugin to register commands and listeners with
   * @param logger to log errors and info to
   * @param source which this holder is for
   * @param world to put plays into
   */
  public UHCRound(Plugin plugin, Logger logger, FacetConfigurationSource source, UHCWorld world) {
    super(null, plugin, logger, source, new ActionlessWorldProvider<>(world));
  }
}
