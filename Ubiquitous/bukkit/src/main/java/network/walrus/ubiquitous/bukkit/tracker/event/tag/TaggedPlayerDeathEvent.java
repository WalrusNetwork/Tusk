package network.walrus.ubiquitous.bukkit.tracker.event.tag;

import java.time.Instant;
import java.util.List;
import network.walrus.ubiquitous.bukkit.tracker.event.entity.EntityDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import network.walrus.ubiquitous.bukkit.tracker.tag.CombatLoggerState;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Event which is fired when a {@link Player} is killed.
 *
 * @author Overcast Network
 */
public class TaggedPlayerDeathEvent extends EntityDeathEvent {

  private final CombatLoggerState player;

  /**
   * Constructor.
   *
   * @param player which is dying
   * @param location that the death occurred
   * @param lifetime of the entity being killed
   * @param time that the death occurred
   * @param drops dropped by the entity
   * @param droppedExp XP dropped by the entity
   */
  public TaggedPlayerDeathEvent(
      CombatLoggerState player,
      Location location,
      Lifetime lifetime,
      Instant time,
      List<ItemStack> drops,
      int droppedExp) {
    super(player, location, lifetime, time, drops, droppedExp);
    this.player = player;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public HandlerList getHandlers() {
    return handlers;
  }

  public CombatLoggerState getPlayer() {
    return player;
  }
}
