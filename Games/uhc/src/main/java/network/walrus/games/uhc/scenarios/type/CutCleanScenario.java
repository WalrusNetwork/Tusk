package network.walrus.games.uhc.scenarios.type;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.scenarios.Scenario;
import network.walrus.games.uhc.scenarios.ScenarioAuthorInfo;
import network.walrus.ubiquitous.bukkit.tracker.event.entity.EntityDeathByEntityEvent;
import network.walrus.ubiquitous.bukkit.tracker.tag.LoggerNPCManager;
import network.walrus.utils.bukkit.block.BlockUtils;
import network.walrus.utils.core.text.LocalizedFormat;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Scenario which turns non-cooked items into their cooked counterparts.
 *
 * @author Austin Mayes
 */
public class CutCleanScenario extends Scenario {

  private static final Map<EntityType, Material> DEFAULT_DROPS = Maps.newHashMap();
  private static final Map<Material, Material> REPLACEMENTS = Maps.newHashMap();
  private static final List<Material> XP = Arrays.asList(Material.GOLD_ORE, Material.IRON_ORE);

  static {
    DEFAULT_DROPS.put(EntityType.CREEPER, Material.SULPHUR);
    DEFAULT_DROPS.put(EntityType.SKELETON, Material.ARROW);
    DEFAULT_DROPS.put(EntityType.SPIDER, Material.STRING);
    DEFAULT_DROPS.put(EntityType.ZOMBIE, Material.ROTTEN_FLESH);
    DEFAULT_DROPS.put(EntityType.SLIME, Material.SLIME_BALL);
    DEFAULT_DROPS.put(EntityType.GHAST, Material.GHAST_TEAR);
    DEFAULT_DROPS.put(EntityType.PIG_ZOMBIE, Material.GOLD_NUGGET);
    DEFAULT_DROPS.put(EntityType.ENDERMAN, Material.ENDER_PEARL);
    DEFAULT_DROPS.put(EntityType.CAVE_SPIDER, Material.STRING);
    DEFAULT_DROPS.put(EntityType.BLAZE, Material.BLAZE_POWDER);
    DEFAULT_DROPS.put(EntityType.MAGMA_CUBE, Material.MAGMA_CREAM);
    DEFAULT_DROPS.put(EntityType.PIG, Material.GRILLED_PORK);
    DEFAULT_DROPS.put(EntityType.SHEEP, Material.COOKED_MUTTON);
    DEFAULT_DROPS.put(EntityType.COW, Material.COOKED_BEEF);
    DEFAULT_DROPS.put(EntityType.CHICKEN, Material.FEATHER);
    DEFAULT_DROPS.put(EntityType.IRON_GOLEM, Material.RED_ROSE);

    REPLACEMENTS.put(Material.GRAVEL, Material.FLINT);
    REPLACEMENTS.put(Material.RAW_BEEF, Material.COOKED_BEEF);
    REPLACEMENTS.put(Material.RAW_CHICKEN, Material.COOKED_CHICKEN);
    REPLACEMENTS.put(Material.RAW_FISH, Material.COOKED_FISH);
    REPLACEMENTS.put(Material.PORK, Material.GRILLED_PORK);
    REPLACEMENTS.put(Material.MUTTON, Material.COOKED_MUTTON);
    REPLACEMENTS.put(Material.RABBIT, Material.COOKED_RABBIT);
    REPLACEMENTS.put(Material.IRON_ORE, Material.IRON_INGOT);
    REPLACEMENTS.put(Material.GOLD_ORE, Material.GOLD_INGOT);
  }

  @Override
  public String name() {
    return "CutClean";
  }

  @Override
  public LocalizedFormat[] description() {
    return UHCMessages.SCEN_DESC_CUT_CLEAN;
  }

  @Override
  public MaterialData icon() {
    return new MaterialData(Material.CHAINMAIL_CHESTPLATE);
  }

  @Override
  public ScenarioAuthorInfo authorInfo() {
    return new ScenarioAuthorInfo("/u/KatManKhaos", "https://www.reddit.com/user/KatManKhaos");
  }

  /** Roast items */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onItemSpawn(ItemSpawnEvent event) {
    ItemStack stack = event.getEntity().getItemStack();

    if (XP.contains(stack.getType())) {
      ExperienceOrb orb =
          (ExperienceOrb)
              event
                  .getLocation()
                  .getWorld()
                  .spawnEntity(BlockUtils.center(event.getLocation()), EntityType.EXPERIENCE_ORB);
      orb.setExperience(1);
    }

    if (REPLACEMENTS.containsKey(stack.getType())) {
      stack.setType(REPLACEMENTS.get(stack.getType()));
    }
  }

  /** Ensure every mob has a drop */
  @EventHandler(priority = EventPriority.LOW)
  public void onEntityKill(EntityDeathByEntityEvent event) {
    if (LoggerNPCManager.isNPC(event.getEntity())) return;
    if (event.getEntity() instanceof Animals || event.getEntity() instanceof Monster) {
      if (!event.getDrops().isEmpty()) return;

      if (DEFAULT_DROPS.containsKey(event.getEntityType()))
        event.getDrops().add(new ItemStack(DEFAULT_DROPS.get(event.getEntityType())));
    }
  }

  /** Roast items */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerDeath(EntityDeathEvent event) {
    if (event.getEntity() instanceof Animals) {
      for (ItemStack stack : event.getDrops()) {
        Material mat = REPLACEMENTS.get(stack.getType());
        if (mat != null) {
          stack.setType(mat);
        }
      }
    }
  }
}
