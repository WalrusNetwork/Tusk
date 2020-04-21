package network.walrus.nerve.bukkit.user;

import com.google.common.collect.Maps;
import java.util.Map;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import network.walrus.utils.core.player.CommandSenderRelationInfo;
import network.walrus.utils.core.player.PrefixGenerator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Class which ensures player prefixes are properly rendered.
 *
 * @author Rafi Baum
 */
public class BukkitPrefixHandler implements PrefixGenerator, Listener {

  private static final BaseComponent DEFAULT = new TextComponent("");

  private final Map<Player, Prefix> prefixes;

  /** Constructor. */
  public BukkitPrefixHandler() {
    prefixes = Maps.newHashMap();
  }

  /**
   * Attaches a prefix to a player.
   *
   * @param player
   * @param flair
   * @param tag
   */
  public void attachPrefix(Player player, String flair, String tag) {
    prefixes.put(
        player,
        new Prefix(
            flatten(TextComponent.fromLegacyText(flair)),
            flatten(TextComponent.fromLegacyText(tag))));
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    prefixes.remove(event.getPlayer());
  }

  @Override
  public BaseComponent renderLong(CommandSenderRelationInfo info) {
    Prefix prefix = getPrefix(info);

    if (prefix != null) {
      return prefix.tag;
    } else {
      return DEFAULT;
    }
  }

  @Override
  public BaseComponent renderCondensed(CommandSenderRelationInfo info) {
    Prefix prefix = getPrefix(info);

    if (prefix != null) {
      return prefix.flair;
    } else {
      return DEFAULT;
    }
  }

  private Prefix getPrefix(CommandSenderRelationInfo info) {
    if (!(info.target() instanceof Player)) {
      return null;
    }

    if (!info.trueIdentityPrimary() && !info.canSeeThroughFakeIdentity()) {
      return null;
    }

    return prefixes.get(info.target());
  }

  private BaseComponent flatten(BaseComponent... components) {
    if (components.length == 0) {
      return null;
    }

    BaseComponent base = components[0];
    for (int i = 1; i < components.length; i++) {
      base.addExtra(components[i]);
    }

    return base;
  }
}

class Prefix {
  final BaseComponent flair;
  final BaseComponent tag;

  Prefix(BaseComponent flair, BaseComponent tag) {
    this.flair = flair;
    this.tag = tag;
  }
}
