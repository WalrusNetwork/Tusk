package network.walrus.games.uhc.facets.goldenhead;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.time.Duration;
import java.util.UUID;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.UHCRound;
import network.walrus.games.uhc.scenarios.type.GoldenRetrieverScenario;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathEvent;
import network.walrus.utils.bukkit.item.ItemTag.Boolean;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC;
import network.walrus.utils.parsing.facet.Facet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Facet which handles all aspects of the golden head mechanic.
 *
 * @author ShinyDialga
 */
public class GoldenHeadFacet extends Facet implements Listener {

  private static final ItemStack goldenHead = new ItemStack(Material.GOLDEN_APPLE);
  private static final Boolean GOLDEN_HEAD_ITEM = new Boolean("uhc.goldenHead", false);

  static {
    GOLDEN_HEAD_ITEM.set(goldenHead, true);
  }

  private final UHCRound round;
  private final GoldenRetrieverScenario goldenRetriever;
  private Multimap<UUID, Long> gapplesAte = HashMultimap.create();
  private Multimap<UUID, Long> headsAte = HashMultimap.create();

  /** @param round this facet is operating inside of */
  public GoldenHeadFacet(UHCRound round) {
    this.round = round;
    this.goldenRetriever =
        (GoldenRetrieverScenario)
            UHCManager.instance.getScenarioManager().search("GoldenRetriever").get();
  }

  private void addRecipes() {
    ShapedRecipe recipe = new ShapedRecipe(goldenHead.clone());

    recipe.shape("GGG", "GHG", "GGG");

    recipe.setIngredient('G', Material.GOLD_INGOT);
    recipe.setIngredient('H', new MaterialData(Material.SKULL_ITEM, (byte) 3));

    Bukkit.getServer().addRecipe(recipe);
  }

  @Override
  public void enable() {
    addRecipes();
  }

  /** Translate item names for each player */
  @EventHandler
  public void translateName(PrepareItemCraftEvent event) {
    ItemStack stack = event.getRecipe().getResult();
    if (GOLDEN_HEAD_ITEM.get(stack)) {
      ItemMeta meta = stack.getItemMeta();
      meta.setDisplayName(
          UHCMessages.GOLDEN_HEAD_ITEM
              .with(UHC.GoldenHead.NAME)
              .render(event.getActor())
              .toLegacyText());
      stack.setItemMeta(meta);
      event.getInventory().setResult(stack);
    }
  }

  @Override
  public void disable() {
    Bukkit.getServer().resetRecipes();
  }

  private ItemStack generateApples(UUID applesFor) {
    double points = 0;
    for (Long duration : gapplesAte.get(applesFor)) {
      points += getPoints(round.getPlayingDuration().minusMillis(duration));
    }
    for (Long duration : headsAte.get(applesFor)) {
      points += 2 * getPoints(round.getPlayingDuration().minusMillis(duration));
    }

    if (points < 1) return null;

    return new ItemStack(Material.GOLDEN_APPLE, (int) points);
  }

  /** Spawn golden head stand and apply drops on death */
  @EventHandler(priority = EventPriority.HIGH)
  public void onDeath(TaggedPlayerDeathEvent event) {
    if (UHCManager.instance.getScenarioManager().isActive(goldenRetriever)) {
      spawnHead(event.getPlayer().getLocation(), event.getPlayer().getName());
    } else {
      event.getDrops().add(goldenHead.clone());
    }

    ItemStack apple = generateApples(event.getPlayer().getUniqueId());
    if (apple == null) return;
    event.getDrops().add(apple);
  }

  /** Spawn golden head stand and apply drops on death */
  @EventHandler(priority = EventPriority.HIGH)
  public void onDeath(PlayerDeathEvent event) {
    if (UHCManager.instance.getScenarioManager().isActive(goldenRetriever)) {
      spawnHead(event.getPlayer().getLocation(), event.getPlayer().getName());
    } else {
      event.getDrops().add(goldenHead.clone());
    }

    ItemStack apple = generateApples(event.getPlayer().getUniqueId());
    if (apple == null) return;
    event.getDrops().add(apple);
  }

  /** Apply effects for golden heads */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onItemConsume(PlayerItemConsumeEvent event) {
    ItemStack item = event.getItem();
    if (GOLDEN_HEAD_ITEM.get(item)) {
      event.getActor().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 1));
      headsAte.put(event.getActor().getUniqueId(), round.getPlayingDuration().toMillis());
    } else {
      gapplesAte.put(event.getActor().getUniqueId(), round.getPlayingDuration().toMillis());
    }
  }

  private void spawnHead(Location location, String owner) {
    location.getBlock().setType(Material.NETHER_FENCE);
    location.add(0, 1, 0);
    location.getBlock().setType(Material.SKULL);
    location.getBlock().setData((byte) 1);
    Skull skull = (Skull) location.getBlock().getState();
    skull.setSkullType(SkullType.PLAYER);
    skull.setRotation(BlockFace.NORTH);
    skull.setOwner(owner);
    skull.update();
  }

  private double getPoints(Duration durationSinceConsumption) {
    if (durationSinceConsumption.toMinutes() > 2) {
      return 0;
    } else if (durationSinceConsumption.toMinutes() > 1) {
      return 0.1;
    } else if (durationSinceConsumption.getSeconds() > 30) {
      return 0.25;
    } else if (durationSinceConsumption.getSeconds() > 5) {
      return 0.5;
    } else {
      return 0.75;
    }
  }
}
