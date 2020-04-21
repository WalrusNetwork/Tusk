package network.walrus.utils.bukkit.color;

import java.util.Optional;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

/**
 * Returns the same color regardless of the player supplied.
 *
 * @author Avicus Network
 */
public class StaticColorProvider implements ColorProvider {

  private final Color color;

  /**
   * Constructor.
   *
   * @param color which will always be provided
   */
  public StaticColorProvider(Color color) {
    this.color = color;
  }

  @Override
  public DyeColor getDyeColor(Optional<Player> player) {
    return DyeColor.getByColor(color);
  }

  @Override
  public Color getColor(Optional<Player> player) {
    return this.color;
  }
}
