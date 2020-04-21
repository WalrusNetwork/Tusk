package network.walrus.games.uhc.facets.xray;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeByPlayerEvent;
import network.walrus.utils.bukkit.logging.TranslatableLogRecord;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.UHC.Moderation;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Hosts.Xray;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.TextStyle;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Facet which sends hosts notifications when players mine too much of a certain material within a
 * certain amount of time.
 *
 * @author Austin Mayes
 */
public class XRayNotificationFacet extends Facet implements Listener {

  private static final NotifiableMaterial[] MATERIALS =
      new NotifiableMaterial[] {
        NotifiableMaterial.of(
            Material.DIAMOND_ORE,
            Duration.ofMinutes(5),
            8,
            ChatColor.AQUA + "diamonds",
            "5 minutes"),
        NotifiableMaterial.of(
            Material.MOB_SPAWNER,
            Duration.ofMinutes(10),
            4,
            ChatColor.RED + "mob spawners",
            "10 minutes"),
        NotifiableMaterial.of(
            Material.GOLD_ORE, Duration.ofMinutes(15), 25, ChatColor.GOLD + "gold", "15 minutes"),
      };
  private final Table<UUID, Material, Long> lastMineTimes = HashBasedTable.create();
  private final Table<UUID, Material, AtomicInteger> mineTotals = HashBasedTable.create();
  private final FacetHolder holder;

  /** @param holder which this facet is operating in */
  public XRayNotificationFacet(FacetHolder holder) {
    this.holder = holder;
  }

  /** Check for patterns and send alerts */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onBreak(BlockChangeByPlayerEvent event) {
    if (event.isToAir()) {
      Instant now = Instant.now();
      Material brokenMat = event.getOldState().getType();
      for (NotifiableMaterial material : MATERIALS) {
        if (material.find == event.getBlock().getType()) {
          UUID uuid = event.getPlayer().getUniqueId();
          if (!mineTotals.contains(uuid, brokenMat)
              || (lastMineTimes.contains(uuid, brokenMat)
                  && Instant.ofEpochMilli(lastMineTimes.get(uuid, brokenMat))
                      .plus(material.coolDown)
                      .isAfter(now))) {
            mineTotals.put(uuid, brokenMat, new AtomicInteger());
            lastMineTimes.put(uuid, brokenMat, now.toEpochMilli());
            return;
          }
          AtomicInteger current = mineTotals.get(uuid, brokenMat);
          current.incrementAndGet();
          if (current.get() >= material.threshHold && current.get() % 10 == 0) {
            UHCManager.instance
                .hostLogger()
                .log(
                    new TranslatableLogRecord(
                        Level.WARNING,
                        UHCMessages.XRAY_NOTIFICATION.with(
                            Xray.NOTIFICATION,
                            new PersonalizedBukkitPlayer(event.getPlayer()),
                            new LocalizedNumber(
                                current.get(),
                                TextStyle.ofColor(
                                    Xray.determineSeverity(current.get(), material.threshHold))),
                            new UnlocalizedText(material.humanMaterial),
                            new UnlocalizedText(material.humanCoolDown)),
                        Moderation.Xray.ALERT));
          }

          break;
        }
      }
    }
  }

  private static final class NotifiableMaterial {

    final Material find;
    final Duration coolDown;
    final Integer threshHold;
    final String humanMaterial;
    final String humanCoolDown;

    NotifiableMaterial(
        Material find,
        Duration coolDown,
        Integer threshHold,
        String humanMaterial,
        String humanCoolDown) {
      this.find = find;
      this.coolDown = coolDown;
      this.threshHold = threshHold;
      this.humanMaterial = humanMaterial;
      this.humanCoolDown = humanCoolDown;
    }

    static NotifiableMaterial of(
        Material material,
        Duration coolDown,
        Integer threshHold,
        String humanMaterial,
        String humanCoolDown) {
      return new NotifiableMaterial(material, coolDown, threshHold, humanMaterial, humanCoolDown);
    }
  }
}
