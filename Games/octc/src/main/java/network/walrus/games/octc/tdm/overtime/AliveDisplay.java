package network.walrus.games.octc.tdm.overtime;

import java.util.Optional;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.utils.bukkit.visual.display.DisplayManager;
import network.walrus.utils.bukkit.visual.display.DisplayPane;
import network.walrus.utils.bukkit.visual.renderable.StaticRenderable;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;

/**
 * Display pane that renders all the alive players on all teams
 *
 * @author David Rodriguez
 */
public class AliveDisplay extends DisplayPane {

  private static final char ARROW = '«';
  private final FacetHolder holder;
  private final String ALIVE = "{0} {1} ";
  private Optional<Competitor> competitor;

  /**
   * @param manager this pane is working inside of
   * @param holder used to get score data from
   * @param competitor that this display is for
   */
  public AliveDisplay(DisplayManager manager, FacetHolder holder, Optional<Competitor> competitor) {
    super(manager);
    this.holder = holder;
    this.competitor = competitor;
    setTitle("§c§lBlitz");
    addElements();
  }

  private void addElements() {
    GroupsManager manager = holder.getFacetRequired(GroupsManager.class);
    for (Competitor comp : manager.getCompetitors()) {
      TextStyle style = comp.getColor().style();
      boolean same = competitor.isPresent() && comp.equals(competitor.get());
      UnlocalizedFormat format = new UnlocalizedFormat(ALIVE + (same ? (ARROW) : ""));
      addElement(
          new StaticRenderable(comp.id()) {
            @Override
            public Localizable[] text() {
              Localizable name = comp.getName().toText(style);
              int count = 0;
              for (Player player : comp.getGroup().getPlayers()) {
                if (!manager.isDead(player)) {
                  count++;
                }
              }
              Localizable alive = new LocalizedNumber(count);
              return new Localizable[] {format.with(name, alive)};
            }
          });
    }
  }
}
