package network.walrus.ubiquitous.bukkit.tracker.api;

import javax.annotation.Nonnull;
import network.walrus.ubiquitous.bukkit.tracker.Damage;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * A simple API to allow direct damage to be inflicted on a player which will call the popper Bukkit
 * events and pass the data through the tracking chain as if the damage was natural.
 *
 * @author Overcast Network
 */
public interface DamageAPI {

  /**
   * Inflicts the given damage on an entity.
   *
   * <p>This method will call the appropriate damage method and fire an {@link EntityDamageEvent}.
   *
   * @param entity Entity to inflict damage upon
   * @param damage Amount of half-hearts of damage to inflict
   * @param info {@link DamageInfo} object that details the type of damage
   * @return the final {@link Damage} object (never null)
   * @throws NullPointerException if entity or info is null
   */
  @Nonnull
  Damage inflictDamage(@Nonnull LivingEntity entity, int damage, @Nonnull DamageInfo info);
}
