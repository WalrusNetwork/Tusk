package network.walrus.games.octc.tdm;

import network.walrus.games.core.api.round.RoundFactory;
import network.walrus.games.octc.Match;
import network.walrus.games.octc.OCNGame;
import network.walrus.games.octc.score.PointBasedGame;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A classic game of team death match.
 *
 * @author Austin Mayes
 */
public class TDMGame extends OCNGame implements PointBasedGame {

  /**
   * Constructor.
   *
   * @param matchFactory used to create matches of this game type
   */
  public TDMGame(RoundFactory<Match> matchFactory) {
    super(matchFactory);
  }

  @Override
  public ItemStack getIcon() {
    return new ItemStack(Material.IRON_SWORD);
  }

  @Override
  public boolean canJoinMidMatch(boolean premium) {
    return true;
  }

  @Override
  public boolean canSpectateMidMatch() {
    return true;
  }

  @Override
  public String slug() {
    return "tdm";
  }

  @Override
  public String name() {
    return "Team Death Match";
  }
}
