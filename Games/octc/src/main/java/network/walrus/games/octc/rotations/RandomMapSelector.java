package network.walrus.games.octc.rotations;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import network.walrus.games.octc.OCNGameManager;
import network.walrus.games.octc.OCNMap;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.utils.parsing.world.Sourced;

/**
 * Map selection strategy which randomly picks a map from the pool.
 *
 * @author Rafi Baum
 */
public class RandomMapSelector implements MapSelectorStrategy {

  private static final Random RANDOM = new Random();
  private final MapSelector mapSelector;
  private final Duration switchTime;

  /**
   * Constructor.
   *
   * @param mapSelector
   */
  RandomMapSelector(MapSelector mapSelector) {
    this.mapSelector = mapSelector;
    this.switchTime =
        Duration.ofSeconds(
            OCNGameManager.instance.getConfig().getLong("maps.random.switch-time", 10));
  }

  @Override
  public void selectMap() {
    List<Sourced> worlds = mapSelector.worlds().getSources();

    OCNMap currentMap = null;
    if (OCNGameManager.instance.getCurrentMatch() != null) {
      currentMap = (OCNMap) OCNGameManager.instance.getCurrentMatch().map();
    }

    OCNMap map;
    do {
      map = (OCNMap) worlds.get(RANDOM.nextInt(worlds.size()));
    } while (!map.equals(currentMap));
    UbiquitousBukkitPlugin.getInstance()
        .getCountdownManager()
        .start(new MapCountdown(switchTime, map));
  }
}
