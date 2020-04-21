package network.walrus.games.uhc.facets.groups;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.command.FacetCommandContainer;
import org.bukkit.entity.Player;

/**
 * Commands enabled in single and team games.
 *
 * @author Austin Mayes
 */
public class GeneralTeamCommands extends FacetCommandContainer<UHCGroupsManager> {

  /**
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public GeneralTeamCommands(FacetHolder holder, UHCGroupsManager facet) {
    super(holder, facet);
  }

  /** Become a spectator */
  @Command(
      aliases = {"spec", "spectate"},
      desc = "Become a spectator")
  public void spec(@Sender Player player) {
    getFacet().changeGroup(player, getFacet().getSpectators(), false, false);
  }

  /** Join the match */
  @Command(aliases = "join", desc = "Join the match")
  public void join(@Sender Player player) throws TranslatableCommandErrorException {
    if (UHCManager.instance.getUHC() != null
        && (UHCManager.instance.getUHC().getState().starting()
            || UHCManager.instance.getUHC().getState().started())) {
      throw new TranslatableCommandErrorException(UHCMessages.ERROR_JOIN_AFTER_SCATTER);
    }

    if (getFacet().isSpectator(player)) {
      getFacet().addPlayer(player);
    }
  }
}
