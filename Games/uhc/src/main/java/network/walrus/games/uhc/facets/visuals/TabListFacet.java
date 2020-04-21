package network.walrus.games.uhc.facets.visuals;

import com.keenant.tabbed.Tabbed;
import com.keenant.tabbed.tablist.TabList;
import com.keenant.tabbed.tablist.TitledTabList;
import javax.annotation.Nullable;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.uhc.UHCManager;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.events.player.PlayerJoinDelayedEvent;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Scoreboard;
import network.walrus.utils.core.color.NetworkColorConstants.Network;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.util.StringUtils;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.FacetLoadException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Main class responsible for the rendering of tab lists for all players during UHC matches.
 *
 * @author Austin Mayes
 */
@SuppressWarnings("JavaDoc")
public class TabListFacet extends Facet implements Listener {

  private final GameRound holder;
  private final GameTask onTick;
  private Tabbed tabbed;

  /** @param holder the list is being rendered inside of */
  public TabListFacet(FacetHolder holder) {
    this.holder = (GameRound) holder;
    this.onTick =
        GameTask.of(
            "Tablist tick",
            () -> {
              for (Player player : holder.getContainer().players()) {
                updateTitles(player);
              }
            });
  }

  @Override
  public void load() throws FacetLoadException {
    this.tabbed = new Tabbed(GamesPlugin.instance);
    this.onTick.repeatAsync(0, 1);
  }

  @Override
  public void unload() {
    this.onTick.cancel();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerJoin(PlayerJoinDelayedEvent event) {
    // delayed by 1 1/2 second for locale to be sent
    new GameTask("Tablist Set: " + event.getPlayer().getName()) {
      @Override
      public void run() {
        if (event.getPlayer().isOnline() && !isTabLegacy(event.getPlayer())) {
          TabListFacet.this.tabbed.newTitledTabList(event.getPlayer());
        }
      }
    }.laterAsync(30);
  }

  private TitledTabList getTabList(Player player) {
    TabList tab = this.tabbed.getTabList(player);
    return (TitledTabList) tab;
  }

  private boolean isTabLegacy(Player player) {
    return UbiquitousBukkitPlugin.getInstance().getCompatManager().getVersion(player) < 47;
  }

  /**
   * Update the header and footer for a specific player using current match information.
   *
   * @param player to update titles for
   */
  private void updateTitles(Player player) {
    @Nullable final TitledTabList tab = this.getTabList(player);
    if (tab == null) {
      return;
    }

    String header = Scoreboard.TITLE.apply(UHCManager.GAME_NAME).toLegacyText();

    final String time = StringUtils.secondsToClock((int) holder.getPlayingDuration().getSeconds());
    BaseComponent footer = new TextComponent("");

    @Nullable String serverName = null; // TODO
    if (serverName != null) {
      footer.addExtra(new UnlocalizedText(serverName, Network.LOCAL_SERVER).render(player));
      footer.addExtra(new UnlocalizedText(" - ", Games.OCN.TabList.NEUTRAL).render(player));
    }
    footer.addExtra(ChatColor.AQUA + "" + ChatColor.BOLD + time);

    tab.setHeaderFooter(header, footer.toLegacyText());
  }
}
