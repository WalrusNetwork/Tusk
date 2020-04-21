package network.walrus.games.core.commands;

import app.ashcon.intake.Command;
import app.ashcon.intake.CommandException;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.parametric.annotation.Default;
import app.ashcon.intake.parametric.provider.EnumProvider;
import java.time.Duration;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.GamesCorePermissions;
import network.walrus.games.core.facets.applicators.ApplicatorListener;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.core.round.states.RoundStartCountdown;
import network.walrus.games.core.util.GameTask;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.utils.bukkit.region.Region;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import network.walrus.utils.core.registry.Identifiable;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.command.FacetCommandContainer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Various testing commands. This class will likely go away after initial beta testing is complete.
 *
 * @author Austin Mayes
 */
public class DevCommands extends FacetCommandContainer<Facet> {

  /** @param holder which this object is inside of */
  public DevCommands(FacetHolder holder) {
    super(holder);
    holder.addCommandModule(
        binder ->
            binder.bind(FilterLogType.class).toProvider(new EnumProvider<>(FilterLogType.class)));
  }

  /**
   * Start the match.
   *
   * @throws CommandException if the holder isn't a game round
   */
  @Command(
      aliases = {"start", "begin", "startgame", "sg", "s", "b"},
      desc = "Start the round.",
      perms = GamesCorePermissions.START_ROUND)
  public void start(@Default("30s") Duration startTime) throws CommandException {
    if (!(getHolder() instanceof GameRound)) {
      throw new TranslatableCommandErrorException(GamesCoreMessages.ERROR_ONLY_ROUNDS);
    }
    GameRound round = (GameRound) getHolder();
    if (round.getState().started()) {
      return;
    }

    UbiquitousBukkitPlugin.getInstance().getCountdownManager().cancelAll();
    UbiquitousBukkitPlugin.getInstance()
        .getCountdownManager()
        .start(new RoundStartCountdown(startTime, (GameRound) getHolder()));
  }

  /** List regions a player is currently standing in. */
  @Command(
      aliases = {"regions", "regionlist", "currentregions", "rl", "cr"},
      desc = "Print what regions you are currently in.",
      perms = GamesCorePermissions.LIST_REGIONS)
  public void currentRegions(@Sender Player player) {
    for (Identifiable value : getHolder().getRegistry().getObjects().values()) {
      if (value.object() instanceof Region) {
        Region region = (Region) value.object();

        if (region.contains(player)) {
          player.sendMessage("You are in " + value.id());
        }
      }
    }
  }

  /** Toggle filter logging for applicator actions. */
  @Command(
      aliases = {"logfilters", "lf"},
      desc = "Log filter results for applicators to map dev chat.",
      perms = GamesCorePermissions.LOG_FILTERS)
  public void logFilters(@Sender CommandSender sender, FilterLogType type) {
    switch (type) {
      case ENTER:
        ApplicatorListener.logEnter = !ApplicatorListener.logEnter;
        sender.sendMessage("Switched enter logging to " + ApplicatorListener.logEnter);
        break;
      case LEAVE:
        ApplicatorListener.logLeave = !ApplicatorListener.logLeave;
        sender.sendMessage("Switched leave logging to " + ApplicatorListener.logLeave);
        break;
      case PLACE:
        ApplicatorListener.logPlace = !ApplicatorListener.logPlace;
        sender.sendMessage("Switched place logging to " + ApplicatorListener.logPlace);
        break;
      case BREAK:
        ApplicatorListener.logBreak = !ApplicatorListener.logBreak;
        sender.sendMessage("Switched break logging to " + ApplicatorListener.logBreak);
        break;
      case USE:
        ApplicatorListener.logUse = !ApplicatorListener.logUse;
        sender.sendMessage("Switched use logging to " + ApplicatorListener.logUse);
        break;
    }
  }

  /** Play all sounds. */
  @Command(
      aliases = {"playallsounds", "playall", "pas"},
      desc = "Play all sounds",
      perms = GamesCorePermissions.PLAY_ALL_SOUNDS)
  public void playAllSounds(@Sender Player player) {
    GameTask.of(
            "Play all sounds",
            () -> {
              for (Sound value : Sound.values()) {
                player.sendMessage("playing " + value.name());
                player.playSound(player.getLocation(), value, 1, 1);
                try {
                  Thread.sleep(2000);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
              }
            })
        .nowAsync();
  }

  enum FilterLogType {
    ENTER,
    LEAVE,
    BREAK,
    PLACE,
    USE
  }
}
