package network.walrus.games.core.commands;

import app.ashcon.intake.parametric.binder.Binder;
import network.walrus.ubiquitous.bukkit.command.UbiquitousCommandModule;

/**
 * Command module for provisions for all commands used in the games plugin. This is also the
 * superclass for all of the command modules for all of the external components.
 *
 * @author Austin Mayes
 */
public class GamesCoreCommandModule extends UbiquitousCommandModule {

  @Override
  public void configure(Binder binder) {
    super.configure(binder);
  }
}
