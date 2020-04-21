package network.walrus.ubiquitous.bukkit;

import app.ashcon.intake.bukkit.BukkitIntake;
import app.ashcon.intake.bukkit.graph.BasicBukkitCommandGraph;
import com.google.common.collect.Sets;
import com.keenant.tabbed.Tabbed;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.logging.Logger;
import network.walrus.ubiquitous.bukkit.boss.BossBarManager;
import network.walrus.ubiquitous.bukkit.boss.LegacyBossBarContext;
import network.walrus.ubiquitous.bukkit.chat.ChatCommands;
import network.walrus.ubiquitous.bukkit.chat.ChatManager;
import network.walrus.ubiquitous.bukkit.chat.filter.ChatFilter;
import network.walrus.ubiquitous.bukkit.chat.filter.IPFilter;
import network.walrus.ubiquitous.bukkit.chat.filter.WordFilter;
import network.walrus.ubiquitous.bukkit.command.SettingCommands;
import network.walrus.ubiquitous.bukkit.command.TrackerCommands;
import network.walrus.ubiquitous.bukkit.command.UbiquitousCommandModule;
import network.walrus.ubiquitous.bukkit.compat.CompatManager;
import network.walrus.ubiquitous.bukkit.countdown.CountdownCommands;
import network.walrus.ubiquitous.bukkit.countdown.CountdownManager;
import network.walrus.ubiquitous.bukkit.display.DisplayManagerImpl;
import network.walrus.ubiquitous.bukkit.doublejump.DoubleJumpManager;
import network.walrus.ubiquitous.bukkit.freeze.FreezeCommands;
import network.walrus.ubiquitous.bukkit.freeze.FreezeManager;
import network.walrus.ubiquitous.bukkit.inventory.InventoryListener;
import network.walrus.ubiquitous.bukkit.inventory.InventoryManager;
import network.walrus.ubiquitous.bukkit.item.DefuseListener;
import network.walrus.ubiquitous.bukkit.item.ItemAttributesHandler;
import network.walrus.ubiquitous.bukkit.listeners.BlockChangeListener;
import network.walrus.ubiquitous.bukkit.listeners.EntityChangeListener;
import network.walrus.ubiquitous.bukkit.listeners.GeneralListener;
import network.walrus.ubiquitous.bukkit.task.BetterRunnable;
import network.walrus.ubiquitous.bukkit.tracker.TrackerSupervisor;
import network.walrus.ubiquitous.bukkit.tracker.trackers.ExplosiveTracker;
import network.walrus.utils.bukkit.WalrusBukkitPlugin;
import network.walrus.utils.bukkit.logging.ChatLogHandler;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants;
import network.walrus.utils.bukkit.sound.SoundBundle;
import network.walrus.utils.bukkit.sound.SoundInjector;
import network.walrus.utils.bukkit.visual.display.DisplayManager;
import network.walrus.utils.core.color.NetworkColorConstants;
import network.walrus.utils.core.color.NetworkColorConstants.Moderation;
import network.walrus.utils.core.color.StyleBundle;
import network.walrus.utils.core.color.StyleInjector;
import network.walrus.utils.core.translation.GlobalLocalizations;
import network.walrus.utils.core.translation.MessageReferenceHolder;
import network.walrus.utils.core.translation.TranslationProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Bukkit utilities plugin main class.
 *
 * @author Austin Mayes
 */
public class UbiquitousBukkitPlugin extends WalrusBukkitPlugin {

  private static UbiquitousBukkitPlugin instance;
  private DisplayManager displayManager;
  private BossBarManager bossBarManager;
  private CompatManager compatManager;
  private CountdownManager countdownManager;
  private TrackerSupervisor trackerSupervisor;
  private FreezeManager freezeManager;
  private ChatManager chatManager;
  private DoubleJumpManager doubleJumpManager;
  private InventoryManager inventoryManager;
  private BasicBukkitCommandGraph graph;
  private BukkitIntake intake;
  private Logger moderationLogger;
  private Tabbed tabbed;

  public static UbiquitousBukkitPlugin getInstance() {
    return instance;
  }

  @Override
  public void load() {
    instance = this;
    displayManager = new DisplayManagerImpl(getServer().getPluginManager(), this);
    injectUI();
  }

  @Override
  public void enable() {
    this.tabbed = new Tabbed(this);
    displayManager.init();

    final PluginManager pm = this.getServer().getPluginManager();
    final BukkitScheduler scheduler = this.getServer().getScheduler();

    final LegacyBossBarContext legacyContext = new LegacyBossBarContext();
    scheduler.runTaskTimer(this, legacyContext, 0, 5 * 20);
    this.bossBarManager = new BossBarManager(legacyContext);
    pm.registerEvents(this.bossBarManager, this);
    scheduler.runTaskTimer(this, this.bossBarManager, 0, 5);

    this.compatManager = new CompatManager();
    pm.registerEvents(this.compatManager, this);

    this.countdownManager = new CountdownManager();
    pm.registerEvents(this.countdownManager, this);

    freezeManager = new FreezeManager();
    freezeManager.enable();

    Set<String> superBadWords = Sets.newHashSet(getConfig().getStringList("chat.super-bad-words"));
    Set<String> badWords = Sets.newHashSet(getConfig().getStringList("chat.bad-words"));

    chatManager =
        new ChatManager(
            new ChatFilter[] {
              new IPFilter(Sets.newHashSet(getConfig().getStringList("chat.allowed-domains"))),
              new WordFilter(superBadWords)
            },
            new ChatFilter[] {new WordFilter(badWords)});
    chatManager.enable();

    inventoryManager = new InventoryManager();

    doubleJumpManager = new DoubleJumpManager();
    doubleJumpManager.enable();

    trackerSupervisor = new TrackerSupervisor(this);
    trackerSupervisor.enable();
    registerListeners();
    registerCommands();

    // Load messages file
    UbiquitousMessages.DEFUSER_NAME.with();
    ((BetterRunnable) () -> MessageReferenceHolder.printUndefined(Bukkit.getLogger()))
        .runTaskLater(20 * 5, "translation-missing-alerter");
  }

  private void injectUI() {
    GlobalLocalizations.INSTANCE.setBundle(
        TranslationProvider.loadBundle(getConfig().getString("locales-path")));
    try {
      StyleBundle styleBundle = new StyleBundle();
      styleBundle.load(Paths.get(getConfig().getString("ui-path"), "styles"));

      SoundBundle soundBundle = new SoundBundle();
      soundBundle.load(Paths.get(getConfig().getString("ui-path"), "sounds"));

      StyleInjector.map(styleBundle, NetworkColorConstants.class, "");
      SoundInjector.map(soundBundle, NetworkSoundConstants.class, "");

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void disable() {
    if (trackerSupervisor != null) {
      trackerSupervisor.disable();
    }
    if (freezeManager != null) {
      freezeManager.disable();
    }
  }

  private void registerCommands() {
    this.graph = new BasicBukkitCommandGraph(new UbiquitousCommandModule());
    this.intake = new BukkitIntake(this, this.graph);
    graph.getRootDispatcherNode().registerCommands(new CountdownCommands());
    graph.getRootDispatcherNode().registerCommands(new SettingCommands());
    graph
        .getRootDispatcherNode()
        .registerCommands(new TrackerCommands(trackerSupervisor.getLifetimeManager()));
    graph.getRootDispatcherNode().registerCommands(new FreezeCommands(this.freezeManager));
    graph.getRootDispatcherNode().registerCommands(new ChatCommands(this.chatManager));
    intake.register();
  }

  public BossBarManager getBossBarManager() {
    return bossBarManager;
  }

  public CompatManager getCompatManager() {
    return compatManager;
  }

  public CountdownManager getCountdownManager() {
    return countdownManager;
  }

  private void registerListeners() {
    getServer()
        .getPluginManager()
        .registerEvents(
            new BlockChangeListener(
                trackerSupervisor.getManager().getTracker(ExplosiveTracker.class)),
            this);
    getServer().getPluginManager().registerEvents(new EntityChangeListener(), this);
    getServer().getPluginManager().registerEvents(new GeneralListener(), this);
    getServer().getPluginManager().registerEvents(new DefuseListener(), this);
    getServer().getPluginManager().registerEvents(new ItemAttributesHandler(), this);
    getServer()
        .getPluginManager()
        .registerEvents(new InventoryListener(this.inventoryManager), this);
  }

  public DisplayManager getDisplayManager() {
    return displayManager;
  }

  public TrackerSupervisor getTrackerSupervisor() {
    return trackerSupervisor;
  }

  public FreezeManager getFreezeManager() {
    return freezeManager;
  }

  /** @return Class that manages the Inventory GUI API */
  public InventoryManager getInventoryManager() {
    return inventoryManager;
  }

  /** @return Class that manages players who have selected the DoubleJumpKit */
  public DoubleJumpManager getDoubleJumpManager() {
    return doubleJumpManager;
  }

  public Tabbed getTabbed() {
    return tabbed;
  }

  /** @return logger which should be used to send local moderation information to */
  public Logger moderationLogger() {
    if (moderationLogger != null) {
      return moderationLogger;
    }

    moderationLogger = Logger.getLogger("moderation-alerts");
    moderationLogger.setUseParentHandlers(false);
    moderationLogger.addHandler(
        new ChatLogHandler(Moderation.PREFIX, "S", UbiquitousPermissions.LOCAL_MODERATION_ALERTS));
    return moderationLogger;
  }
}
