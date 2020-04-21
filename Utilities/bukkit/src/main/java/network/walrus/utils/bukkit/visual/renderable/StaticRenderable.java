package network.walrus.utils.bukkit.visual.renderable;

import network.walrus.utils.core.translation.Localizable;

/**
 * A {@link Renderable} with data that is not player specific. To clarify: technically the data is
 * specific to each player due to localization, but the base {@link Localizable} is the same across
 * all players.
 *
 * @author Austin Mayes
 */
public abstract class StaticRenderable extends Renderable {

  /** @see Renderable#Renderable(String, byte) */
  public StaticRenderable(String id, byte maxLines) {
    super(id, maxLines);
  }

  /** @see Renderable#Renderable(String) */
  public StaticRenderable(String id) {
    super(id);
  }

  /**
   * The localizable text that this renderable should display.
   *
   * @return the text to display
   */
  public abstract Localizable[] text();
}
