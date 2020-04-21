package network.walrus.games.core.facets.spawners.spawneritems;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

/**
 * Spawns a splash potion with a specified potion effect
 *
 * @author Matthew Arnold
 */
public class SpawnerSplashPotion implements SpawnerEntry {

  private final PotionEffect potionEffect;

  /** @param potionEffect The potion effect to spawn in the effect */
  public SpawnerSplashPotion(PotionEffect potionEffect) {
    this.potionEffect = potionEffect;
  }

  @Override
  public void spawn(Location location, Vector velocity) {
    ItemStack item = new ItemStack(Material.POTION);
    PotionMeta meta = (PotionMeta) item.getItemMeta();
    meta.addCustomEffect(potionEffect, true);
    item.setItemMeta(meta);
    ThrownPotion thrownPotion =
        (ThrownPotion) location.getWorld().spawnEntity(location, EntityType.SPLASH_POTION);
    thrownPotion.setItem(item);
    thrownPotion.setVelocity(velocity);
  }
}
