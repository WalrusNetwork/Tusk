package network.walrus.games.octc.hills.koth;

import network.walrus.games.core.api.round.RoundFactory;
import network.walrus.games.octc.Match;
import network.walrus.games.octc.OCNGame;
import network.walrus.games.octc.score.PointBasedGame;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A King of the Hill Game (not yet implemented)
 *
 * @author Matthew Arnold
 */
public class KothGame extends OCNGame implements PointBasedGame {

  public KothGame(RoundFactory<Match> matchFactory) {
    super(matchFactory);
  }

  @Override
  public ItemStack getIcon() {
    return new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1);
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
    return "koth";
  }

  @Override
  public String name() {
    return "King Of The Hill";
  }
}
