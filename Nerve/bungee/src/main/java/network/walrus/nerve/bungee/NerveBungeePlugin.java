package network.walrus.nerve.bungee;

import app.ashcon.intake.bungee.BungeeIntake;
import app.ashcon.intake.bungee.graph.BasicBungeeCommandGraph;
import java.util.Optional;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import network.walrus.nerve.core.redis.RedisClient;
import network.walrus.nerve.core.redis.RedisConfig;
import network.walrus.utils.bungee.WalrusBungeePlugin;

/**
 * Main plugin class for the bungee version of the API.
 *
 * @author Shiny
 */
public class NerveBungeePlugin extends WalrusBungeePlugin {

  private static NerveBungeePlugin plugin;
  private RedisClient redis;
  private BasicBungeeCommandGraph graph;
  private BungeeIntake intake;

  public static NerveBungeePlugin getPlugin() {
    return plugin;
  }

  @Override
  public void load() {
    initializeAPI();
    if (redis != null) {
      try {
        redis.connect();
      } catch (Exception e) {
        getLogger().severe("Failed to connect to Redis");
        e.printStackTrace();
        ProxyServer.getInstance().stop();
      }
    }

    this.graph = new BasicBungeeCommandGraph();
    this.intake = new BungeeIntake(this, this.graph);

    plugin = this;
  }

  @Override
  public void enable() {
    registerListeners();
    registerCommands();
  }

  private void registerListeners() {
    PluginManager manager = getProxy().getPluginManager();
  }

  private void registerCommands() {
    intake.register();
  }

  @Override
  public void disable() {
    if (redis != null) {
      redis.disconnect();
    }
  }

  private void initializeAPI() {
    Configuration api = getConfiguration().getSection("api");
    if (api.getBoolean("enabled", true)) {}

    Configuration redis = getConfiguration().getSection("redis");
    if (redis.getBoolean("enabled", true)) {
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

  /** @return redis client */
  public RedisClient getRedis() {
    return redis;
  }

  /** @return the unique ID of the bungee instance */
  public String serverId() {
    return "TODO";
  }
}
