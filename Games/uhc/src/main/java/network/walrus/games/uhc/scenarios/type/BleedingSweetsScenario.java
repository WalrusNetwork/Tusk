package network.walrus.games.uhc.scenarios.type;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.scenarios.Scenario;
import network.walrus.games.uhc.scenarios.ScenarioAuthorInfo;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathEvent;
import network.walrus.utils.core.text.LocalizedFormat;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Scenario which add resources to drops when a player is killed.
 *
 * @author Rafi Baum
 */
public class BleedingSweetsScenario extends Scenario {

  private static final Collection<ItemStack> sweets = Sets.newHashSet();

  static {
    sweets.add(new ItemStack(Material.DIAMOND));
    sweets.add(new ItemStack(Material.GOLD_INGOT, 5));
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    event.getDrops().addAll(sweets);
  }

  @EventHandler
  public void onTaggedPlayerDeath(TaggedPlayerDeathEvent event) {
    List<ItemStack> drops = Lists.newArrayList(event.getDrops());
    drops.addAll(sweets);
    event.setDrops(drops);
  }

  /**
   * The name of the scenario, used for UI. This is intentionally not localized since all scenario
   * names should be uniform across locales.
   */
  @Override
  public String name() {
    return "BleedingSweets";
  }

  /** Description array of information describing the details of this scenario. */
  @Override
  public LocalizedFormat[] description() {
    return UHCMessages.SCEN_DESC_BLEEDING;
  }

  /** Icon of the scenario, used in selection and information menus. */
  @Override
  public MaterialData icon() {
    return new MaterialData(Material.GOLD_INGOT);
  }

  /** Information about the author of this scenario. */
  @Override
  public ScenarioAuthorInfo authorInfo() {
    return ScenarioAuthorInfo.UNKNOWN;
  }
}
