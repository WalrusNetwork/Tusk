package network.walrus.utils.bukkit.visual.renderable;

import org.bukkit.entity.Player;

/**
 * A {@link Renderable} which will return different data based on each player.
 *
 * @author Austin Mayes
 */
public abstract class TargetedRenderable extends Renderable {

  /** @see Renderable#Renderable(String, byte) */
  public TargetedRenderable(String id, byte maxLines) {
    super(id, maxLines);
  }

  /** @see Renderable#Renderable(String) */
  public TargetedRenderable(String id) {
    super(id);
  }

  /**
   * Get the text that should be shown to this specific player.
   *
   * @param player to render the text for
   * @return the text to display
   */
  public abstract String[] text(Player player);
}
