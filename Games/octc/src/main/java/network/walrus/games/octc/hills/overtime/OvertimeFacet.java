package network.walrus.games.octc.hills.overtime;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.round.GameRound;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.boss.BossBar;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN.Hill.Overtime;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.parsing.facet.Facet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * A standard overtime facet, controls the visuals of overtimes
 *
 * @author Matthew Arnold
 */
public class OvertimeFacet extends Facet implements Listener {

  private final GameRound gameRound;

  private final Localizable chatAnnouncement;
  private final Localizable barMessage;
  private final Map<UUID, BossBar> bossBarMap;
  private OvertimeState matchState = OvertimeState.NORMAL;

  /**
   * Creates a new overtime facet
   *
   * @param gameRound the game round
   * @param barTitle the title for the boss bar
   * @param chatAnnouncement the announcement to put in chat when overtime starts
   */
  public OvertimeFacet(GameRound gameRound, Localizable barTitle, Localizable chatAnnouncement) {
    this.gameRound = gameRound;
    this.barMessage = barTitle;
    this.chatAnnouncement = chatAnnouncement;
    this.bossBarMap = Maps.newHashMap();
  }

  /**
   * Called when the match enters overtime
   *
   * @param event the overtime event
   */
  @EventHandler
  public void enterOvertime(OvertimeStartEvent event) {
    if (this.matchState == OvertimeState.OVERTIME) {
      return;
    }

    this.matchState = OvertimeState.OVERTIME;
    onOvertime();
  }

  @EventHandler
  public void onRoundChange(RoundStateChangeEvent event) {
    if (event.isChangeToNotPlaying()) {
      matchState = OvertimeState.END;
      for (BossBar bossBar : bossBarMap.values()) {
        bossBar.destroy();
      }
      bossBarMap.clear();
    }
  }

  /** Called when a player joins the game, gives them the bosbar */
  @EventHandler
  public void join(PlayerJoinEvent event) {
    if (matchState != OvertimeState.OVERTIME) {
      return;
    }
    addBar(event.getPlayer());
  }

  /**
   * Checks to see whether or not the match is currently in overtime
   *
   * @return true if the match state is after normal time, false otherwise
   */
  public boolean isAfterNormalTime() {
    return matchState != OvertimeState.NORMAL;
  }

  /**
   * Gets the current overtime state.
   *
   * <p>NOTE: This is not the preferred method to check if the game is in overtime, this method is
   * designed to distinguish between the game being in overtime and the game being over. Usually
   * this will not be needed. To check if the game is just in overtime regardless of if the match is
   * finished or not use {@link #isAfterNormalTime}
   *
   * @return the current overtime state
   */
  public OvertimeState state() {
    return matchState;
  }

  private void onOvertime() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      addBar(player);
    }
    gameRound.getContainer().broadcast(chatAnnouncement); // broadcasts in chat the message
    gameRound.getContainer().broadcast(Overtime.STARTED); // plays the start sound
  }

  private void addBar(Player player) {
    getBossBar(player).setName(barMessage.render(player)).setPercent(0f);
  }

  private BossBar getBossBar(Player player) {
    return bossBarMap.computeIfAbsent(
        player.getUniqueId(),
        x -> UbiquitousBukkitPlugin.getInstance().getBossBarManager().create(player));
  }
}
