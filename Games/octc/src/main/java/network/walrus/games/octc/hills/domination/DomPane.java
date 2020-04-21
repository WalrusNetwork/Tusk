package network.walrus.games.octc.hills.domination;

import java.util.List;
import java.util.Optional;
import net.md_5.bungee.api.ChatColor;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.hills.HillObjective;
import network.walrus.games.octc.hills.domination.overtime.DominationOvertimeFacet;
import network.walrus.utils.bukkit.visual.display.DisplayManager;
import network.walrus.utils.bukkit.visual.display.DisplayPane;
import network.walrus.utils.bukkit.visual.renderable.StaticRenderable;
import network.walrus.utils.core.color.NetworkColorConstants;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.CP;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;

/**
 * A domination pane, displays the domination point on the scoreboard
 *
 * @author Matthew Arnold
 */
public class DomPane extends DisplayPane {

  public static final String INCOMPLETE = "\u29be"; // ⦾
  public static final String COMPLETE = "\u29bf"; // ⦿

  private final List<HillObjective> objectives;
  private final Optional<DominationOvertimeFacet> domination;

  /**
   * Creates a new domination pane
   *
   * @param objectives the hill objectives to display on the display pane
   * @param gameRound the current game round
   * @param manager the display manager for the display
   */
  public DomPane(
      List<HillObjective> objectives,
      Optional<DominationOvertimeFacet> domination,
      GameRound gameRound,
      DisplayManager manager) {
    super(manager);
    this.objectives = objectives;
    this.domination = domination;
    setTitle(CP.SCOREBOARD_HEADER.apply(gameRound.map().game().name()));
    setPane();
  }

  private void setPane() {
    domination
        .map(this::addDomination)
        .ifPresent(
            x -> {
              addElement(x);
              addSpacer();
            });
    for (HillObjective objective : objectives) {
      StaticRenderable staticRenderable = transformHill(objective);
      addElement(staticRenderable);
    }
  }

  private StaticRenderable addDomination(DominationOvertimeFacet domination) {
    return new StaticRenderable(DominationOvertimeFacet.SCOREBOARD_TIME) {
      @Override
      public Localizable[] text() {
        return new Localizable[] {
          DominationOvertimeFacet.DOMINATION_FORMAT.with(domination.timeText())
        };
      }
    };
  }

  private StaticRenderable transformHill(HillObjective hillObjective) {
    UnlocalizedFormat format = new UnlocalizedFormat("{0} {1}");

    return new StaticRenderable(hillObjective.getName().translateDefault()) {
      @Override
      public Localizable[] text() {

        Localizable name = hillObjective.getColoredName();

        Localizable control;
        if (hillObjective.completionPercentage() != 0
            && hillObjective.completionPercentage() != 100) {

          LocalizedNumber number = new LocalizedNumber(hillObjective.completionPercentage());
          UnlocalizedFormat percentage = new UnlocalizedFormat("{0}%");
          control = percentage.with(styleOfCompetitor(hillObjective.highestCompetition()), number);
        } else if (hillObjective.owner().isPresent()) {
          // fully complete
          control =
              new UnlocalizedText(COMPLETE, NetworkColorConstants.Games.OCN.CP.COMPLETION_SYMBOL);
        } else {
          control =
              new UnlocalizedText(INCOMPLETE, NetworkColorConstants.Games.OCN.CP.COMPLETION_SYMBOL);
        }
        return new Localizable[] {format.with(control, name)};
      }
    };
  }

  private TextStyle styleOfCompetitor(Optional<Competitor> competitor) {
    return competitor.map(x -> x.getColor().style()).orElse(TextStyle.ofColor(ChatColor.WHITE));
  }
}
