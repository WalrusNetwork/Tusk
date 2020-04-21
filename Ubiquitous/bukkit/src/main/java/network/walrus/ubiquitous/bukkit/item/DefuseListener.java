package network.walrus.ubiquitous.bukkit.item;

import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.UbiquitousMessages;
import network.walrus.utils.bukkit.inventory.ScopableItemStack;
import network.walrus.utils.bukkit.item.ItemTag;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Defuse;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Extinguish;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttackEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Listener which handles all defuser and extinguisher uses.
 *
 * @author Austin Mayes
 */
public class DefuseListener implements Listener {

  public static final ItemTag.Boolean IS_DEFUSER = new ItemTag.Boolean("defuser", false);
  public static final ItemTag.Boolean IS_EXTINGUISHER = new ItemTag.Boolean("extinguisher", false);
  public static final ScopableItemStack DEFUSER = createDefuser();
  public static final ScopableItemStack EXTINGUISHER = createExtinguisher();
  public static Predicate<Player> DEFUSER_USE_CHECK = (p) -> true;
  public static BiPredicate<Player, Entity> DEFUSE_CHECK = (p, e) -> true;
  public static Predicate<Player> EXTINGUISHER_USE_CHECK = (p) -> true;
  public static BiPredicate<Player, Entity> EXTINGUISH_CHECK = (p, e) -> true;

  private static ScopableItemStack createDefuser() {
    ItemStack stack = new ItemStack(Material.SHEARS);
    IS_DEFUSER.set(stack, true);
    ScopableItemStack scopable = new ScopableItemStack(stack);
    scopable
        .name(new LocalizedConfigurationProperty(UbiquitousMessages.DEFUSER_NAME))
        .lore(
            Arrays.stream(UbiquitousMessages.DEFUSER_LORE)
                .map(f -> new LocalizedConfigurationProperty(f))
                .toArray(LocalizedConfigurationProperty[]::new));
    return scopable;
  }

  private static ScopableItemStack createExtinguisher() {
    ItemStack stack = new ItemStack(Material.FEATHER);
    IS_EXTINGUISHER.set(stack, true);
    ScopableItemStack scopable = new ScopableItemStack(stack);
    scopable
        .name(new LocalizedConfigurationProperty(UbiquitousMessages.EXTINGUISHER_NAME))
        .lore(
            Arrays.stream(UbiquitousMessages.EXTINGUISHER_LORE)
                .map(f -> new LocalizedConfigurationProperty(f))
                .toArray(LocalizedConfigurationProperty[]::new));
    return scopable;
  }

  /**
   * Remove all primed TNT within a certain radius of a location.
   *
   * @param player who is defusing the TNT
   * @param location to use as the center of the defuse area
   * @param radius out from the center to remove entities from
   */
  public static void defuseMulti(@Nullable Player player, Location location, int radius) {
    if (radius <= 0) {
      return;
    }

    double radiusSq = radius * radius;
    for (Entity entity : location.getWorld().getEntities()) {
      if (location.distanceSquared(entity.getLocation()) > radiusSq) {
        continue;
      }

      if (entity instanceof TNTPrimed && (player == null || DEFUSE_CHECK.test(player, entity))) {
        entity.remove();
        if (player != null) {
          Defuse.SELF.play(player);
          for (Entity e : player.getNearbyEntities(5, 5, 5)) {
            if (e instanceof Player) {
              Defuse.OTHER.play(e, entity.getLocation());
            }
          }
        }
      }
    }
  }

  /**
   * Remove all fire within a certain radius of a location.
   *
   * @param player who is removing the fire
   * @param location to use as the center of the extinguish area
   * @param radius out from the center to remove fire from
   */
  public static void extinguishMulti(@Nullable Player player, Location location, int radius) {
    if (radius <= 0) {
      return;
    }

    final Vector vector = new Vector(radius, radius, radius);
    final Vector min = location.clone().subtract(vector).toVector();
    final Vector max = location.clone().add(vector).toVector();
    for (int x = min.getBlockX(); x < max.getBlockX(); x++) {
      for (int y = min.getBlockY(); y < max.getBlockY(); y++) {
        for (int z = min.getBlockZ(); z < max.getBlockZ(); z++) {
          final Block block = location.getWorld().getBlockAt(x, y, z);
          if (block.getType() == Material.FIRE) {
            block.setType(Material.AIR);
          }
        }
      }
    }

    double radiusSq = radius * radius;
    for (Entity entity : location.getWorld().getEntities()) {
      if (location.distanceSquared(entity.getLocation()) > radiusSq) {
        continue;
      }

      if (player == null || EXTINGUISH_CHECK.test(player, entity)) {
        entity.setFireTicks(0);
        if (player != null) {
          Extinguish.SELF.play(player);
          for (Entity e : player.getNearbyEntities(5, 5, 5)) {
            if (e instanceof Player) {
              Extinguish.OTHER.play(e, entity.getLocation());
            }
          }
        }
      }
    }
  }

  private void defuseSingle(Player clicker, Entity clicked) {
    if (!DEFUSER_USE_CHECK.test(clicker)) {
      return;
    }
    ItemStack hand = clicker.getItemInHand();
    if (hand == null || !IS_DEFUSER.get(hand)) {
      return;
    }
    if (!(clicked instanceof TNTPrimed)) {
      return;
    }

    if (DEFUSE_CHECK.test(clicker, clicked)) {
      Defuse.SELF.play(clicked);
      for (Entity e : clicker.getNearbyEntities(5, 5, 5)) {
        if (e instanceof Player) {
          Defuse.OTHER.play(e, clicked.getLocation());
        }
      }
      clicked.remove();
    }
  }

  private void exinguishSingle(Player clicker, Entity clicked) {
    if (!EXTINGUISHER_USE_CHECK.test(clicker)) {
      return;
    }
    ItemStack hand = clicker.getItemInHand();
    if (hand == null || !IS_EXTINGUISHER.get(hand)) {
      return;
    }
    if (EXTINGUISH_CHECK.test(clicker, clicked)) {
      clicked.setFireTicks(0);
      Extinguish.SELF.play(clicker);
      for (Entity e : clicker.getNearbyEntities(5, 5, 5)) {
        if (e instanceof Player) {
          Extinguish.OTHER.play(e, clicked.getLocation());
        }
      }
    }
  }

  /** Defuser clicked not on primed TNT. */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void defuseArea(final PlayerInteractEvent event) {
    ItemStack hand = event.getPlayer().getItemInHand();
    if (hand == null || !IS_DEFUSER.get(hand)) {
      return;
    }

    defuseMulti(event.getPlayer(), event.getPlayer().getLocation(), 16);
  }

  /** Defuser clicked on primed TNT. */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void defuseDirect(final PlayerInteractEntityEvent event) {
    defuseSingle(event.getPlayer(), event.getRightClicked());
  }

  /** Defuser clicked on primed TNT. */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void defuseDirect(final PlayerAttackEntityEvent event) {
    defuseSingle(event.getPlayer(), event.getLeftClicked());
  }

  /** Extinguisher clicked not on primed TNT. */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void extinguishArea(final PlayerInteractEvent event) {
    ItemStack hand = event.getPlayer().getItemInHand();
    if (hand == null || !IS_EXTINGUISHER.get(hand)) {
      return;
    }

    extinguishMulti(event.getPlayer(), event.getPlayer().getLocation(), 16);
  }

  /** Extinguisher clicked on ignited entity. */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void extinguishDirect(final PlayerInteractEntityEvent event) {
    exinguishSingle(event.getPlayer(), event.getRightClicked());
  }

  /** Extinguisher clicked on ignited entity. */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void extinguishDirect(final PlayerAttackEntityEvent event) {
    exinguishSingle(event.getPlayer(), event.getLeftClicked());
  }
}
