package network.walrus.ubiquitous.bukkit.tracker.event.entity;

import java.time.Instant;
import java.util.List;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Event which is fired when an {@link Entity} is killed by {@link T}.
 *
 * @param <T> type of entity which caused the damage
 * @author Overcast Network
 */
public class EntityDeathByEntityEvent<T extends LivingEntity> extends EntityDeathEvent {

  private final T cause;

  /**
   * Constructor.
   *
   * @param entity which is dying
   * @param location that the death occurred
   * @param lifetime of the entity being killed
   * @param time that the death occurred
   * @param drops dropped by the entity
   * @param droppedExp XP dropped by the entity
   * @param cause entity which caused the death
   */
  public EntityDeathByEntityEvent(
      Entity entity,
      Location location,
      Lifetime lifetime,
      Instant time,
      List<ItemStack> drops,
      int droppedExp,
      T cause) {
    super(entity, location, lifetime, time, drops, droppedExp);
    this.cause = cause;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public T getCause() {
    return this.cause;
  }

  public HandlerList getHandlers() {
    return handlers;
  }
}
