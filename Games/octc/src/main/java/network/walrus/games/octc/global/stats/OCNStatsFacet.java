package network.walrus.games.octc.global.stats;

import gg.walrus.javaapiclient.type.AresStatsInput.Builder;
import network.walrus.games.core.facets.stats.StatsFacet;
import network.walrus.games.octc.ctf.flags.FlagTracker;
import network.walrus.games.octc.ctw.wools.WoolTracker;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;

/**
 * Facet which manages stats for the OCN gamemodes.
 *
 * @author Rafi Baum
 */
public class OCNStatsFacet extends StatsFacet {

  public OCNStatsFacet(FacetHolder holder) {
    super(holder);
  }

  @Override
  protected void updateStats(Player player, Builder statsInputBuilder) {
    super.updateStats(player, statsInputBuilder);
    getTracker(FlagTracker.class)
        .ifPresent(flagTracker -> statsInputBuilder.flags(flagTracker.fetchUpdate(player)));
    getTracker(WoolTracker.class)
        .ifPresent(woolTracker -> statsInputBuilder.wools(woolTracker.fetchUpdate(player)));
  }
}
