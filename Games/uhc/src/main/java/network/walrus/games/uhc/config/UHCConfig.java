package network.walrus.games.uhc.config;

import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.List;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.ubiquitous.bukkit.config.Config;
import network.walrus.ubiquitous.bukkit.config.ConfigEntry;
import network.walrus.ubiquitous.bukkit.config.ConfigGroup;
import network.walrus.utils.core.color.NetworkColorConstants;
import network.walrus.utils.core.translation.Localizable;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

/**
 * Config which determines various pre-creation options for all UHC games.
 *
 * @author Rafi Baum
 */
public class UHCConfig implements Listener {
  private final Config config;
  // World Stuff
  public ConfigEntry<Boolean> end = new ConfigEntry<>(UHCMessages.CONFIG_END, false);
  public ConfigEntry<Boolean> nether = new ConfigEntry<>(UHCMessages.CONFIG_NETHER, false);
  // Potions
  public ConfigEntry<Boolean> potions = new ConfigEntry<>(UHCMessages.CONFIG_POTIONS, true);
  public ConfigEntry<Boolean> strengthTwo =
      new ConfigEntry<>(UHCMessages.CONFIG_STRENGTH_TWO, false);
  // Game Mechanics
  public ConfigEntry<Boolean> goldenHead = new ConfigEntry<>(UHCMessages.CONFIG_GOLDEN_HEAD, true);
  public ConfigEntry<Integer> initialBorder =
      new ConfigEntry<>(UHCMessages.CONFIG_INITIAL_BORDER, 1000);
  public ConfigEntry<Boolean> flameEnchants =
      new ConfigEntry<>(UHCMessages.CONFIG_FIRE_ENCHANTS, true);
  public ConfigEntry<Boolean> horse = new ConfigEntry<>(UHCMessages.CONFIG_HORSES, true);
  // Players
  public ConfigEntry<Integer> teamSize = new ConfigEntry<>(UHCMessages.CONFIG_TEAM_SIZE, 1);
  public ConfigEntry<Integer> playerCount = new ConfigEntry<>(UHCMessages.CONFIG_PLAYER_COUNT, 100);
  public ConfigEntry<Integer> enderPearlDamage =
      new ConfigEntry<>(UHCMessages.CONFIG_ENDER_PEARL_DAMAGE, 1);
  // Apples
  public ConfigEntry<Boolean> godApples = new ConfigEntry<>(UHCMessages.CONFIG_GOD_APPLES, false);
  public ConfigEntry<Boolean> absorption = new ConfigEntry<>(UHCMessages.CONFIG_ABSORPTION, false);
  // Starter Food
  public ConfigEntry<Pair<Material, Integer>> starterFood =
      new ConfigEntry<>(UHCMessages.CONFIG_STARTER_FOOD, Pair.of(Material.COOKED_BEEF, 10));
  // Delays
  public ConfigEntry<Duration> timeoutDelay =
      new ConfigEntry<>(UHCMessages.CONFIG_TIMEOUT_DELAY, Duration.ofMinutes(15));
  public ConfigEntry<Duration> permaDayAt =
      new ConfigEntry<>(UHCMessages.CONFIG_PERMA_DAY, Duration.ofHours(1));
  public ConfigEntry<Duration> finalHealAt =
      new ConfigEntry<>(UHCMessages.CONFIG_HEAL_DELAY, Duration.ofSeconds(30));
  public ConfigEntry<Duration> pvpDelay =
      new ConfigEntry<>(UHCMessages.CONFIG_PVP_DELAY, Duration.ofMinutes(10));
  // Rates
  public ConfigEntry<Double> appleChance = new ConfigEntry<>(UHCMessages.CONFIG_APPLE_CHANCE, 5.0);
  public ConfigEntry<Double> flintChance =
      new ConfigEntry<>(UHCMessages.CONFIG_FLINT_CHANCE, 100.0);
  // Reddit options
  public ConfigEntry<Boolean> redditBanListEnabled =
      new ConfigEntry<>(UHCMessages.CONFIG_REDDIT_BANS, true);
  // Death lightning
  public ConfigEntry<Boolean> lightningOnDeath =
      new ConfigEntry<>(UHCMessages.CONFIG_DEATH_LIGHTNING, true);

  public UHCConfig() {
    List<ConfigGroup> groups = Lists.newArrayList();

    groups.add(new ConfigGroup(UHCMessages.CONFIG_GROUP_WORLDS, Material.NETHERRACK, nether, end));
    groups.add(
        new ConfigGroup(
            UHCMessages.CONFIG_GROUP_PLAYER,
            new ItemStack(Material.SKULL_ITEM, 1, (short) 3),
            playerCount,
            teamSize));
    groups.add(
        new ConfigGroup(
            UHCMessages.CONFIG_GROUP_APPLE,
            Material.GOLDEN_APPLE,
            appleChance,
            absorption,
            godApples,
            goldenHead));
    groups.add(new StarterFoodGroup(starterFood));
    groups.add(
        new ConfigGroup(
            UHCMessages.CONFIG_GROUP_POTIONS,
            new Potion(PotionType.INSTANT_HEAL).toItemStack(1),
            potions,
            strengthTwo));
    groups.add(
        new ConfigGroup(
            UHCMessages.CONFIG_GROUP_TIMERS,
            Material.WATCH,
            finalHealAt,
            pvpDelay,
            permaDayAt,
            timeoutDelay));
    groups.add(
        new ConfigGroup(
            UHCMessages.CONFIG_GROUP_MISC,
            Material.ENDER_PEARL,
            enderPearlDamage,
            flintChance,
            lightningOnDeath,
            flameEnchants,
            horse));
    groups.add(
        new ConfigGroup(UHCMessages.CONFIG_GROUP_REDDIT, Material.BOOK, redditBanListEnabled));

    config =
        new Config(
            getConfigEntries(),
            groups,
            UHCMessages.CONFIG_UI_TITLE.with(NetworkColorConstants.Games.UHC.Config.TITLE));
    config.addSpacer(7);
  }

  /** @return a list of components explaining all values of this config */
  public List<Localizable> print() {
    return config.print();
  }

  /**
   * Shows a player the config UI
   *
   * @param player to show the config UI
   */
  public void showConfigUI(Player player) {
    config.showConfigUI(player);
  }

  private List<ConfigEntry<?>> getConfigEntries() {
    List<ConfigEntry<?>> entries = Lists.newArrayList();
    try {
      for (Field field : this.getClass().getDeclaredFields()) {
        if (field.getType() != ConfigEntry.class) {
          continue;
        }

        entries.add((ConfigEntry<?>) field.get(this));
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    return entries;
  }
}
