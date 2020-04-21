package network.walrus.nerve.bukkit;

import app.ashcon.intake.bukkit.BukkitIntake;
import app.ashcon.intake.bukkit.graph.BasicBukkitCommandGraph;
import com.apollographql.apollo.ApolloClient;
import java.util.Optional;
import network.walrus.nerve.bukkit.command.NerveCommandModule;
import network.walrus.nerve.bukkit.listeners.ConnectionHandler;
import network.walrus.nerve.bukkit.listeners.SoundMessageListener;
import network.walrus.nerve.bukkit.user.BukkitPermissionHandler;
import network.walrus.nerve.bukkit.user.BukkitPrefixHandler;
import network.walrus.nerve.core.redis.RedisClient;
import network.walrus.nerve.core.redis.RedisConfig;
import network.walrus.utils.bukkit.WalrusBukkitPlugin;
import network.walrus.utils.core.player.PersonalizedPlayer;
import okhttp3.OkHttpClient;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;

/**
 * Plugin responsible for providing global functions with the use of the API.
 *
 * @author Austin Mayes
 */
public class NerveBukkitPlugin extends WalrusBukkitPlugin {

  private static NerveBukkitPlugin instance;

  private ApolloClient apiClient;
  private RedisClient redis;
  private boolean apiEnabled;
  private boolean redisEnabled;
  private BasicBukkitCommandGraph graph;
  private BukkitIntake intake;
  private BukkitPermissionHandler permissionHandler;
  private BukkitPrefixHandler prefixHandler;

  /** @return instance singleton */
  public static NerveBukkitPlugin instance() {
    return instance;
  }

  @Override
  public void enable() {
    instance = this;

    if (!apiEnabled) {
      return;
    }

    getServer()
        .getMessenger()
        .registerIncomingPluginChannel(
            this, SoundMessageListener.SUBCHANNEL, new SoundMessageListener());

    this.permissionHandler = new BukkitPermissionHandler();

    registerCommands();
    registerListeners();
    prefixHandler = new BukkitPrefixHandler();
    PersonalizedPlayer.PREFIX_GENERATOR = prefixHandler;
  }

  private void registerListeners() {
    PluginManager manager = Bukkit.getPluginManager();
    manager.registerEvents(this.permissionHandler, this);
    manager.registerEvents(new ConnectionHandler(), this);
  }

  private void registerCommands() {
    this.graph = new BasicBukkitCommandGraph(new NerveCommandModule());
    this.intake = new BukkitIntake(this, this.graph);
    intake.register();
  }

  @Override
  public void disable() {
    if (redis != null) {
      redis.disconnect();
    }
    instance = null;
  }

  @Override
  public void load() {
    initializeAPI();
    if (redis != null) {
      try {
        this.redis.connect();
      } catch (Exception e) {
        getLogger().severe("Failed to connect to Redis");
        e.printStackTrace();
        getServer().shutdown();
      }
    }
  }

  /** @return if the api is enabled. */
  public boolean isApiEnabled() {
    return apiEnabled;
  }

  /** @return if redis is enabled. */
  public boolean isRedisEnabled() {
    return redisEnabled;
  }

  private void initializeAPI() {
    ConfigurationSection api = this.getConfig().getConfigurationSection("api");
    this.apiEnabled = api.getBoolean("enabled", true);
    if (apiEnabled) {
      OkHttpClient httpClient = new OkHttpClient.Builder().build();
      apiClient =
          ApolloClient.builder().serverUrl(api.getString("host")).okHttpClient(httpClient).build();
    }

    ConfigurationSection redis = this.getConfig().getConfigurationSection("redis");
    this.redisEnabled = redis.getBoolean("enabled", true);
    if (redisEnabled) {
      this.redis =
          RedisClient.initialize(
              new RedisConfig(
                  redis.getString("host"),
                  redis.getInt("port", 6379),
                  Optional.ofNullable(redis.getString("password", null)),
                  redis.getInt("db"),
                  redis.getInt("threads")));
    }
  }

  public BukkitPermissionHandler getPermissionHandler() {
    return permissionHandler;
  }

  public ApolloClient getApiClient() {
    return apiClient;
  }

  public BukkitPrefixHandler getPrefixHandler() {
    return prefixHandler;
  }
}
