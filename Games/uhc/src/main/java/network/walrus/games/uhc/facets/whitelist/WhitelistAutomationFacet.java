package network.walrus.games.uhc.facets.whitelist;

import com.google.api.client.util.Sets;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.UHCPermissions;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathEvent;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.FacetLoadException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

/**
 * Facet which automates whitelist functions.
 *
 * @author Austin Mayes
 */
public class WhitelistAutomationFacet extends Facet implements Listener {

  private final FacetHolder holder;
  private final Set<UUID> attempted;
  private WhitelistEnableCountdown countdown;

  /** @param holder which this facet is operating inside of */
  public WhitelistAutomationFacet(FacetHolder holder) {
    this.holder = holder;
    this.attempted = Sets.newHashSet();
  }

  @Override
  public void load() throws FacetLoadException {
    super.load();
    Bukkit.setWhitelist(true);
  }

  /**
   * Starts a new whitelist countdown.
   *
   * @param duration until the whitelist is enabled
   * @throws TranslatableCommandErrorException when a countdown is already in progress
   */
  public void startCountdown(Duration duration) throws TranslatableCommandErrorException {
    if (countdown != null) {
      throw new TranslatableCommandErrorException(UHCMessages.WHITELIST_ALREADY_ON);
    }

    countdown = new WhitelistEnableCountdown(duration);
    UbiquitousBukkitPlugin.getInstance().getCountdownManager().start(countdown);
  }

  /**
   * Stops the current whitelist countdown
   *
   * @throws TranslatableCommandErrorException if a countdown is not in progress
   */
  public void stopCountdown() throws TranslatableCommandErrorException {
    if (countdown == null) {
      throw new TranslatableCommandErrorException(UHCMessages.WHITELIST_NONE_EXISTS);
    }

    UbiquitousBukkitPlugin.getInstance().getCountdownManager().cancel(countdown);
    countdown = null;
  }

  @EventHandler
  public void onRoundStart(RoundStateChangeEvent event) {
    if (event.getTo().isPresent() && event.getTo().get().starting()) {
      Bukkit.setWhitelist(true);
    }
  }

  /** Whitelist players on join */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoin(PlayerLoginEvent event) {
    if (UHCManager.instance.getUHC() != null
        && UHCManager.instance.getUHC().playingPlayers().size()
            >= UHCManager.instance.getConfig().playerCount.get()) {
      if (!event.getPlayer().hasPermission(UHCPermissions.PLAYER_COUNT_EXEMPT)) {
        event.disallow(
            Result.KICK_FULL,
            UHCMessages.SERVER_FULL.with(UHC.SERVER_FULL).render(event.getPlayer()).toLegacyText());
        if (!attempted.contains(event.getPlayer().getUniqueId())) {
          UHCManager.instance
              .hostLogger()
              .info(event.getPlayer().getDisplayName() + " tried to join but the server was full");
          attempted.add(event.getPlayer().getUniqueId());
        }

        return;
      }
    }

    if (!Bukkit.hasWhitelist()
        || event.getPlayer().hasPermission(UHCPermissions.PLAYER_WHITELIST_BYPASS)) {
      event.getPlayer().setWhitelisted(true);
    }
  }

  /** Spawn players once they've joined */
  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    UHCManager.instance.getSpawnManager().spawn(event.getPlayer());
  }

  /** Un-whitelist players on death */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDeath(PlayerDeathEvent event) {
    event.getPlayer().setWhitelisted(false);
    UHCManager.instance.getSpawnManager().spawn(event.getPlayer());
    GameTask.of(
            "Whitelist remove",
            () -> {
              if (event.getPlayer() != null && event.getPlayer().isOnline()) {
                event.getPlayer().kickPlayer(ChatColor.GOLD + "Thanks for playing :)");
              }
            })
        .later(30 * 20);
  }

  /** Un-whitelist players on death */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDeath(TaggedPlayerDeathEvent event) {
    event.getPlayer().setWhitelisted(false);
  }
}
