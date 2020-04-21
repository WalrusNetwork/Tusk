package network.walrus.games.uhc.scenarios.type;

import com.google.api.client.util.Lists;
import java.util.List;
import java.util.UUID;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.scenarios.Scenario;
import network.walrus.games.uhc.scenarios.ScenarioAuthorInfo;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Scenarios.Rodless;
import network.walrus.utils.core.text.LocalizedFormat;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;

/**
 * Scenario which disables fishing rods and snowballs.
 *
 * @author Rafi Baum
 */
public class RodlessScenario extends Scenario {

  private final List<UUID> notified = Lists.newArrayList();

  @Override
  public String name() {
    return "Rodless";
  }

  @Override
  public LocalizedFormat[] description() {
    return UHCMessages.SCEN_DESC_RODLESS;
  }

  @Override
  public MaterialData icon() {
    return new MaterialData(Material.FISHING_ROD);
  }

  @Override
  public ScenarioAuthorInfo authorInfo() {
    return new ScenarioAuthorInfo(
        "BigfootPlaysMc",
        "https://www.reddit.com/r/ultrahardcore/comments/3c94fn/no_fishing_rods/");
  }

  @EventHandler
  public void onRightClick(PlayerInteractEvent event) {
    if (event.getAction() == Action.RIGHT_CLICK_BLOCK
        || event.getAction() == Action.RIGHT_CLICK_AIR) {
      if (event.getMaterial() == Material.SNOW_BALL
          || event.getMaterial() == Material.FISHING_ROD) {
        event.setCancelled(true);
        if (!notified.contains(event.getPlayer().getUniqueId())) {
          event.getPlayer().sendMessage(UHCMessages.NO_RODS.with(Rodless.NO_RODS));
          notified.add(event.getPlayer().getUniqueId());
          GameTask.of("Rodless notify", () -> notified.remove(event.getPlayer().getUniqueId()))
              .later(20 * 5);
        }
      }
    }
  }
}
