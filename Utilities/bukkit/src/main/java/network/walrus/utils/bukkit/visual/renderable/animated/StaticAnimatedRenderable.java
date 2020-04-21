package network.walrus.utils.bukkit.visual.renderable.animated;

import java.util.concurrent.atomic.AtomicInteger;
import network.walrus.utils.bukkit.visual.renderable.Renderable;
import network.walrus.utils.bukkit.visual.renderable.StaticRenderable;
import network.walrus.utils.core.translation.Localizable;

/**
 * A {@link AnimatedRenderable} that will show the same text to all players.
 *
 * @author Austin Mayes
 */
public abstract class StaticAnimatedRenderable extends StaticRenderable
    implements AnimatedRenderable {

  /** Current frame in the loop. */
  private final AtomicInteger frame = new AtomicInteger();

  /** @see Renderable#Renderable(String, byte) */
  public StaticAnimatedRenderable(String id, byte maxLines) {
    super(id, maxLines);
  }

  /** @see Renderable#Renderable(String) */
  public StaticAnimatedRenderable(String id) {
    super(id);
  }

  /**
   * Get the text to render for the specified frame in the animation cycle.
   *
   * @param frame in the cycle
   * @return the text to render for the frame
   */
  public abstract Localizable[] text(int frame);

  @Override
  public Localizable[] text() {
    return text(currentFrame());
  }

  @Override
  public int currentFrame() {
    return this.frame.intValue();
  }

  @Override
  public void incrementFrame() {
    if (currentFrame() + 1 >= maxFrames()) {
      this.frame.set(0);
    } else {
      this.frame.incrementAndGet();
    }
  }
}
