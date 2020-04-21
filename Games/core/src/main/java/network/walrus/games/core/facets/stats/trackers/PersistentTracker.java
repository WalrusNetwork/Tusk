package network.walrus.games.core.facets.stats.trackers;

/**
 * Class which extends {@link Tracker} and specifies that this metric should not be reset when a
 * player changes groups.
 *
 * @author Rafi Baum
 */
public abstract class PersistentTracker<T> extends Tracker<T> {}
