package network.walrus.ubiquitous.bukkit.tracker.event.entity;

import java.time.Instant;
import java.util.List;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Event which is fired when an {@link Entity} is killed by a player.
 *
 * @author Overcast Network
 */
public class EntityDeathByPlayerEvent extends EntityDeathByEntityEvent<Player> {

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
  public EntityDeathByPlayerEvent(
      Entity entity,
      Location location,
      Lifetime lifetime,
      Instant time,
      List<ItemStack> drops,
      int droppedExp,
      Player cause) {
    super(entity, location, lifetime, time, drops, droppedExp, cause);
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public HandlerList getHandlers() {
    return handlers;
  }
}
