package network.walrus.games.octc.ctf.flags;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import network.walrus.games.core.facets.stats.StatsFacet;
import network.walrus.games.core.facets.visual.PaneGroup;
import network.walrus.games.core.facets.visual.SidebarFacet;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.ctf.flags.events.FlagCaptureEvent;
import network.walrus.games.octc.global.results.WinCalculator;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.FacetLoadException;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Facet which manages all {@link FlagObjective}s alond with their corresponding {@link Post}s and
 * {@link Net}s.
 *
 * @author Austin Mayes
 */
public class FlagsFacet extends Facet implements Listener {

  private final GameRound round;
  private final List<FlagObjective> flags;
  private final List<Post> posts;
  private final List<Net> nets;
  private final WinCalculator winCalculator;
  private final FlagTask highlightTask;
  private final Map<Player, FlagObjective> carriers;

  /**
   * @param round the facet is operating in
   * @param flags this facet is responsible for
   * @param posts which flags can be spawned at
   * @param nets where flags can be captured
   */
  public FlagsFacet(GameRound round, List<FlagObjective> flags, List<Post> posts, List<Net> nets) {
    this.round = round;
    this.flags = flags;
    this.posts = posts;
    this.nets = nets;
    this.winCalculator =
        new WinCalculator(round, new ArrayList<>(this.flags), Lists.newArrayList());
    this.highlightTask = new FlagTask(flags);
    SidebarFacet.PANE_CREATE_FUNCTION =
        (m, c) -> new PaneGroup(Pair.of("flags", new FlagDisplay((GameRound) round, m, this, c)));
    this.carriers = Maps.newHashMap();
  }

  @Override
  public void load() throws FacetLoadException {
    for (FlagObjective wool : flags) { // "Wool"
      wool.initialize();
    }

    round
        .getFacet(StatsFacet.class)
        .ifPresent(statsFacet -> statsFacet.addTracker(new FlagTracker()));
  }

  @Override
  public void enable() {
    this.highlightTask.start();
  }

  @Override
  public void disable() {
    this.highlightTask.reset();
    UbiquitousBukkitPlugin.getInstance()
        .getCountdownManager()
        .cancelAll(c -> c instanceof FlagCountdown);
    for (FlagObjective flag : this.flags) {
      if (flag.isCarried() && flag.getCurrentLocation().isPresent()) {
        Location current = flag.getCurrentLocation().get();
        flag.spawn(flag.getCurrentLocation().get());
      }
    }
  }

  List<FlagObjective> getFlags() {
    return flags;
  }

  public List<Post> getPosts() {
    return posts;
  }

  public List<Net> getNets() {
    return nets;
  }

  /** Handle wins */
  @EventHandler
  public void onComplete(FlagCaptureEvent event) {
    this.winCalculator.check();
  }

  void carry(Player player, FlagObjective objective) {
    this.carriers.put(player, objective);
  }

  void drop(Player player) {
    this.carriers.remove(player);
  }

  Optional<FlagObjective> isCarrying(Player player) {
    return Optional.ofNullable(this.carriers.get(player));
  }
}
