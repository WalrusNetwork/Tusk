package network.walrus.games.octc.ctf;

import network.walrus.games.core.api.round.RoundFactory;
import network.walrus.games.octc.Match;
import network.walrus.games.octc.OCNGame;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

/**
 * A classic game of capture the flag.
 *
 * @author Austin Mayes
 */
public class CTFGame extends OCNGame {

  /**
   * Constructor.
   *
   * @param matchFactory used to create matches of this game type
   */
  public CTFGame(RoundFactory<Match> matchFactory) {
    super(matchFactory);
  }

  @Override
  public ItemStack getIcon() {
    ItemStack item = new ItemStack(Material.BANNER);

    BannerMeta meta = (BannerMeta) item.getItemMeta();
    meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.FLOWER));
    meta.setBaseColor(DyeColor.RED);
    item.setItemMeta(meta);

    return item;
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
    return "ctf";
  }

  @Override
  public String name() {
    return "Capture the Flag";
  }
}
