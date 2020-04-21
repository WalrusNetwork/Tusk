package network.walrus.utils.core.text;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import network.walrus.common.CommandSender;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;

/**
 * A {@link Localizable} that joins multiple {@link Localizable}s together.
 *
 * @author Avicus Network
 */
public class MultiPartLocalizable implements Localizable {

  private final Localizable[] args;

  /**
   * Constructor.
   *
   * @param args to be substituted in from placeholders
   */
  public MultiPartLocalizable(Localizable... args) {
    this.args = args;
  }

  @Override
  public TextStyle style() {
    throw new UnsupportedOperationException("Multipart localizables do not have styling.");
  }

  @Override
  public Localizable duplicate() {
    return new MultiPartLocalizable(this.args);
  }

  @Override
  public BaseComponent render(CommandSender viewer) {
    TextComponent message = new TextComponent(this.args[0].render(viewer));

    for (int i = 1; i < this.args.length; i++) {
      message.addExtra(this.args[i].render(viewer));
    }

    return message;
  }
}
