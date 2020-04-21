package network.walrus.ubiquitous.bukkit.tracker.manager;

import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.trackers.Tracker;
import org.bukkit.World;

/**
 * Manages all references to {@link Tracker}s.
 *
 * @author Overcast Network
 */
public interface TrackerManager {

  Set<Tracker> getTrackers();

  /**
   * Determine if the manager contains a tracker instance for a specific class
   *
   * @param trackerClass to check for existence
   * @return if the manager contains a tracker instance for the specified class
   */
  boolean hasTracker(@Nonnull Class<? extends Tracker> trackerClass);

  /**
   * Get an instance of {@link T} based on a class identifier.
   *
   * @param trackerClass to get an instance for
   * @param <T> type of tracker
   * @return a tracker instance for the supplied class
   */
  @Nullable
  <T extends Tracker> T getTracker(@Nonnull Class<T> trackerClass);

  /**
   * Add a tracker instance to the manager which can be referenced by a class.
   *
   * @param trackerClass to reference the tracker by
   * @param tracker to add
   * @param <T> type of tracker
   * @return tracker which was previously set
   */
  @Nullable
  <T extends Tracker> T setTracker(@Nonnull Class<T> trackerClass, @Nullable T tracker);

  /**
   * Remove a tracker from the manager.
   *
   * @param trackerClass class to clear
   * @param <T> type of tracker
   * @return tracker which was cleared
   */
  @Nullable
  <T extends Tracker> T clearTracker(@Nonnull Class<T> trackerClass);

  /**
   * Remove a tracker from the manager. A tracker lookup will be performed using the base tracker
   * class, and if the found instance is implementable by trackerImplClass, the tracker will be
   * removed.
   *
   * @param trackerClass to search for
   * @param trackerImplClass to check that the found tracker implements
   * @param <T> type of tracker
   * @return tracker which was cleared
   */
  @Nullable
  <T extends Tracker> T clearTracker(
      @Nonnull Class<T> trackerClass, @Nonnull Class<? extends T> trackerImplClass);

  /**
   * Remove all tracker data for a world.
   *
   * @param world to remove trackers for
   */
  void clearTrackers(@Nonnull World world);
}
