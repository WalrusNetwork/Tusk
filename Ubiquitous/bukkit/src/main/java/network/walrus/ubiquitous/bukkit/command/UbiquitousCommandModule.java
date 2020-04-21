package network.walrus.ubiquitous.bukkit.command;

import app.ashcon.intake.parametric.Key;
import app.ashcon.intake.parametric.binder.Binder;
import app.ashcon.intake.parametric.provider.EnumProvider;
import network.walrus.nerve.bukkit.command.NerveCommandModule;
import network.walrus.ubiquitous.bukkit.settings.Setting;
import network.walrus.ubiquitous.bukkit.settings.SettingsProvider;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.bukkit.Material;

/**
 * Command module for provisions for all commands used in the ubiquitous plugin.
 *
 * @author Austin Mayes
 */
public class UbiquitousCommandModule extends NerveCommandModule {

  @Override
  public void configure(Binder binder) {
    super.configure(binder);
    Key<Setting<Object>> key = Key.get(TypeUtils.parameterize(Setting.class, Object.class));
    binder.bind(key).toProvider(new SettingsProvider());
    binder.bind(Material.class).toProvider(new EnumProvider<>(Material.class));
  }
}
