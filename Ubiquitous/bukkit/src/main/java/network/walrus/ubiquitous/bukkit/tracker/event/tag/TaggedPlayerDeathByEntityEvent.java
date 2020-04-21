package network.walrus.ubiquitous.bukkit.tracker.event.tag;

import java.time.Instant;
import java.util.List;
import network.walrus.ubiquitous.bukkit.tracker.event.entity.EntityDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import network.walrus.ubiquitous.bukkit.tracker.tag.CombatLoggerState;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Event which is fired when a {@link Player} is killed by a {@link T}.
 *
 * @param <T> type of entity that killed the player
 * @author Overcast Network
 */
public class TaggedPlayerDeathByEntityEvent<T extends LivingEntity> extends TaggedPlayerDeathEvent {

  private final T cause;

  /**
   * Constructor.
   *
   * @param player which is dying
   * @param location that the death occurred
   * @param lifetime of the entity being killed
   * @param time that the death occurred
   * @param drops dropped by the entity
   * @param droppedExp XP dropped by the entity
   * @param cause of the damage
   */
  public TaggedPlayerDeathByEntityEvent(
      CombatLoggerState player,
      Location location,
      Lifetime lifetime,
      Instant time,
      List<ItemStack> drops,
      int droppedExp,
      T cause) {
    super(player, location, lifetime, time, drops, droppedExp);
    this.cause = cause;
  }

  public static HandlerList getHandlerList() {
    return EntityDeathEvent.handlers;
  }

  public T getCause() {
    return this.cause;
  }

  public HandlerList getHandlers() {
    return EntityDeathEvent.handlers;
  }
}
