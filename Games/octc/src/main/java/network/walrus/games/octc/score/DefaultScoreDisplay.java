package network.walrus.games.octc.score;

import java.util.Optional;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.bukkit.visual.display.DisplayManager;
import network.walrus.utils.bukkit.visual.display.DisplayPane;
import network.walrus.utils.bukkit.visual.renderable.StaticRenderable;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;
import network.walrus.utils.parsing.facet.holder.FacetHolder;

/**
 * Display pane which is used to render all scores for a specific {@link ScoreFacet} for a specific
 * {@link Competitor}.
 *
 * @author Austin Mayes
 */
public class DefaultScoreDisplay extends DisplayPane {

  private static final char ARROW = '«';
  private final FacetHolder holder;
  private final ScoreFacet facet;
  private final Optional<Competitor> competitor;
  private final String UNLIMITED = "{0} {1} ";
  private final String LIMITED = "{0} {1}/{2} ";

  /**
   * @param manager this pane is working inside of
   * @param holder that this display is operating in
   * @param facet used to get score data from
   * @param competitor that this display is for
   */
  public DefaultScoreDisplay(
      DisplayManager manager, GameRound holder, ScoreFacet facet, Optional<Competitor> competitor) {
    super(manager);
    this.holder = holder;
    this.facet = facet;
    this.competitor = competitor;
    setTitle(Games.OCN.TDM.SCOREBOARD_HEADER.apply(holder.map().game().name()));
    addElements();
  }

  private void addElements() {
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
      addElement(
          new StaticRenderable(comp.id()) {
            @Override
            public Localizable[] text() {
              Localizable compScore = new LocalizedNumber(facet.getObjective().getPoints(comp));
              Localizable localizedLimit = new LocalizedNumber(limit.orElse(0));
              return new Localizable[] {
                format.with(comp.getName().toText(style), compScore, localizedLimit)
              };
            }
          });
    }
  }
}
