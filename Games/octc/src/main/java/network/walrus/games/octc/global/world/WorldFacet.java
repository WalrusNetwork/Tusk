package network.walrus.games.octc.global.world;

import java.util.Map;
import java.util.Map.Entry;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.FacetLoadException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * Facet responsible for managing all world configuration options that can be changed by map
 * creators.
 *
 * @author Austin Mayes
 */
@SuppressWarnings("JavaDoc")
public class WorldFacet extends Facet implements Listener {

  private final FacetHolder holder;
  private final boolean lockTime;
  private final Map<GameRule, String> gameRules;

  /**
   * @param holder which this facet is managing worlds for
   * @param lockTime if time should be frozen in the world
   * @param gameRules map of game rule -> value which should be applied to all worlds for the round
   */
  public WorldFacet(FacetHolder holder, boolean lockTime, Map<GameRule, String> gameRules) {
    this.holder = holder;
    this.lockTime = lockTime;
    this.gameRules = gameRules;
  }

  @Override
  public void load() throws FacetLoadException {
    if (lockTime) {
      gameRules.put(GameRule.doDaylightCycle, "false");
    }

    holder
        .getContainer()
        .actOnAllWorlds(
            w -> {
              for (Entry<GameRule, String> rule : this.gameRules.entrySet()) {
                GameRule r = rule.getKey();
                String v = rule.getValue();
                w.setGameRuleValue(r.name(), v);
              }
            });
  }

  @EventHandler
  public void onWeatherChange(WeatherChangeEvent event) {
    event.setCancelled(true);
  }

  @EventHandler
  public void onThunderChange(ThunderChangeEvent event) {
    event.setCancelled(true);
  }
}
