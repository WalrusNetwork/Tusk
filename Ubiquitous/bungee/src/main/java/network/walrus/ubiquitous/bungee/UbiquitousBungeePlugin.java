package network.walrus.ubiquitous.bungee;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import network.walrus.utils.bungee.WalrusBungeePlugin;
import network.walrus.utils.core.color.NetworkColorConstants;
import network.walrus.utils.core.color.StyleBundle;
import network.walrus.utils.core.color.StyleInjector;
import network.walrus.utils.core.translation.GlobalLocalizations;
import network.walrus.utils.core.translation.MessageReferenceHolder;
import network.walrus.utils.core.translation.TranslationProvider;

/**
 * Bungee utilities plugin main class.
 *
 * @author Austin Mayes
 */
public class UbiquitousBungeePlugin extends WalrusBungeePlugin {

  @Override
  public void enable() {
    getProxy()
        .getScheduler()
        .schedule(
            this, () -> MessageReferenceHolder.printUndefined(getLogger()), 5, TimeUnit.SECONDS);
  }

  @Override
  public void disable() {}

  @Override
  public void load() {
    injectUI();
  }

  private void injectUI() {
    GlobalLocalizations.INSTANCE.setBundle(
        TranslationProvider.loadBundle(getConfiguration().getString("locales-path")));
    try {
      StyleBundle styleBundle = new StyleBundle();
      styleBundle.load(Paths.get(getConfiguration().getString("ui-path"), "styles"));

      StyleInjector.map(styleBundle, NetworkColorConstants.class, "");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
