package network.walrus.utils.bukkit.color;

import java.util.Optional;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

/**
 * A generic interface to get a color based on the player who is viewing it.
 *
 * @author Avicus Network
 */
public interface ColorProvider {

  /**
   * Get a specific {@link DyeColor} based on the context of the supplied player, or a generic
   * result if a player is not provided to the method.
   *
   * @param player to get the color for, if one can be determined
   * @return the color generated for the specific player, or a generic result if one cannot be
   *     determined
   */
  DyeColor getDyeColor(Optional<Player> player);

  /**
   * Get a specific {@link Color} based on the context of the supplied player, or a generic result
   * if a player is not provided to the method.
   *
   * @param player to get the color for, if one can be determined
   * @return the color generated for the specific player, or a generic result if one cannot be
   *     determined
   */
  Color getColor(Optional<Player> player);
}
