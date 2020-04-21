package network.walrus.games.uhc.facets.border;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import java.time.Duration;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.utils.core.color.NetworkColorConstants.Commands;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Borders;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.util.StringUtils;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.command.FacetCommandContainer;
import org.bukkit.entity.Player;

/**
 * Commands for sending specialized messages to team chat.
 *
 * @author Austin Mayes
 */
public class BorderCommands extends FacetCommandContainer<BorderFacet> {

  /**
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public BorderCommands(FacetHolder holder, BorderFacet facet) {
    super(holder, facet);
  }

  /** Add a new border */
  @Command(
      aliases = {"set", "add", "a", "s"},
      desc = "Add a new border")
  public void set(@Sender Player player, int diameter, Duration time) {
    WorldBorder border = new WorldBorder(diameter, time);
    getFacet().addBorder(border);
    player.sendMessage(UHCMessages.BORDER_ADDED.with(UHC.Borders.ADDED));
  }

  /** Remove a border */
  @Command(
      aliases = {"remove", "rm", "delete", "d"},
      desc = "Remove a border")
  public void remove(@Sender Player player, Duration time) {
    for (WorldBorder border : getFacet().getBorders()) {
      if (border.duration.equals(time)) {
        getFacet().removeBorder(border);
        player.sendMessage(UHCMessages.BORDER_REMOVE_SUCCESS.with(UHC.Borders.REMOVED));
        return;
      }
    }

    player.sendMessage(UHCMessages.BORDER_REMOVE_FAIL.with(Commands.ERROR));
  }

  /** Recalculate the borders */
  @Command(
      aliases = {"recalculate"},
      desc = "Resets all the borders and creates new ones from the specified starting size")
  public void recalculate(@Sender Player player, int diameter) {
    getFacet().recalculateDefaultBorders(diameter);
    player.sendMessage(UHCMessages.BORDER_RECALCULATED.with(Borders.RECALCULATED));
  }

  /** List all current borders */
  @Command(
      aliases = {"list", "l", "all"},
      desc = "List all current borders")
  public void list(@Sender Player player) {
    player.sendMessage(
        UHCMessages.BORDER_HEADER.with(
            UHC.Borders.HEADER_TEXT.padded().padStyle(UHC.Borders.HEADER_LINE)));
    for (WorldBorder border : getFacet().getBorders()) {
      player.sendMessage(
          UHCMessages.BORDER_DESC.with(
              UHC.Borders.DESC_TEXT,
              new UnlocalizedText(
                  StringUtils.secondsToClock((int) border.duration.getSeconds()),
                  UHC.Borders.DESC_TIME),
              new LocalizedNumber(border.radius, UHC.Borders.DESC_SIZE)));
    }
  }

  @Override
  public String[] rootAlias() {
    return new String[] {"border", "borders", "woldborder", "wb"};
  }
}
