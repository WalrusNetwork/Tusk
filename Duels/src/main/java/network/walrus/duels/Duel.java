package network.walrus.duels;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeByPlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import org.bukkit.entity.Player;

/**
 * Base class for a dual between a group of people that happens inside of an enclosed space in the
 * same world as any number of other concurrent duels.
 *
 * @author Austin Mayes
 */
public abstract class Duel {

  private final List<Player> players = Lists.newArrayList();
  private final ArenaProperties properties;
  private final AtomicBoolean active = new AtomicBoolean(false);

  /** @param properties that define how this duel should work */
  public Duel(ArenaProperties properties) {
    this.properties = properties;
  }

  /** Enable this duel. */
  public void enable() {
    active.set(true);
  }

  /** Disable this duel. */
  public void reset() {
    players.clear();
    active.set(false);
  }

  /**
   * Spawn a player into this duel. This has the potential to be called multiple times per duel, if
   * the type supports multiple lives.
   */
  abstract void spawn(Player player);

  public List<Player> getPlayers() {
    return players;
  }

  /**
   * Assign a player to this duel.
   *
   * @param player to assign
   */
  public void assignPlayer(Player player) {
    this.players.add(player);
  }

  public ArenaProperties getProperties() {
    return properties;
  }

  public boolean isActive() {
    return active.get();
  }

  abstract void onDeath(PlayerDeathEvent event);

  abstract boolean onModify(BlockChangeByPlayerEvent event);
}
