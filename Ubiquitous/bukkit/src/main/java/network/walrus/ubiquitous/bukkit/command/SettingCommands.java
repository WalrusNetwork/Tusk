package network.walrus.ubiquitous.bukkit.command;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.parametric.annotation.Default;
import app.ashcon.intake.parametric.annotation.Switch;
import app.ashcon.intake.parametric.annotation.Text;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import network.walrus.ubiquitous.bukkit.UbiquitousMessages;
import network.walrus.ubiquitous.bukkit.command.exception.MustBePlayerCommandException;
import network.walrus.ubiquitous.bukkit.settings.PlayerSettings;
import network.walrus.ubiquitous.bukkit.settings.Setting;
import network.walrus.ubiquitous.bukkit.settings.SettingValue;
import network.walrus.ubiquitous.bukkit.settings.SettingValueToggleable;
import network.walrus.utils.core.color.NetworkColorConstants.Settings;
import network.walrus.utils.core.color.NetworkColorConstants.Settings.Info;
import network.walrus.utils.core.command.exception.InvalidPaginationPageException;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.util.Paginator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commands for interacting with settings.
 *
 * @author Austin Mayes
 */
public class SettingCommands {

  /**
   * Set a setting to a specified value.
   *
   * @throws TranslatableCommandErrorException if the specified value is not recognized
   */
  @Command(
      aliases = {"set"},
      desc = "Set a setting to a specific value.",
      usage = "<name> <value>",
      min = 2)
  public static void set(@Sender Player player, Setting<Object> setting, String valueRaw)
      throws TranslatableCommandErrorException {
    Optional<SettingValue> value = (Optional<SettingValue>) setting.getType().parse(valueRaw);

    if (!value.isPresent()) {
      throw new TranslatableCommandErrorException(UbiquitousMessages.ERROR_SETTINGS_INVALID_VALUE);
    }

    PlayerSettings.store().set(player.getUniqueId(), setting, value.get().raw());

    Localizable name = setting.getName().duplicate();
    Localizable set = new UnlocalizedText(value.get().serialize());
    player.sendMessage(UbiquitousMessages.SETTINGS_SET.with(Settings.SET, name, set));
  }

  /**
   * View a setting's value.
   *
   * @throws TranslatableCommandErrorException if the target is null and the sender is not a player
   */
  @Command(
      aliases = {"setting"},
      desc = "See a setting's value and information.",
      min = 1,
      usage = "<name>",
      flags = "o:")
  public static void setting(
      @Sender CommandSender sender, Setting<Object> setting, @Switch('o') Player other)
      throws TranslatableCommandErrorException {
    final Player target =
        other != null && sender.hasPermission("settings.other.view")
            ? other
            : MustBePlayerCommandException.ensurePlayer(sender);
    // Header
    UnlocalizedText line =
        new UnlocalizedText("--------------", Info.HEADER_LINE.duplicate().strike());
    UnlocalizedFormat header = new UnlocalizedFormat("{0} {1} {2}");
    Localizable name = setting.getName().duplicate();
    sender.sendMessage(header.with(Info.HEADER_TEXT, line, name, line));

    // Summary
    Localizable summary = setting.getSummary().duplicate();
    summary.style().inherit(Info.SUMMARY_TEXT);
    sender.sendMessage(UbiquitousMessages.SETTINGS_SUMMARY.with(Info.SUMMARY_IDENTIFIER, summary));

    if (setting.getDescription().isPresent()) {
      // Description
      Localizable desc = setting.getDescription().get().duplicate();
      desc.style().inherit(Info.DESCRIPTION_TEXT);
      sender.sendMessage(
          UbiquitousMessages.SETTINGS_DESCRIPTION.with(Info.DESCRIPTION_IDENTIFIER, desc));
    }

    // Current value
    Object currentRaw = PlayerSettings.store().get(target.getUniqueId(), setting);
    String current = setting.getType().value(currentRaw).serialize();
    Localizable currentText = new UnlocalizedText(current, Info.CURRENT_VALUE_TEXT);

    sender.sendMessage(
        UbiquitousMessages.SETTiNGS_CURRENT.with(Info.CURRENT_VALUE_IDENTIFIER, currentText));

    // Default value
    Object defaultRaw = setting.getDefaultValue();
    String def = setting.getType().value(defaultRaw).serialize();
    Localizable defText = new UnlocalizedText(def, Info.DEFAULT_VALUE_TEXT);

    sender.sendMessage(
        UbiquitousMessages.SETTiNGS_DEFAULT.with(Info.DEFAULT_VALUE_IDENTIFIER, defText));

    if (setting.getType().value(setting.getDefaultValue()) instanceof SettingValueToggleable
        && target.getName().equals(sender.getName())) {
      Localizable toggle = UbiquitousMessages.SETTINGS_TOGGLE.with(Info.TOGGLE);
      toggle.style().italic();
      toggle
          .style()
          .click(
              new ClickEvent(
                  ClickEvent.Action.RUN_COMMAND, "/toggle " + name.render(sender).toPlainText()));

      sender.sendMessage(toggle);
    }
  }

  /**
   * View all available settings.
   *
   * @throws TranslatableCommandErrorException if the supplied page is out of bounds
   */
  @Command(
      aliases = {"settings"},
      desc = "List available settings.",
      usage = "[page/query]")
  public static void settings(@Sender CommandSender sender, @Text @Default("") String args)
      throws TranslatableCommandErrorException {
    List<Setting> list = new ArrayList<>(PlayerSettings.settings());

    String arg1 = args.split(" ").length == 0 ? "" : args.split(" ")[0];
    String arg2 = args.split(" ").length < 2 ? "" : args.split(" ")[1];

    int page = 1;
    if (!arg1.isEmpty()) {
      try {
        page = Integer.parseInt(arg1);
      } catch (Exception e) {
        // Not a number, maybe a query?

        Iterator<Setting> iterator = list.iterator();
        while (iterator.hasNext()) {
          Setting next = iterator.next();
          String name = next.getName().render(sender).toPlainText();
          if (!name.toLowerCase().contains(arg1.toLowerCase())) {
            iterator.remove();
          }
        }

        if (!arg2.isEmpty()) {
          try {
            page = Integer.parseInt(arg2);
          } catch (Exception e1) {
            return;
          }
        }
      }
    }

    // page index = page - 1
    page--;

    list.sort(
        (o1, o2) -> {
          String n1 = o1.getName().render(sender).toPlainText();
          String n2 = o2.getName().render(sender).toPlainText();
          return n1.compareTo(n2);
        });

    Paginator<Setting> paginator = new Paginator<>(list, 5);

    if (!paginator.hasPage(page)) {
      throw new InvalidPaginationPageException(paginator);
    }

    // Page Header
    UnlocalizedText line =
        new UnlocalizedText("--------------", Settings.List.HEADER_LINE.strike());
    UnlocalizedFormat header = new UnlocalizedFormat("{0} {1} ({2}/{3}) {4}");
    LocalizedNumber pageNumber = new LocalizedNumber(page + 1, Settings.List.CURRENT_PAGE);
    LocalizedNumber pagesNumber =
        new LocalizedNumber(paginator.getPageCount(), Settings.List.TOTAL_PAGES);
    Localizable title = UbiquitousMessages.SETTINGS_HEADER.with(Settings.List.HEADER_TEXT);
    sender.sendMessage(header.with(line, title, pageNumber, pagesNumber, line));

    // Setting Format
    UnlocalizedFormat format = new UnlocalizedFormat("{0}: {1}");

    // Click me!
    BaseComponent[] clickMe =
        new BaseComponent[] {UbiquitousMessages.CLICK_ME.with(Settings.List.HOVER).render(sender)};

    for (Setting setting : paginator.getPage(page)) {
      Localizable name = setting.getName().duplicate();
      name.style()
          .click(
              new ClickEvent(
                  ClickEvent.Action.RUN_COMMAND, "/setting " + name.render(sender).toPlainText()));
      name.style().hover(new HoverEvent(HoverEvent.Action.SHOW_TEXT, clickMe));
      name.style().italic();
      name.style().inherit(Settings.List.NAME);

      Localizable summary = setting.getSummary().duplicate();

      sender.sendMessage(format.with(Settings.List.SUMMARY, name, summary));
    }
  }

  /**
   * Toggle a setting.
   *
   * @throws TranslatableCommandErrorException if the setting specified is not toggleable.
   */
  @Command(
      aliases = {"toggle"},
      desc = "Toggle a setting between values.",
      min = 1,
      max = 1)
  public static void toggle(@Sender Player player, Setting<Object> setting)
      throws TranslatableCommandErrorException {
    Optional<Object> result = PlayerSettings.store().toggle((player).getUniqueId(), setting);

    if (result.isPresent()) {
      Localizable name = setting.getName().duplicate();
      Localizable value = new UnlocalizedText(setting.getType().value(result.get()).serialize());

      player.sendMessage(UbiquitousMessages.SETTINGS_SET.with(Settings.SET, name, value));
    } else {
      throw new TranslatableCommandErrorException(UbiquitousMessages.ERROR_SETTINGS_NOT_TOGGLE);
    }
  }
}
