package network.walrus.utils.core.text;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import network.walrus.common.CommandSender;
import network.walrus.utils.core.translation.LocaleBundle;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;

/**
 * Text which contains legacy color codes within the translation.
 *
 * @author Rafi Baum
 */
public class LegacyText implements Localizable {

  private final LocaleBundle bundle;
  private final String key;
  private final TextStyle style;

  /**
   * Constructor.
   *
   * @param bundle
   * @param key
   * @param style
   */
  public LegacyText(LocaleBundle bundle, String key, TextStyle style) {
    this.bundle = bundle;
    this.key = key;
    this.style = style;
  }

  /**
   * Constructor.
   *
   * @param bundle
   * @param key
   */
  public LegacyText(LocaleBundle bundle, String key) {
    this(bundle, key, TextStyle.create());
  }

  @Override
  public TextStyle style() {
    return style;
  }

  @Override
  public Localizable duplicate() {
    return new LegacyText(bundle, key, style.duplicate());
  }

  @Override
  public BaseComponent render(CommandSender viewer) {
    Optional<String> text = this.bundle.get(viewer.getLocale(), this.key);
    if (!text.isPresent()) {
      return new TextComponent("translation: '" + this.key + "'");
    }

    List<BaseComponent> extra = Lists.newArrayList(TextComponent.fromLegacyText(text.get()));
    BaseComponent component = new TextComponent("");
    component.setExtra(extra);
    return component;
  }

  @Override
  public String toLegacyText(CommandSender viewer) {
    Optional<String> text = this.bundle.get(viewer.getLocale(), this.key);
    if (!text.isPresent()) {
      return new TextComponent("translation: '" + this.key + "'").toLegacyText();
    }

    return text.get();
  }

  /** @return bundle key of the string */
  public String getKey() {
    return key;
  }
}
