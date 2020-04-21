package network.walrus.games.octc;

import network.walrus.games.core.api.game.Game;
import network.walrus.games.core.api.map.GameMap;
import network.walrus.games.core.api.round.RoundFactory;
import org.bukkit.inventory.ItemStack;

/**
 * A {@link Game} specific to the {@link OCNGameManager} component.
 *
 * @author Austin Mayes
 */
public abstract class OCNGame implements Game<Match> {

  private final RoundFactory<Match> matchFactory;

  /**
   * Constructor.
   *
   * @param matchFactory used to create matches
   */
  public OCNGame(RoundFactory<Match> matchFactory) {
    this.matchFactory = matchFactory;
  }

  @Override
  public Match constructRound(GameMap map) {
    return matchFactory.create(map);
  }

  /**
   * Construct an item to be used in UIs.
   *
   * @return a representation of this game type as an item
   */
  public abstract ItemStack getIcon();
}
