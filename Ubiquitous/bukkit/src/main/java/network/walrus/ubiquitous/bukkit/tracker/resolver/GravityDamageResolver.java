package network.walrus.ubiquitous.bukkit.tracker.resolver;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.GravityDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import network.walrus.ubiquitous.bukkit.tracker.trackers.base.gravity.Fall;
import network.walrus.ubiquitous.bukkit.tracker.trackers.base.gravity.SimpleGravityKillTracker;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Resolver which resolves the information about the cause of a damage caused by gravity.
 *
 * @author Overcast Network
 */
public class GravityDamageResolver implements DamageResolver {

  private final @Nonnull SimpleGravityKillTracker tracker;

  /**
   * Constructor.
   *
   * @param tracker used to get fall information from
   */
  public GravityDamageResolver(@Nonnull SimpleGravityKillTracker tracker) {
    Preconditions.checkNotNull(tracker, "tracker");
    this.tracker = tracker;
  }

  public @Nullable DamageInfo resolve(
      @Nonnull LivingEntity entity,
      @Nonnull Lifetime lifetime,
      @Nonnull EntityDamageEvent damageEvent) {
    if (!(entity instanceof Player)) {
      return null;
    }
    Player victim = (Player) entity;
    Fall fall = this.tracker.getCausingFall(victim, damageEvent.getCause());
    if (fall != null) {
      return new GravityDamageInfo(fall.attacker, fall.cause, fall.from, fall.whereOnGround);
    } else {
      return null;
    }
  }
}
