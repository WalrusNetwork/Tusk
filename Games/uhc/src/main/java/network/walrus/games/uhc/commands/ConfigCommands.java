package network.walrus.games.uhc.commands;

import app.ashcon.intake.Command;
import app.ashcon.intake.CommandException;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import java.time.Duration;
import java.util.logging.Level;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.UHCPermissions;
import network.walrus.games.uhc.UHCRound;
import network.walrus.games.uhc.config.UHCConfig;
import network.walrus.games.uhc.facets.redditbans.RedditBansFacet;
import network.walrus.ubiquitous.bukkit.UbiquitousMessages;
import network.walrus.utils.bukkit.logging.TranslatableLogRecord;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.UHC;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Config;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.util.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commands used to interact with the {@link UHCConfig}.
 *
 * @author Austin Mayes
 */
public class ConfigCommands {

  private final UHCConfig config;

  /** @param config which is being managed */
  public ConfigCommands(UHCConfig config) {
    this.config = config;
  }

  /**
   * Configure the flint drop chance
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = {"fdc", "flintdropchance"},
      desc = "Configure the flint drop chance",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void flintDropChance(@Sender CommandSender sender, double flintChance)
      throws CommandException {
    double old = config.flintChance.get();
    config.flintChance.set(flintChance);
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("flint drop chance"),
            new LocalizedNumber(old),
            new LocalizedNumber(flintChance)));
    notifyHosts(sender, "flint drop chance", Double.toString(flintChance));
  }

  /**
   * Configure the apple drop chance
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = {"adc", "appledropchance"},
      desc = "Configure the apple drop chance",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void appleDropChance(@Sender CommandSender sender, double appleChance)
      throws CommandException {
    double old = config.appleChance.get();
    config.appleChance.set(appleChance);
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("apple drop chance"),
            new LocalizedNumber(old),
            new LocalizedNumber(appleChance)));
    notifyHosts(sender, "apple drop chance", Double.toString(appleChance));
  }

  /**
   * Configure the amount of time before PVP is enabled
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = {"pvd", "pvpdelay"},
      desc = "Configure the amount of time before PVP is enabled",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void pvpDelay(@Sender CommandSender sender, Duration pvpDelay) throws CommandException {
    preventPostCreationChange();
    Duration old = config.pvpDelay.get();
    config.pvpDelay.set(pvpDelay);
    String newDurr = StringUtils.secondsToClock((int) pvpDelay.getSeconds());
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("pvp delay"),
            new UnlocalizedText(StringUtils.secondsToClock((int) old.getSeconds())),
            new UnlocalizedText(newDurr)));
    notifyHosts(sender, "pvp delay", newDurr);
  }

  /**
   * Configure the amount of time before the final heal
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = {"hd", "healdelay", "fh", "finalheal"},
      desc = "Configure the amount of time before the final heal",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void healDuration(@Sender CommandSender sender, Duration healDuration)
      throws CommandException {
    preventPostCreationChange();
    Duration old = config.finalHealAt.get();
    config.finalHealAt.set(healDuration);
    String newDurr = StringUtils.secondsToClock((int) healDuration.getSeconds());
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("final heal delay"),
            new UnlocalizedText(StringUtils.secondsToClock((int) old.getSeconds())),
            new UnlocalizedText(newDurr)));
    notifyHosts(sender, "final heal delay", newDurr);
  }

  /**
   * Configure the amount of time before permanent day is enabled
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = {"pd", "permaday"},
      desc = "Configure the amount of time before permanent day is enabled",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void permaDayDuration(@Sender CommandSender sender, Duration permaDayDuration)
      throws CommandException {
    preventPostStartedChange();
    Duration old = config.permaDayAt.get();
    config.permaDayAt.set(permaDayDuration);
    String newDurr = StringUtils.secondsToClock((int) permaDayDuration.getSeconds());
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("perma day delay"),
            new UnlocalizedText(StringUtils.secondsToClock((int) old.getSeconds())),
            new UnlocalizedText(newDurr)));
    notifyHosts(sender, "perma day delay", newDurr);
  }

  /**
   * Configure the death duration
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = {"td", "timeoutduration"},
      desc =
          "Configure the amount of time a player has to log back in after logging out during the game",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void deathDuration(@Sender CommandSender sender, Duration deathDuration)
      throws CommandException {
    Duration old = config.timeoutDelay.get();
    config.timeoutDelay.set(deathDuration);
    String newDurr = StringUtils.secondsToClock((int) deathDuration.getSeconds());
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("timeout duration"),
            new UnlocalizedText(StringUtils.secondsToClock((int) old.getSeconds())),
            new UnlocalizedText(newDurr)));
    notifyHosts(sender, "timeout duration", newDurr);
  }

  /**
   * Configure the starter food
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = {"sf", "starterfood"},
      desc = "Configure the starter food",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void starterFood(@Sender CommandSender sender, Material starterFoodType, int count)
      throws CommandException {
    Pair<Material, Integer> old = config.starterFood.get();
    config.starterFood.set(Pair.of(starterFoodType, count));
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("starter food"),
            new UnlocalizedText(old.getKey().name() + " (" + old.getValue() + ")"),
            new UnlocalizedText(starterFoodType.name() + " (" + count + ")")));
    notifyHosts(sender, "starter food", starterFoodType.name());
  }

  /**
   * Configure the initial border
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = {"initialborder", "ib"},
      desc = "Configure the initial border diameter",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void initialBorder(@Sender CommandSender sender, int diameter) throws CommandException {
    preventPostCreationChange();
    int old = config.initialBorder.get();
    config.initialBorder.set(diameter);
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("initial border"),
            new LocalizedNumber(old),
            new LocalizedNumber(diameter)));
    notifyHosts(sender, "initial border", Integer.toString(diameter));
  }

  /**
   * Configure if absorption is enabled
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = "absorption",
      desc = "Configure if absorption is enabled",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void absorption(@Sender CommandSender sender, boolean absorption) throws CommandException {
    boolean old = config.absorption.get();
    config.absorption.set(absorption);
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("absorption"),
            UbiquitousMessages.bool(old),
            UbiquitousMessages.bool(absorption)));
    notifyHosts(sender, "absorption", absorption ? "on" : "off");
  }

  /**
   * Configure if god apples are enabled
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = {"gaps", "ga", "godapples"},
      desc = "Configure if god apples are enabled",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void godApples(@Sender CommandSender sender, boolean godApples) throws CommandException {
    boolean old = config.godApples.get();
    config.godApples.set(godApples);
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("god apples"),
            UbiquitousMessages.bool(old),
            UbiquitousMessages.bool(godApples)));
    notifyHosts(sender, "god apples", godApples ? "on" : "off");
  }

  /**
   * Configure if ender pearl damage is enabled
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = {"epd", "enderpearldamage"},
      desc = "Configure if ender pearl damage is enabled",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void enderPearlDamage(@Sender CommandSender sender, int enderPearlDamage)
      throws CommandException {
    int old = config.enderPearlDamage.get();
    config.enderPearlDamage.set(enderPearlDamage);
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("ender pearl damage"),
            new LocalizedNumber(old),
            new LocalizedNumber(enderPearlDamage)));
    notifyHosts(sender, "ender pearl damage", Integer.toString(enderPearlDamage));
  }

  /**
   * Configure the player count
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = {"playercount", "pc"},
      desc = "Configure the amount of allowed players",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void playerCount(@Sender CommandSender sender, int teamCount) throws CommandException {
    preventPostStartedChange();
    int old = config.playerCount.get();
    config.playerCount.set(teamCount);
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("player count"),
            new LocalizedNumber(old),
            new LocalizedNumber(teamCount)));
    notifyHosts(sender, "player count", Integer.toString(teamCount));
  }

  /**
   * Configure the team size
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = {"teamsize", "ts"},
      desc = "Configure the size of each team. Use \"1\" for FFA",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void teamSize(@Sender CommandSender sender, int teamSize) throws CommandException {
    preventPostCreationChange();
    int old = config.teamSize.get();
    config.teamSize.set(teamSize);
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("team size"),
            new LocalizedNumber(old),
            new LocalizedNumber(teamSize)));
    notifyHosts(sender, "team size", Integer.toString(teamSize));
  }

  /**
   * Configure if the nether is enabled
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = "nether",
      desc = "Configure whether the nether is enabled",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void nether(@Sender CommandSender sender, boolean nether) throws CommandException {
    preventPostCreationChange();
    boolean old = config.nether.get();
    config.nether.set(nether);
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("nether"),
            UbiquitousMessages.bool(old),
            UbiquitousMessages.bool(nether)));
    notifyHosts(sender, "nether", nether ? "on" : "off");
  }

  /**
   * Configure if the end is enabled
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = "end",
      desc = "Configure whether the end is enabled",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void end(@Sender CommandSender sender, boolean end) throws CommandException {
    preventPostCreationChange();
    boolean old = config.end.get();
    config.end.set(end);
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("end"),
            UbiquitousMessages.bool(old),
            UbiquitousMessages.bool(end)));
    notifyHosts(sender, "end", end ? "on" : "off");
  }

  /**
   * Configure if strength two potions are enabled
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = {"st", "strengthTwo"},
      desc = "Configure if strength two potions are enabled",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void strenghTwo(@Sender CommandSender sender, boolean strengthTwo)
      throws CommandException {
    preventPostCreationChange();
    boolean old = config.strengthTwo.get();
    config.strengthTwo.set(strengthTwo);
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("strength two"),
            UbiquitousMessages.bool(old),
            UbiquitousMessages.bool(strengthTwo)));
    notifyHosts(sender, "strength two", strengthTwo ? "on" : "off");
  }

  /**
   * Configure if potions are enabled
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = "potions",
      desc = "Configure if potions are enabled",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void potions(@Sender CommandSender sender, boolean potions) throws CommandException {
    preventPostCreationChange();
    boolean old = config.potions.get();
    config.potions.set(potions);
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("potions"),
            UbiquitousMessages.bool(old),
            UbiquitousMessages.bool(potions)));
    notifyHosts(sender, "potions", potions ? "on" : "off");
  }

  /**
   * Configure if golden heads are enabled
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = {"goldenheads", "gh"},
      desc = "Configure if golden heads are enabled",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void goldenHeads(@Sender CommandSender sender, boolean goldenHead)
      throws CommandException {
    preventPostCreationChange();
    boolean old = config.goldenHead.get();
    config.goldenHead.set(goldenHead);
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("golden heads"),
            UbiquitousMessages.bool(old),
            UbiquitousMessages.bool(goldenHead)));
    notifyHosts(sender, "golden heads", goldenHead ? "on" : "off");
  }

  /**
   * Configure if the UBL should be enforced
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = "ubl",
      desc = "Configure if the Reddit ban list is enforced",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void banList(@Sender CommandSender sender, boolean banList) throws CommandException {
    boolean old = config.redditBanListEnabled.get();
    config.redditBanListEnabled.set(banList);
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("reddit ban list"),
            UbiquitousMessages.bool(old),
            UbiquitousMessages.bool(banList)));
    notifyHosts(sender, "reddit ban list", banList ? "on" : "off");
    if (banList) {
      UHCRound round = UHCManager.instance.getUHC();
      if (round != null) {
        round.getFacet(RedditBansFacet.class).ifPresent(RedditBansFacet::kickBannedPlayers);
      }
    }
  }

  @Command(
      aliases = "lightning",
      desc = "Configure if lightning should strike when a player dies",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void lightning(@Sender CommandSender sender, boolean lightning) throws CommandException {
    preventPostStartedChange();
    boolean old = config.lightningOnDeath.get();
    config.lightningOnDeath.set(lightning);
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("death lightning"),
            UbiquitousMessages.bool(old),
            UbiquitousMessages.bool(lightning)));
    notifyHosts(sender, "death lightning", lightning ? "on" : "off");
  }

  @Command(
      aliases = {"fireenchants", "fire"},
      desc = "Configure if fire enchants are allowed",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void fireEnchants(@Sender CommandSender sender, boolean fire) throws CommandException {
    preventPostStartedChange();
    boolean old = config.flameEnchants.get();
    config.flameEnchants.set(fire);
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("fire enchants"),
            UbiquitousMessages.bool(old),
            UbiquitousMessages.bool(fire)));
    notifyHosts(sender, "fire enchants", fire ? "on" : "off");
  }

  @Command(
      aliases = {"horses"},
      desc = "Configure if horses are allowed",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void horses(@Sender CommandSender sender, boolean horses) throws CommandException {
    preventPostStartedChange();
    boolean old = config.horse.get();
    config.horse.set(horses);
    sender.sendMessage(
        UHCMessages.CONFIG_UPDATE.with(
            Config.UPDATE,
            new UnlocalizedText("horses"),
            UbiquitousMessages.bool(old),
            UbiquitousMessages.bool(horses)));
    notifyHosts(sender, "horses", horses ? "on" : "off");
  }

  private void notifyHosts(CommandSender who, String changed, String newValue) {
    UHCManager.instance
        .hostLogger()
        .log(
            new TranslatableLogRecord(
                Level.INFO,
                UHCMessages.CONFIG_UPDATE_NOTIFICATION.with(
                    Config.UPDATE_NOTIFICATION,
                    new PersonalizedBukkitPlayer(who),
                    new UnlocalizedText(changed),
                    new UnlocalizedText(newValue))));
  }

  /**
   * Apply the config and create the UHC
   *
   * @throws CommandException if the config cannot be modified
   */
  @Command(
      aliases = "apply",
      desc = "Apply the config and create the UHC",
      perms = UHCPermissions.UPDATE_CONFIG)
  public void apply(@Sender CommandSender sender) throws CommandException {
    preventPostCreationChange();
    sender.sendMessage(UHCMessages.CONFIG_APPLIED.with(Config.APPLIED));
    UHCManager.instance.createUHC();
    UHC.Config.APPLIED.play(Bukkit.getOnlinePlayers());
    UHCManager.instance
        .hostLogger()
        .log(
            new TranslatableLogRecord(
                Level.INFO,
                UHCMessages.CONFIG_APPLY_NOTIFICATION.with(
                    Config.APPLIED_NOTIFICATION, new PersonalizedBukkitPlayer(sender))));
  }

  /** View the config */
  @Command(aliases = "view", desc = "View the config")
  public void view(@Sender CommandSender sender) {
    if (sender instanceof Player) {
      config.showConfigUI((Player) sender);
    } else {
      print(sender);
    }
  }

  /** Print the config */
  @Command(aliases = "print", desc = "Print the config")
  public void print(@Sender CommandSender sender) {
    for (Localizable localizable : config.print()) {
      sender.sendMessage(localizable);
    }
  }

  private void preventPostCreationChange() throws CommandException {
    if (UHCManager.instance.getUHC() != null) {
      throw new TranslatableCommandErrorException(UHCMessages.CANNOT_CONFIGURE);
    }
  }

  private void preventPostStartedChange() throws CommandException {
    if (UHCManager.instance.getUHC() != null
        && (UHCManager.instance.getUHC().getState().starting()
            || UHCManager.instance.getUHC().getState().started())) {
      throw new TranslatableCommandErrorException(UHCMessages.CANNOT_CONFIGURE);
    }
  }
}
