package network.walrus.utils.bukkit.visual.renderable.animated;

import java.util.concurrent.atomic.AtomicInteger;
import network.walrus.utils.bukkit.visual.renderable.Renderable;
import network.walrus.utils.bukkit.visual.renderable.TargetedRenderable;
import org.bukkit.entity.Player;

/**
 * A {@link AnimatedRenderable} that will show different text to each players.
 *
 * @author Austin Mayes
 */
public abstract class TargetedAnimatedRenderable extends TargetedRenderable
    implements AnimatedRenderable {

  /** Current frame in the loop. */
  private final AtomicInteger frame = new AtomicInteger();

  /** @see Renderable#Renderable(String, byte) */
  public TargetedAnimatedRenderable(String id, byte maxLines) {
    super(id, maxLines);
  }

  /** @see Renderable#Renderable(String) */
  public TargetedAnimatedRenderable(String id) {
    super(id);
  }

  /**
   * Get the text to render for the specified frame in the animation cycle for the specified player.
   *
   * @param frame in the cycle
   * @param player to render the text to
   * @return the text to render for the frame
   */
  public abstract String[] text(Player player, int frame);

  @Override
  public String[] text(Player player) {
    return text(player, currentFrame());
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
