package network.walrus.ubiquitous.bukkit.tracker.event.entity;

import java.time.Instant;
import java.util.List;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Event which is fired when an {@link Entity} is killed.
 *
 * @author Overcast Network
 */
public class EntityDeathEvent extends EntityEvent {

  protected static final HandlerList handlers = new HandlerList();
  private final Location location;
  private final Lifetime lifetime;
  private final Instant time;
  private List<ItemStack> drops;
  private int droppedExp;

  /**
   * Constructor.
   *
   * @param entity which is dying
   * @param location that the death occurred
   * @param lifetime of the entity being killed
   * @param time that the death occurred
   * @param drops dropped by the entity
   * @param droppedExp XP dropped by the entity
   */
  public EntityDeathEvent(
      Entity entity,
      Location location,
      Lifetime lifetime,
      Instant time,
      List<ItemStack> drops,
      int droppedExp) {
    super(entity);
    this.entity = entity;
    this.location = location;
    this.lifetime = lifetime;
    this.time = time;
    this.drops = drops;
    this.droppedExp = droppedExp;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public HandlerList getHandlers() {
    return handlers;
  }

  public Location getLocation() {
    return location;
  }

  public Lifetime getLifetime() {
    return lifetime;
  }

  public Instant getTime() {
    return time;
  }

  public List<ItemStack> getDrops() {
    return drops;
  }

  public void setDrops(List<ItemStack> drops) {
    this.drops = drops;
  }

  public int getDroppedExp() {
    return droppedExp;
  }

  public void setDroppedExp(int droppedExp) {
    this.droppedExp = droppedExp;
  }
}
