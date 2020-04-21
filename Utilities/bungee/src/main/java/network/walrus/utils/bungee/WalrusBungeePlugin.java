package network.walrus.utils.bungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import network.walrus.utils.core.stage.Stage;

/**
 * Base class for all plugins which adds extra functionality on top of the base {@link Plugin} which
 * makes life easier and more streamlined.
 *
 * <p>This allows us to add global functionality to the {@link #onEnable()} and {@link #onDisable()}
 * callbacks which will be executed for each plugin.
 *
 * @author Austin Mayes
 */
public abstract class WalrusBungeePlugin extends Plugin {

  private static final Stage STAGE =
      Enum.valueOf(Stage.class, System.getProperty("stage", "production").toUpperCase());
  private static final String SERVER_ID = System.getenv("SERVER_ID");
  private static WalrusBungeePlugin instance;

  private Configuration configuration;

  public static Stage getStage() {
    return STAGE;
  }

  public static String getServerId() {
    return SERVER_ID;
  }

  /** @return the current instance of walrus plugin */
  public static WalrusBungeePlugin getWalrusPlugin() {
    return instance;
  }

  @Override
  public void onLoad() {
    loadConfig();
    load();
  }

  @Override
  public void onEnable() {
    if (instance == null) {
      instance = this;
    }
    enable();
  }

  @Override
  public void onDisable() {
    if (instance == this) {
      instance = null;
    }
    disable();
  }

  /** Called when the plugin is enabled after initial setup is complete. */
  public abstract void enable();

  /** Called when the plugin is disabled after initial teardown is complete. */
  public abstract void disable();

  /** Called when the plugin is loaded after initial setup is complete. */
  public abstract void load();

  public Configuration getConfiguration() {
    return configuration;
  }

  private void loadConfig() {
    if (!getDataFolder().exists()) {
      getDataFolder().mkdir();
    }

    File file = new File(getDataFolder(), "config.yml");

    if (!file.exists()) {
      try (InputStream in = getResourceAsStream("config.yml")) {
        Files.copy(in, file.toPath());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
      this.configuration =
          ConfigurationProvider.getProvider(YamlConfiguration.class)
              .load(new File(getDataFolder(), "config.yml"));
    } catch (IOException e) {
      e.printStackTrace();
      ProxyServer.getInstance().stop();
    }
  }
}
