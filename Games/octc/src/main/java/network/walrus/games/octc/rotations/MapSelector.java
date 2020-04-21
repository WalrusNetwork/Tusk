package network.walrus.games.octc.rotations;

import java.util.Random;
import javax.annotation.Nullable;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.octc.OCNGameManager;
import network.walrus.games.octc.OCNMap;
import network.walrus.ubiquitous.bukkit.settings.PlayerSettings;
import network.walrus.utils.parsing.world.config.ConfiguredWorldManager;
import org.bukkit.Bukkit;

/**
 * Class which selects the next map to play according to the specified strategy.
 *
 * @author Rafi Baum
 */
public class MapSelector {

  private final ConfiguredWorldManager<OCNMap> worlds;
  @Nullable private final MapSelectorStrategy mapSelectorStrategy;

  /**
   * Constructor.
   *
   * @param mode to use for selection
   * @param worlds list of loaded worlds
   */
  public MapSelector(MapSelectionMode mode, ConfiguredWorldManager<OCNMap> worlds) {
    this.worlds = worlds;
    PlayerSettings.register(VotingMapSelector.SHOW_VOTE_SETTING);

    switch (mode) {
      case RANDOM:
        mapSelectorStrategy = new RandomMapSelector(this);
        break;
      case VOTE:
        mapSelectorStrategy = new VotingMapSelector(this);
        OCNGameManager.instance.registerCommand(mapSelectorStrategy);
        break;
      default:
        mapSelectorStrategy = null;
    }
  }

  /** Selects the next map according to the specified strategy. */
  public void selectNextMap() {
    if (mapSelectorStrategy == null) {
      return;
    }

    if (worlds.getSources().size() < 1) {
      GamesPlugin.instance
          .mapLogger()
          .warning("Tried to select a map but there are no maps loaded!");
      return;
    } else if (worlds.getSources().size() == 1) {
      OCNGameManager.instance.setMap((OCNMap) worlds.getSources().get(0));
      return;
    }

    if (Bukkit.getOnlinePlayers().size() < 1) {
      // Select a random map if nobody's on
      OCNGameManager.instance.setMap(
          (OCNMap) worlds.getSources().get(new Random().nextInt(worlds.getSources().size())));
      return;
    }

    mapSelectorStrategy.selectMap();
  }

  /** Reloads map selectors with new maps. */
  public void reload() {
    if (mapSelectorStrategy != null) {
      mapSelectorStrategy.reload();
    }
  }

  ConfiguredWorldManager<OCNMap> worlds() {
    return worlds;
  }
}
