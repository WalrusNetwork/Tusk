package network.walrus.games.uhc.scenarios.type;

import app.ashcon.intake.exception.TranslatableCommandException;
import com.google.common.collect.Maps;
import java.util.Map;
import network.walrus.games.core.events.competitor.PlayerChangeCompetitorEvent;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.scenarios.Scenario;
import network.walrus.games.uhc.scenarios.ScenarioAuthorInfo;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import network.walrus.utils.core.text.LocalizedFormat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.MaterialData;

/**
 * Shared inventories for teams.
 *
 * @author Austin Mayes
 */
public class BackpackScenario extends Scenario {

  private final Map<String, Inventory> teamInventories = Maps.newHashMap();

  @Override
  public String name() {
    return "Backpacks";
  }

  @Override
  public LocalizedFormat[] description() {
    return UHCMessages.SCEN_DESC_BACKPACK;
  }

  @Override
  public MaterialData icon() {
    return new MaterialData(Material.CHEST);
  }

  @Override
  public ScenarioAuthorInfo authorInfo() {
    return new ScenarioAuthorInfo("/u/Audicyy", "https://www.reddit.com/user/Audicyy");
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onMine(PlayerChangeCompetitorEvent event) {
    // Localization here would be nice, but not having to keep track of items across multiple
    // inventories is nicer.
    event
        .getCompetitorTo()
        .ifPresent(
            g ->
                teamInventories.put(
                    g.id(),
                    Bukkit.getServer()
                        .createInventory(
                            null,
                            27,
                            g.getColoredName().render(Bukkit.getConsoleSender()).toLegacyText()
                                + "'s Shared Inventory")));
  }

  public void open(Player viewer, Group toOpen) throws TranslatableCommandException {
    if (!teamInventories.containsKey(toOpen.id())) {
      throw new TranslatableCommandErrorException(
          UHCMessages.BACKPACK_NO_INV, toOpen.getName().toText());
    }

    viewer.closeInventory();
    viewer.openInventory(teamInventories.get(toOpen.id()));
  }
}
