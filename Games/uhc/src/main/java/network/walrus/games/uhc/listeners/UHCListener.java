package network.walrus.games.uhc.listeners;

import java.util.Random;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.UHCRound;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeByPlayerEvent;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeEvent;
import network.walrus.utils.bukkit.inventory.MaterialMatcher;
import network.walrus.utils.bukkit.inventory.MultiMaterialMatcher;
import network.walrus.utils.bukkit.inventory.SingleMaterialMatcher;
import network.walrus.utils.core.color.NetworkColorConstants.Commands;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Misc UHC event handlers that don't quit fit anywhere else
 *
 * @author Austin Mayes
 */
public class UHCListener extends FacetListener {

  private final MaterialMatcher LEAF_MATCHER =
      new MultiMaterialMatcher(Material.LEAVES, Material.LEAVES_2);
  private final MaterialMatcher GRAVEL_MATCHER = new SingleMaterialMatcher(Material.GRAVEL);
  private final Random RANDOM = new Random();

  /**
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public UHCListener(FacetHolder holder, GroupsManager facet) {
    super(holder, facet);
  }

  /** Stop chunks from unloading at the start of the game */
  @EventHandler
  public void stopUnload(ChunkUnloadEvent event) {
    if (!((UHCRound) getHolder()).getState().playing()) return;
    if (((UHCRound) getHolder()).getPlayingDuration().getSeconds() > 45) return;
    event.setCancelled(true);
  }

  /** Stop thunder storms */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void noThunder(ThunderChangeEvent event) {
    event.setCancelled(event.toThunderState());
  }

  /** Stop rain storms */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void noRain(WeatherChangeEvent event) {
    event.setCancelled(event.toWeatherState());
  }

  /** @see #onBreak(BlockChangeEvent) */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerBreak(BlockChangeByPlayerEvent event) {
    this.onBreak(event);
  }

  /** Modify ender pearl damage */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void pearlDamage(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player)
        || event.getDamager().getType() != EntityType.ENDER_PEARL) {
      return;
    }

    event.setDamage(UHCManager.instance.getConfig().enderPearlDamage.get());
  }

  /** Increase rates */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBreak(BlockChangeEvent event) {
    if (!event.isToAir()) return;

    increaseDrops(
        LEAF_MATCHER,
        event.getBlock(),
        UHCManager.instance.getConfig().appleChance.get(),
        Material.APPLE);
    increaseDrops(
        GRAVEL_MATCHER,
        event.getBlock(),
        UHCManager.instance.getConfig().flintChance.get(),
        Material.GRAVEL);
  }

  /** Increase decay rates */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onDecay(LeavesDecayEvent event) {
    increaseDrops(
        LEAF_MATCHER,
        event.getBlock(),
        UHCManager.instance.getConfig().appleChance.get(),
        Material.APPLE);
  }

  private void increaseDrops(
      MaterialMatcher blockMatcher, Block broken, double chance, Material toIncrease) {
    if (blockMatcher.matches(broken.getState())) {
      boolean shouldIncrease = true;
      for (ItemStack i : broken.getDrops()) {
        if (i.getType() == toIncrease) {
          shouldIncrease = false;
          break;
        }
      }
      if (shouldIncrease) {
        if (RANDOM.nextDouble() <= chance / 100) {
          broken.getWorld().dropItem(broken.getLocation(), new ItemStack(toIncrease));
        }
      }
    }
  }

  /** Disable fire enchants */
  @EventHandler
  public void onFireEnchant(EnchantItemEvent event) {
    if (UHCManager.instance.getConfig().flameEnchants.get()) return;

    for (Enchantment enchantment : event.getEnchantsToAdd().keySet()) {
      if (enchantment.equals(Enchantment.FIRE_ASPECT)
          || enchantment.equals(Enchantment.ARROW_FIRE)) {
        event.setCancelled(true);
        event.getEnchanter().sendMessage(UHCMessages.FIRE_ENCHANTS_DISABLED.with(Commands.ERROR));
        return;
      }
    }
  }

  /** Disable horses */
  @EventHandler
  public void onHorseEnter(VehicleEnterEvent event) {
    if (UHCManager.instance.getConfig().horse.get()) return;
    if (event.getVehicle().getType() != EntityType.HORSE) return;
    if (event.getEntered().getType() != EntityType.PLAYER) return;

    event.setCancelled(true);
    event.getEntered().sendMessage(UHCMessages.HORSES_DISABLED.with(Commands.ERROR));
  }
}
