package network.walrus.ubiquitous.bukkit.tracker.api;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import network.walrus.ubiquitous.bukkit.tracker.Damage;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Provides convenient static API calls for other plugins to use.
 *
 * @author Overcast Network
 */
public class SimpleDamageAPI implements DamageAPI {

  private final DamageAPIHelper apiHelper;

  /**
   * Constructor.
   *
   * @param apiHelper used for various API calls
   */
  public SimpleDamageAPI(DamageAPIHelper apiHelper) {
    this.apiHelper = apiHelper;
  }

  @Override
  public @Nonnull Damage inflictDamage(
      @Nonnull LivingEntity entity, int damage, @Nonnull DamageInfo info) {
    Preconditions.checkNotNull(entity, "living entity");
    Preconditions.checkArgument(damage >= 0, "damage must be greater than or equal to zero");
    Preconditions.checkNotNull(info, "damage info");

    EntityDamageEvent event = new EntityDamageEvent(entity, DamageCause.CUSTOM, damage);
    apiHelper.setEventDamageInfo(event, info);

    Bukkit.getPluginManager().callEvent(event);

    if (event.isCancelled()) {
      return null;
    }

    entity.damage(event.getDamage());

    apiHelper.setEventDamageInfo(event, null);

    return apiHelper.getOurEvent(event).toDamageObject();
  }
}
