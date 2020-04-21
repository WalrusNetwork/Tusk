package network.walrus.games.octc.hills.domination;

import network.walrus.games.core.api.round.RoundFactory;
import network.walrus.games.octc.Match;
import network.walrus.games.octc.OCNGame;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a game of Control Point
 *
 * @author Matthew Arnold
 */
public class DomGame extends OCNGame {

  public DomGame(RoundFactory<Match> matchFactory) {
    super(matchFactory);
  }

  @Override
  public ItemStack getIcon() {
    return new ItemStack(Material.GOLDEN_APPLE);
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
    return "dom";
  }

  @Override
  public String name() {
    return "Domination";
  }
}
