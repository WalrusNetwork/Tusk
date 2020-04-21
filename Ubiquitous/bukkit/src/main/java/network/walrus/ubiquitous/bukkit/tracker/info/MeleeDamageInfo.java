package network.walrus.ubiquitous.bukkit.tracker.info;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

/**
 * Represents damage inflicted by hand-to-hand combat between entities.
 *
 * @author Overcast Network
 */
public interface MeleeDamageInfo extends DamageInfo {

  /**
   * Gets the entity that directly attacked the victim.
   *
   * <p>Note: this returns the same as {@link #getResolvedDamager()}.
   *
   * @return Entity who attacked
   */
  @Nonnull
  LivingEntity getAttacker();

  /**
   * Gets the material of the weapon that the assailant used.
   *
   * <p>Note: fist kills will return {@link Material#AIR}.
   *
   * @return Material of weapon used
   */
  @Nonnull
  Material getWeapon();

  /**
   * Gets the item stack of the weapon that the assailant used.
   *
   * <p>Note: fist kills will return null.
   *
   * @return Stack of weapon used
   */
  @Nullable
  ItemStack getWeaponStack();
}
