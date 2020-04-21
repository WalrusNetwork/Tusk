package network.walrus.games.uhc.facets.scatter;

import app.ashcon.intake.Command;
import app.ashcon.intake.CommandException;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import java.util.ArrayList;
import java.util.List;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.round.states.RoundState;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.UHCPermissions;
import network.walrus.games.uhc.UHCRound;
import network.walrus.games.uhc.facets.groups.UHCGroupsManager;
import network.walrus.games.uhc.spawn.SpawnManager;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.lobby.facets.sterile.WorldProtectionListener;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.UHC;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Scatter;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;
import network.walrus.utils.parsing.facet.parse.configurator.command.FacetCommandContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commands used for scattering players.
 *
 * @author Austin Mayes
 */
public class ScatterCommands extends FacetCommandContainer<UHCGroupsManager> {

  private boolean scattered = false;

  /**
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public ScatterCommands(FacetHolder holder, UHCGroupsManager facet) {
    super(holder, facet);
  }

  /**
   * Scatter players
   *
   * @throws CommandException if players have already been scattered
   */
  @Command(
      aliases = {"scatter", "bs", "beginscatter", "startscatter", "ss", "scat"},
      desc = "Scatter players",
      perms = UHCPermissions.SCATTER_START_PERM)
  public void scatter(@Sender CommandSender sender) throws CommandException {
    if (scattered) {
      throw new TranslatableCommandErrorException(UHCMessages.ALREADY_SCATTERED);
    }
    scattered = true;
    UHCManager.instance.getUHC().setState(RoundState.STARTING);
    WorldProtectionListener.IGNORE_ALL = true;
    for (Player player : ((UHCRound) getHolder()).playingPlayers()) {
      UbiquitousBukkitPlugin.getInstance().getFreezeManager().freeze(player);
    }
    SpawnManager manager = UHCManager.instance.getSpawnManager();
    Bukkit.getServer().broadcast(UHCMessages.SCATTER_GEN_STARTED.with(Scatter.GEN_STARTED));
    Location center = new Location(getHolder().getContainer().mainWorld(), 0, 0, 0);
    manager.populateSpawns(
        (List<Competitor>) getFacet().getCompetitors(),
        center,
        () -> {
          Bukkit.getServer().broadcast(UHCMessages.SCATTER_GEN_FINISHED.with(Scatter.GEN_FINISHED));
          GameTask.of(
                  "Scatter",
                  () -> {
                    getHolder()
                        .getContainer()
                        .actOnAllWorlds(
                            w -> {
                              w.setTime(1000);
                              w.setGameRuleValue("doDaylightCycle", "false");
                            });
                    List<Competitor> nonEmpty = new ArrayList<>();
                    for (Competitor c : getFacet().getCompetitors()) {
                      if (!c.getPlayers().isEmpty()) {
                        nonEmpty.add(c);
                      }
                    }
                    UbiquitousBukkitPlugin.getInstance()
                        .getCountdownManager()
                        .start(new ScatterCountdown(nonEmpty, manager));
                    Bukkit.getServer().broadcast(UHCMessages.SCATTER_STARTED.with(Scatter.STARTED));
                    UHC.Scatter.STARTED.play(sender);
                  })
              .later(5 * 20);
        });
  }

  public static class Configurator implements FacetConfigurator {

    @Override
    public void configure() {
      bindFacetCommands(ScatterCommands.class, UHCGroupsManager.class);
    }
  }
}
