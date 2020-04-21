package network.walrus.ubiquitous.bukkit.tracker.info.base;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.base.AbstractDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.MeleeDamageInfo;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

/**
 * @author Overcast Network
 * @see MeleeDamageInfo
 */
public class SimpleMeleeDamageInfo extends AbstractDamageInfo implements MeleeDamageInfo {

  private final @Nonnull Material weaponMaterial;
  private final @Nullable ItemStack weapon;

  /**
   * Constructor.
   *
   * @param attacker entity which dealt the damage
   * @param weapon used to deal the damage
   */
  public SimpleMeleeDamageInfo(@Nonnull LivingEntity attacker, @Nullable ItemStack weapon) {
    super(attacker);

    Preconditions.checkNotNull(attacker, "attacker");

    this.weaponMaterial = weapon == null ? Material.AIR : weapon.getType();
    this.weapon = weapon;
  }

  public @Nonnull LivingEntity getAttacker() {
    return this.resolvedDamager;
  }

  public @Nonnull Material getWeapon() {
    return this.weaponMaterial;
  }

  public @Nullable ItemStack getWeaponStack() {
    return this.weapon;
  }

  @Override
  public @Nonnull DamageCause getDamageCause() {
    return DamageCause.ENTITY_ATTACK;
  }
}
