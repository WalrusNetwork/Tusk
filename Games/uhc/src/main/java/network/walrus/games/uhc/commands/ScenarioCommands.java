package network.walrus.games.uhc.commands;

import app.ashcon.intake.Command;
import app.ashcon.intake.CommandException;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.parametric.annotation.Default;
import app.ashcon.intake.parametric.annotation.Text;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.UHCPermissions;
import network.walrus.games.uhc.scenarios.Scenario;
import network.walrus.games.uhc.scenarios.ScenarioManager;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.UHC.Scenarios.Commands;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Scenarios;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Scenarios.List;
import network.walrus.utils.core.command.exception.InvalidPaginationPageException;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.util.Paginator;
import network.walrus.utils.core.util.StringUtils;
import org.bukkit.command.CommandSender;

/**
 * Commands for managing and viewing {@link Scenario}s.
 *
 * @author Austin Mayes
 */
public class ScenarioCommands {

  private static void list(
      CommandSender sender, int page, Set<Scenario> scenarios, LocalizedFormat headerFormat)
      throws CommandException {
    page = page - 1;
    Paginator<Scenario> paginator = new Paginator<>(scenarios, 5);
    Collection<Scenario> list;
    try {
      list = paginator.getPage(page);
    } catch (IllegalArgumentException e) {
      throw new InvalidPaginationPageException(paginator);
    }

    Localizable page1 = new UnlocalizedText((page + 1) + "", List.CURRENT_PAGE_COLOR);
    Localizable page2 = new UnlocalizedText(paginator.getPageCount() + "", List.TOTAL_PAGES_COLOR);

    Localizable header = headerFormat.with(List.HEADER_COLOR, page1, page2);
    sender.sendMessage(header);
    for (Scenario scenario : list) {
      UnlocalizedFormat format = new UnlocalizedFormat("{0}: {1}");
      UnlocalizedText name = new UnlocalizedText(scenario.name(), List.SCENARIO_NAME_COLOR);
      Localizable desc =
          scenario.description().length == 0
              ? new UnlocalizedText("TODO", List.SCENARIO_DESCRIPTION_COLOR)
              : scenario.description()[0].with(List.SCENARIO_DESCRIPTION_COLOR);
      if (UHCManager.instance.getScenarioManager().isActive(scenario)) {
        name.style().inherit(List.SCENARIO_ACTIVE_COLOR);
      }
      sender.sendMessage(format.with(List.DELIMITER_COLOR, name, desc));
    }
  }

  /**
   * List all active scenarios
   *
   * @throws CommandException if the supplied page is out of bounds
   */
  @Command(
      aliases = {"scenarios", "scs", "scen"},
      desc = "List all active scenarios")
  public void listCommands(@Sender CommandSender sender, @Default("1") int page)
      throws CommandException {
    list(
        sender,
        page,
        UHCManager.instance.getScenarioManager().getActive(),
        UHCMessages.UI_ACTIVE_SCENARIOS);
  }

  /**
   * List all active scenarios
   *
   * @throws CommandException if the supplied page is out of bounds
   */
  @Command(
      aliases = {"activescenarios", "ac"},
      desc = "List all active scenarios")
  public void active(@Sender CommandSender sender, @Default("1") int page) throws CommandException {
    list(
        sender,
        page,
        UHCManager.instance.getScenarioManager().getActive(),
        UHCMessages.UI_ACTIVE_SCENARIOS);
  }

  public static class ManagementCommands {

    private final ScenarioManager scenarioManager;

    /** @param scenarioManager where the scenarios are stored */
    public ManagementCommands(ScenarioManager scenarioManager) {
      this.scenarioManager = scenarioManager;
    }

    /**
     * List all scenarios
     *
     * @throws CommandException if the supplied page is out of bounds
     */
    @Command(aliases = "list", desc = "List all scenarios")
    public void list(@Sender CommandSender sender, @Default("1") int page) throws CommandException {
      ScenarioCommands.list(
          sender,
          page,
          UHCManager.instance.getScenarioManager().getRegistered(),
          UHCMessages.UI_SCENARIOS);
    }

    /**
     * List all active scenarios
     *
     * @throws CommandException if the supplied page is out of bounds
     */
    @Command(aliases = "active", desc = "List all active scenarios")
    public void active(@Sender CommandSender sender, @Default("1") int page)
        throws CommandException {
      ScenarioCommands.list(
          sender,
          page,
          UHCManager.instance.getScenarioManager().getActive(),
          UHCMessages.UI_ACTIVE_SCENARIOS);
    }

    /**
     * Enable a scenario
     *
     * @throws CommandException if the scenario was not found
     */
    @Command(
        aliases = "enable",
        desc = "Enable a scenario",
        perms = UHCPermissions.SCENARIO_MANAGE_PERM)
    public void enable(@Sender CommandSender sender, @Text String query) throws CommandException {
      Scenario scenario = getScenario(query, scenarioManager.getRegistered());
      scenarioManager.activate(scenario);
      Commands.ENABLED.play(sender);
      sender.sendMessage(
          UHCMessages.prefix(
              UHCMessages.SCENARIO_ACTIVATED.with(
                  Scenarios.ENABLED, new UnlocalizedText(scenario.name()))));
    }

    /**
     * Disable a scenario
     *
     * @throws CommandException if the scenario was not found
     */
    @Command(
        aliases = "disable",
        desc = "Disable a scenario",
        perms = UHCPermissions.SCENARIO_MANAGE_PERM)
    public void disable(@Sender CommandSender sender, @Text String query) throws CommandException {
      Scenario scenario = getScenario(query, scenarioManager.getActive());
      scenarioManager.deActivate(scenario);
      Commands.DISABLED.play(sender);
      sender.sendMessage(
          UHCMessages.prefix(
              UHCMessages.SCENARIO_DEACTIVATED.with(
                  Scenarios.DISABLED, new UnlocalizedText(scenario.name()))));
    }

    @Command(
        aliases = {},
        desc = "List all active scenarios")
    public void fallback(@Sender CommandSender sender) throws CommandException {
      active(sender, 1);
    }

    private Scenario getScenario(String query, Set<Scenario> set) throws CommandException {
      Optional<Scenario> scenarioSearch = scenarioManager.search(query);

      if (scenarioSearch.isPresent()) {
        return scenarioSearch.get();
      } else {
        throw new TranslatableCommandErrorException(
            UHCMessages.SCENARIO_NOT_FOUND,
            new UnlocalizedText(StringUtils.join(new ArrayList<>(set), ",", Scenario::name)));
      }
    }
  }
}
