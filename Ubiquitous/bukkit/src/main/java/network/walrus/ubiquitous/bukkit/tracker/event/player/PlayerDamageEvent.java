package network.walrus.ubiquitous.bukkit.tracker.event.player;

import java.time.Instant;
import javax.annotation.Nonnull;
import network.walrus.ubiquitous.bukkit.tracker.event.entity.EntityDamageEvent;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a player undergoes some type of damage.
 *
 * @author Overcast Network
 */
public class PlayerDamageEvent extends EntityDamageEvent<Player> {

  /**
   * Constructor.
   *
   * @param player which was damaged
   * @param lifetime of the entity being damaged
   * @param damage amount of damage dealt
   * @param location that the damage occurred
   * @param time that the damage occurred
   * @param info describing the damage situation
   */
  public PlayerDamageEvent(
      @Nonnull Player player,
      @Nonnull Lifetime lifetime,
      int damage,
      @Nonnull Location location,
      @Nonnull Instant time,
      @Nonnull DamageInfo info) {
    super(player, lifetime, damage, location, time, info);
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
