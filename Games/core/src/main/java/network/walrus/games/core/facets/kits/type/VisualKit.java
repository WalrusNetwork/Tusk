package network.walrus.games.core.facets.kits.type;

import javax.annotation.Nullable;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.utils.core.math.PreparedNumberAction;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

/**
 * Kit used to change what a player personally sees.
 *
 * @author Avicus Network
 */
public class VisualKit extends Kit {

  // Player weather
  private final WeatherType weather;
  // Player time
  private final PreparedNumberAction time;

  /**
   * @param force if items should be placed in occupied slots
   * @param parent kit to inherit attributes from
   * @param weather that the player should see
   * @param time to modify the player's personal time by
   */
  public VisualKit(
      boolean force, @Nullable Kit parent, WeatherType weather, PreparedNumberAction time) {
    super(force, parent);
    this.weather = weather;
    this.time = time;
  }

  @Override
  public void give(Player player, boolean force) {
    // Weather
    if (this.weather != null) {
      player.setPlayerWeather(this.weather);
    }

    // Time
    if (this.time != null) {
      int current = ((Long) player.getPlayerTime()).intValue();
      player.setPlayerTime(this.time.perform(current), false);
    }
  }
}
