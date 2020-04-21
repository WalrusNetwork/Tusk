package network.walrus.games.octc.ctw;

import network.walrus.games.core.api.round.RoundFactory;
import network.walrus.games.octc.Match;
import network.walrus.games.octc.OCNGame;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A classic game of capture the wool.
 *
 * @author Austin Mayes
 */
public class CTWGame extends OCNGame {

  /**
   * Constructor.
   *
   * @param matchFactory used to create matches of this game type
   */
  public CTWGame(RoundFactory<Match> matchFactory) {
    super(matchFactory);
  }

  @Override
  public ItemStack getIcon() {
    return new ItemStack(Material.WOOL, 1, DyeColor.LIGHT_BLUE.getWoolData());
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
    return "ctw";
  }

  @Override
  public String name() {
    return "Capture the Wool";
  }
}
