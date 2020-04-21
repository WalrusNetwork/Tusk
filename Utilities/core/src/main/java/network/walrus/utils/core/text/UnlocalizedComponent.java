package network.walrus.utils.core.text;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import network.walrus.common.CommandSender;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;

/**
 * A {@link BaseComponent} that can be styled like a {@link Localizable}.
 *
 * @author Avicus Network
 */
public class UnlocalizedComponent implements Localizable {

  private final BaseComponent component;
  private TextStyle style;

  /** @see #UnlocalizedComponent(BaseComponent, TextStyle) */
  public UnlocalizedComponent(BaseComponent component) {
    this(component, null);
  }

  /**
   * Constructor.
   *
   * @param component to be styled
   * @param style to apply to the component
   */
  public UnlocalizedComponent(BaseComponent component, TextStyle style) {
    this.component = component;
    this.style = style;
  }

  @Override
  public TextStyle style() {
    if (this.style == null) {
      this.style = TextStyle.from(this.component);
    }

    return this.style;
  }

  @Override
  public Localizable duplicate() {
    return new UnlocalizedComponent(
        this.component, this.style != null ? this.style.duplicate() : null);
  }

  @Override
  public BaseComponent render(CommandSender viewer) {
    if (this.style != null) {
      this.style.apply(this.component);

      if (style.hoverText() != null) {
        this.component.setHoverEvent(
            new HoverEvent(
                Action.SHOW_TEXT, new BaseComponent[] {style.hoverText().render(viewer)}));
      }
    }

    return this.component;
  }
}
