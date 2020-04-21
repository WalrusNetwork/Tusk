package network.walrus.games.octc.ctw.wools;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.stats.StatsFacet;
import network.walrus.games.core.facets.visual.PaneGroup;
import network.walrus.games.core.facets.visual.SidebarFacet;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.ctw.wools.events.WoolPlaceEvent;
import network.walrus.games.octc.global.results.WinCalculator;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN.CTW.Place;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.FacetLoadException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

/**
 * Manages all {@link WoolObjective}s for the round.
 *
 * @author Austin Mayes
 */
public class WoolsFacet extends Facet implements Listener {

  private final GameRound round;
  private final List<WoolObjective> wools;
  private final WinCalculator winCalculator;
  private final Map<Inventory, WoolObjective> woolInventoriesMap;

  /**
   * @param holder which is holding this object
   * @param wools which should be used
   */
  WoolsFacet(FacetHolder holder, List<WoolObjective> wools) {
    this.round = (GameRound) holder;
    this.wools = wools;
    this.winCalculator =
        new WinCalculator(round, new ArrayList<>(this.wools), Lists.newArrayList());
    SidebarFacet.PANE_CREATE_FUNCTION =
        (m, c) -> new PaneGroup(Pair.of("wools", new WoolDisplay((GameRound) holder, m, this, c)));
    this.woolInventoriesMap = Maps.newHashMap();
  }

  @Override
  public void load() throws FacetLoadException {
    for (WoolObjective wool : wools) {
      wool.initialize();
    }

    round
        .getFacet(StatsFacet.class)
        .ifPresent(statsFacet -> statsFacet.addTracker(new WoolTracker()));
  }

  List<WoolObjective> getWools() {
    return wools;
  }

  Optional<WoolObjective> getObjectiveForWoolChest(Inventory inventory) {
    return Optional.ofNullable(woolInventoriesMap.get(inventory));
  }

  void addWoolInventory(Inventory inventory, WoolObjective objective) {
    woolInventoriesMap.put(inventory, objective);
  }

  /** Handle wins */
  @EventHandler
  public void onComplete(WoolPlaceEvent event) {
    Player player = event.getPlayers().get(0);
    round
        .getFacetRequired(GroupsManager.class)
        .playScopedSound(player, Place.SELF, Place.TEAM, Place.ENEMY, Place.SPECTATOR);
    this.winCalculator.check();
  }
}
