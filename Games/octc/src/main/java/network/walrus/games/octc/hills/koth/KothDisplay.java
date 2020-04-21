package network.walrus.games.octc.hills.koth;

import java.util.List;
import java.util.Optional;
import net.md_5.bungee.api.ChatColor;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.hills.HillObjective;
import network.walrus.games.octc.score.ScoreFacet;
import network.walrus.utils.bukkit.visual.display.DisplayManager;
import network.walrus.utils.bukkit.visual.display.DisplayPane;
import network.walrus.utils.bukkit.visual.renderable.StaticRenderable;
import network.walrus.utils.core.color.NetworkColorConstants;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.KOTH;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;
import network.walrus.utils.parsing.facet.holder.FacetHolder;

/**
 * Scoreboard display for the king of the hill facet
 *
 * @author Matthew Arnold
 */
public class KothDisplay extends DisplayPane {

  private static final String INCOMPLETE = "\u29be"; // ⦾
  private static final String COMPLETE = "\u29bf"; // ⦿

  private static final String UNLIMITED = "{0} {1} ";
  private static final String LIMITED = "{0} {1}/{2} ";

  private static final char ARROW = '«';
  private final FacetHolder holder;
  private final ScoreFacet facet;

  private final Optional<Competitor> competitor;

  private final List<HillObjective> objectives;

  /**
   * Creates a new king of the hill display panel
   *
   * @param objectives the list of hill objectives
   * @param scoreFacet the score facet
   * @param gameRound the game round
   * @param manager the display manager for the display
   * @param competitor the competitor using the display
   */
  public KothDisplay(
      List<HillObjective> objectives,
      ScoreFacet scoreFacet,
      GameRound gameRound,
      DisplayManager manager,
      Optional<Competitor> competitor) {
    super(manager);
    this.objectives = objectives;
    this.holder = gameRound;
    this.facet = scoreFacet;
    this.competitor = competitor;
    setTitle(
        NetworkColorConstants.Games.OCN.CP.SCOREBOARD_HEADER.apply(gameRound.map().game().name()));
    setPane();
  }

  private void setPane() {
    addScoreElements();
    addSpacer();

    for (HillObjective objective : objectives) {
      StaticRenderable staticRenderable = transformHill(objective);
      addElement(staticRenderable);
    }
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

  private void addScoreElements() {
    GroupsManager manager = holder.getFacetRequired(GroupsManager.class);
    for (Competitor comp : manager.getCompetitors()) {
      TextStyle style = comp.getColor().style();
      boolean same = competitor.isPresent() && comp.equals(competitor.get());

      Optional<Integer> limit = facet.getObjective().getLimit();
      UnlocalizedFormat format =
          new UnlocalizedFormat((limit.isPresent() ? LIMITED : UNLIMITED) + (same ? (ARROW) : ""));

      if (same) {
        style.bold();
      }

      if (limit.isPresent()) {
        addElement(limited(comp, format, style));
      } else {
        addElement(noLimit(comp, format, style));
      }
    }
  }

  private TextStyle styleOfCompetitor(Optional<Competitor> competitor) {
    return competitor.map(x -> x.getColor().style()).orElse(TextStyle.ofColor(ChatColor.WHITE));
  }

  private StaticRenderable noLimit(
      Competitor competitor, UnlocalizedFormat format, TextStyle style) {
    return new StaticRenderable(competitor.id()) {
      @Override
      public Localizable[] text() {
        int score = facet.getObjective().getPoints(competitor);
        return new Localizable[] {format.with(style, new LocalizedNumber(score))};
      }
    };
  }

  private StaticRenderable limited(
      Competitor competitor, UnlocalizedFormat format, TextStyle style) {
    return new StaticRenderable(competitor.id()) {
      @Override
      public Localizable[] text() {
        int score = facet.getObjective().getPoints(competitor);
        return new Localizable[] {
          format.with(
              competitor.getName().toText(style),
              new LocalizedNumber(score, KOTH.SCORE),
              new LocalizedNumber(facet.getObjective().getLimit().get(), KOTH.SCORE_LIMIT))
        };
      }
    };
  }
}
