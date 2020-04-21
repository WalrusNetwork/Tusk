package network.walrus.ubiquitous.bukkit.boss;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;

/**
 * This class allows legacy clients to have more than one active {@link BossBar} by rotating between
 * all active bossbars.
 *
 * @author kashike
 */
public final class LegacyBossBarContext implements Runnable {

  /** The state map. */
  private final Map<UUID, State> states = Maps.newHashMap();

  /**
   * Add a boss bar to the player's state.
   *
   * @param player the player
   * @param bar the boss bar
   */
  void add(Player player, LegacyBossBar bar) {
    this.states.computeIfAbsent(player.getUniqueId(), id -> new State()).bars.add(bar);
  }

  /**
   * Remove a boss bar from the player's state.
   *
   * @param player the player
   * @param bar the boss bar
   */
  void remove(Player player, LegacyBossBar bar) {
    this.states.computeIfAbsent(player.getUniqueId(), id -> new State()).bars.remove(bar);
  }

  /**
   * Remove a player from this context.
   *
   * @param player the player
   */
  void remove(Player player) {
    this.states.remove(player.getUniqueId());
  }

  /**
   * Refresh an active boss bar if the state matches.
   *
   * @param player the player
   * @param current the active boss bar
   */
  void refresh(Player player, LegacyBossBar current) {
    this.states.computeIfAbsent(player.getUniqueId(), id -> new State()).refresh(current);
  }

  @Override
  public void run() {
    for (State entry : this.states.values()) {
      entry.shift();
    }
  }

  /** A player boss bar context state. */
  private static class State {

    /** A list of all boss bars that the player can view. */
    private final List<LegacyBossBar> bars = Lists.newCopyOnWriteArrayList();
    /** The current boss bar index. */
    private int index = 0;

    /** Shift between boss bars. */
    private void shift() {
      // Despawn the existing bar first
      @Nullable final LegacyBossBar oldBar = this.get();
      if (oldBar != null) {
        oldBar.despawn();
      }

      // Move down the list
      this.move();

      // Spawn the new boss bar
      @Nullable final LegacyBossBar bar = this.get();
      if (bar != null) {
        bar.spawn();
      }
    }

    /** Move this state to the next entry in the list. */
    private void move() {
      this.index++;
      if (this.index >= this.bars.size()) {
        this.index = 0;
      }
    }

    /**
     * Refresh the active boss bar if this state matches.
     *
     * @param current the active boss bar
     */
    void refresh(LegacyBossBar current) {
      if (this.get() == current) {
        current.despawn();
        current.spawn();
      }
    }

    /**
     * Gets the current boss bar.
     *
     * @return the boss bar
     */
    @Nullable
    private LegacyBossBar get() {
      final int size = this.bars.size();
      if (size == 0) {
        return null;
      }

      this.adjust(size);

      return this.bars.get(this.index);
    }

    private void adjust(final int size) {
      if (this.index == 1 && size == 1) {
        this.index = 0;
      }
    }
  }
}
