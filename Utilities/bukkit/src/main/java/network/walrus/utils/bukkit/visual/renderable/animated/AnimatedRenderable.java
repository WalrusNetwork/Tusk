package network.walrus.utils.bukkit.visual.renderable.animated;

/**
 * An renderable that will be re-rendered after a specified {@link #delay()} and has a specific
 * number of {@link #maxFrames()}.
 *
 * @author Austin Mayes
 */
public interface AnimatedRenderable {

  /**
   * The total number of frames in this animation.
   *
   * @return total number of frames
   */
  int maxFrames();

  /**
   * The delay (in minecraft ticks) between each frame.
   *
   * @return delay between frames
   */
  short delay();

  /**
   * The current frame in the animation cycle.
   *
   * @return current frame
   */
  int currentFrame();

  /**
   * Add 1 to the {@link #currentFrame()}, or if the next frame is above the {@link #maxFrames()},
   * go back to 0.
   */
  void incrementFrame();
}
