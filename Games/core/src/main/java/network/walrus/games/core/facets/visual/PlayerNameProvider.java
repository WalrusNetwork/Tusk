package network.walrus.games.core.facets.visual;

import com.keenant.tabbed.item.PlayerTabItem.PlayerProvider;
import network.walrus.games.core.api.spawns.SpawnsManager;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.player.PlayerTextStyle;
import network.walrus.utils.core.player.PlayerTextStyle.PrefixType;
import network.walrus.utils.core.translation.TextStyle;
import org.bukkit.entity.Player;

/**
 * Player tab name provider which takes into account if a player is currently alive.
 *
 * @author Austin Mayes
 */
public class PlayerNameProvider implements PlayerProvider<String> {

  private final GameRound round;
  private final SpawnsManager manager;
  private final Player viewer;

  /**
   * @param round the player is inside of
   * @param viewer who is seeing the name
   */
  public PlayerNameProvider(GameRound round, Player viewer) {
    this.round = round;
    this.manager = round.getFacetRequired(SpawnsManager.class);
    this.viewer = viewer;
  }

  @Override
  public String get(Player player) {
    if (!player.isOnline()) {
      return Games.OCN.TabList.NOT_ALIVE + player.getName();
    }
    TextStyle style = PlayerTextStyle.create().prefixType(PrefixType.CONDENSED);
    if (manager.isDead(player)) {
      style.inherit(Games.OCN.TabList.NOT_ALIVE);
    }
    return new PersonalizedBukkitPlayer(player, style).render(viewer).toLegacyText();
  }
}
