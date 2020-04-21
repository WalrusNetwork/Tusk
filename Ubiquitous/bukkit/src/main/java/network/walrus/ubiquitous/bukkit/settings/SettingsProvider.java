package network.walrus.ubiquitous.bukkit.settings;

import app.ashcon.intake.argument.ArgumentException;
import app.ashcon.intake.argument.CommandArgs;
import app.ashcon.intake.argument.Namespace;
import app.ashcon.intake.bukkit.parametric.provider.BukkitProvider;
import app.ashcon.intake.parametric.ProvisionException;
import com.google.common.collect.Lists;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.UbiquitousMessages;
import org.bukkit.command.CommandSender;

/**
 * Provider which provides {@link Setting}s for commands.
 *
 * @author Austin Mayes
 */
public class SettingsProvider implements BukkitProvider<Setting<Object>> {

  @Nullable
  @Override
  public Setting get(CommandSender sender, CommandArgs args, List<? extends Annotation> mods)
      throws ArgumentException, ProvisionException {
    StringBuilder query = new StringBuilder();
    while (args.hasNext()) {
      query.append(args.next());
    }
    if (query.length() == 0) {
      // haven't provided a setting
      throw new ArgumentException("You must provide a setting alias");
    }

    Optional<Setting> found = Setting.search(sender, query.toString(), PlayerSettings.settings());
    if (!found.isPresent()) {
      throw new ArgumentException(
          UbiquitousMessages.ERROR_SETTING_NOT_FOUND.with().render(sender).toLegacyText());
    }

    return found.get();
  }

  @Override
  public List<String> getSuggestions(
      String prefix, Namespace namespace, List<? extends Annotation> modifiers) {
    List<String> suggestions = Lists.newArrayList();
    for (Setting setting : PlayerSettings.settings()) {
      suggestions.addAll(setting.getAllAliases(namespace.need(CommandSender.class)));
    }
    suggestions.removeIf(s -> !s.toLowerCase().startsWith(prefix.toLowerCase()));
    return suggestions;
  }

  @Override
  public String getName() {
    return "setting";
  }
}
