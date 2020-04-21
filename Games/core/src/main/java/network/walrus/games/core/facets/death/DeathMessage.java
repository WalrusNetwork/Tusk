package network.walrus.games.core.facets.death;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.UUID;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.ubiquitous.bukkit.settings.PlayerSettings;
import network.walrus.ubiquitous.bukkit.settings.Setting;
import network.walrus.ubiquitous.bukkit.settings.types.SettingTypes;
import network.walrus.ubiquitous.bukkit.tracker.Damage;
import network.walrus.ubiquitous.bukkit.tracker.event.entity.EntityDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.info.AnvilDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.BlockDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.ExplosiveDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.FallDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.GravityDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.LavaDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.MeleeDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.OwnedMobDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.ProjectileDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.VoidDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import network.walrus.ubiquitous.bukkit.tracker.trackers.base.gravity.Fall.Cause;
import network.walrus.ubiquitous.bukkit.tracker.trackers.base.gravity.Fall.From;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.color.NetworkColorConstants.Games.Deaths;
import network.walrus.utils.core.text.LocalizableFormat;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Comprehensive class which builds a death message using a {@link PlayerDeathEvent}.
 *
 * @author Avicus Network
 */
public class DeathMessage {

  public static final Setting<DeathMessageSetting> SETTING =
      new Setting<DeathMessageSetting>(
          "games.core.death-messages",
          SettingTypes.enumOf(DeathMessageSetting.class),
          DeathMessageSetting.ALL,
          GamesCoreMessages.SETTING_DEATHMESSAGE_NAME.with(),
          GamesCoreMessages.SETTING_DEATHMESSAGE_DESCRIPTION.with());
  private static final Timing generationTimer =
      Timings.of(GamesPlugin.instance, "Death message generation");
  private static final Timing broadcastTimer =
      Timings.of(GamesPlugin.instance, "Death message broadcast");
  private static GroupsManager groupsManager;
  private static FacetHolder holder;

  /**
   * Creates a localized death message.
   *
   * @param event The death event.
   * @param playersInvolved An empty list, modified through this method to include all players
   *     involved in this death message.
   */
  private static Localizable create(
      FacetHolder holder, EntityDeathEvent event, Set<UUID> playersInvolved) {
    if (DeathMessage.holder == null || !DeathMessage.holder.equals(holder)) {
      DeathMessage.holder = holder;
      groupsManager = holder.getFacetRequired(GroupsManager.class);
    }

    if (!(event instanceof PlayerDeathEvent || event instanceof TaggedPlayerDeathEvent)) {
      throw new IllegalArgumentException("Event must be the death of a player or tagged player");
    }

    if (event instanceof PlayerDeathEvent) {
      playersInvolved.add(((PlayerDeathEvent) event).getPlayer().getUniqueId());
    }

    Lifetime lifetime = event.getLifetime();
    Damage last = lifetime.getLastDamage();
    Location loc = event.getLocation();

    Localizable name;

    if (event instanceof PlayerDeathEvent) {
      Player player = ((PlayerDeathEvent) event).getPlayer();
      Competitor competitor = groupsManager.getCompetitorOf(player).orElse(null);
      if (competitor == null) {
        name = new PersonalizedBukkitPlayer(player);
      } else {
        name = new PersonalizedBukkitPlayer(player, competitor.getColor().style());
      }
    } else {
      name = new PersonalizedBukkitPlayer(((TaggedPlayerDeathEvent) event).getPlayer().getPlayer());
    }

    if (last == null) {
      return GamesCoreMessages.DEATH_DIED.with(Games.Deaths.MESSAGE, name);
    }

    DamageInfo info = last.getInfo();

    if (info.getResolvedDamager() != null) {
      if (!(info.getResolvedDamager() instanceof Player)) {
        String pretty = pretty(info.getResolvedDamager().getType());
        Localizable entity = new UnlocalizedText(pretty);

        LocalizedFormat message;
        switch (info.getDamageCause()) {
          case PROJECTILE:
            message = GamesCoreMessages.DEATH_BY_MOB_PROJECTILE;
            break;
          case ENTITY_EXPLOSION:
            message = GamesCoreMessages.DEATH_BY_MOB_EXPLODE;
            break;
          default:
            message = GamesCoreMessages.DEATH_BY_MOB;
            break;
        }
        return message.with(Games.Deaths.MESSAGE, name, entity);
      }

      Player attacker = (Player) info.getResolvedDamager();
      playersInvolved.add(attacker.getUniqueId());
      Competitor attackerCompetitor = groupsManager.getCompetitorOf(attacker).orElse(null);
      Localizable attackerName;
      if (attackerCompetitor == null) {
        attackerName = new PersonalizedBukkitPlayer(attacker);
      } else {
        attackerName =
            new PersonalizedBukkitPlayer(attacker, attackerCompetitor.getColor().style());
      }

      if (info instanceof AnvilDamageInfo) {
        return GamesCoreMessages.DEATH_BY_PLAYER_ANVIL.with(
            Games.Deaths.MESSAGE, name, attackerName);
      } else if (info instanceof OwnedMobDamageInfo) {
        String pretty = pretty(((OwnedMobDamageInfo) info).getMob().getType());
        Localizable entity = new UnlocalizedText(pretty);
        return GamesCoreMessages.DEATH_BY_PLAYER_MOB.with(
            Deaths.MESSAGE, name, attackerName, entity);

      } else if (info instanceof ExplosiveDamageInfo) {
        return GamesCoreMessages.DEATH_BY_PLAYER_TNT.with(Games.Deaths.MESSAGE, name, attackerName);
      } else if (info instanceof GravityDamageInfo) {
        GravityDamageInfo gravity = (GravityDamageInfo) info;

        boolean isVoid = loc.getY() < 0;

        if (gravity.getCause() == Cause.HIT) {
          if (gravity.getFrom() == From.FLOOR) {
            if (isVoid) {
              return GamesCoreMessages.DEATH_HIT_FLOOR_VOID.with(
                  Games.Deaths.MESSAGE, name, attackerName);
            } else {
              return GamesCoreMessages.DEATH_HIT_FLOOR_FALL.with(
                  Games.Deaths.MESSAGE, name, attackerName);
            }
          } else if (gravity.getFrom() == From.LADDER) {
            if (isVoid) {
              return GamesCoreMessages.DEATH_HIT_LADDER_VOID.with(
                  Games.Deaths.MESSAGE, name, attackerName);
            } else {
              return GamesCoreMessages.DEATH_HIT_LADDER_FALL.with(
                  Games.Deaths.MESSAGE, name, attackerName);
            }
          } else if (gravity.getFrom() == From.WATER) {
            if (isVoid) {
              return GamesCoreMessages.DEATH_HIT_WATER_VOID.with(
                  Games.Deaths.MESSAGE, name, attackerName);
            } else {
              return GamesCoreMessages.DEATH_HIT_WATER_FALL.with(
                  Games.Deaths.MESSAGE, name, attackerName);
            }
          }
        } else if (gravity.getCause() == Cause.SHOOT) {
          Double distance = null;

          Damage damage = lifetime.getLastDamage(ProjectileDamageInfo.class);
          if (damage != null) {
            distance = ((ProjectileDamageInfo) damage.getInfo()).getDistance();
          }

          Localizable shotMessage = null;

          if (gravity.getFrom() == From.FLOOR) {
            if (isVoid) {
              shotMessage =
                  GamesCoreMessages.DEATH_SHOT_FLOOR_VOID.with(
                      Games.Deaths.MESSAGE, name, attackerName);
            } else {
              shotMessage =
                  GamesCoreMessages.DEATH_SHOT_FLOOR_FALL.with(
                      Games.Deaths.MESSAGE, name, attackerName);
            }
          } else if (gravity.getFrom() == From.LADDER) {
            if (isVoid) {
              shotMessage =
                  GamesCoreMessages.DEATH_SHOT_LADDER_VOID.with(
                      Games.Deaths.MESSAGE, name, attackerName);
            } else {
              shotMessage =
                  GamesCoreMessages.DEATH_SHOT_LADDER_FALL.with(
                      Games.Deaths.MESSAGE, name, attackerName);
            }
          } else if (gravity.getFrom() == From.WATER) {
            if (isVoid) {
              shotMessage =
                  GamesCoreMessages.DEATH_SHOT_WATER_VOID.with(
                      Games.Deaths.MESSAGE, name, attackerName);
            } else {
              shotMessage =
                  GamesCoreMessages.DEATH_SHOT_WATER_FALL.with(
                      Games.Deaths.MESSAGE, name, attackerName);
            }
          }

          if (distance == null) {
            return shotMessage;
          }

          LocalizableFormat format = new UnlocalizedFormat("{0} {1}");
          Localizable number = new LocalizedNumber(distance, 0, 0);
          number.style().color(Games.Deaths.bowDistanceColor(distance));

          return format.with(
              Games.Deaths.MESSAGE, shotMessage, GamesCoreMessages.DEATH_BLOCKS.with(number));
        } else if (gravity.getCause() == Cause.SPLEEF) {
          if (gravity.getFrom() == From.FLOOR) {
            if (isVoid) {
              return GamesCoreMessages.DEATH_SPLEEF_FLOOR_VOID.with(
                  Games.Deaths.MESSAGE, name, attackerName);
            } else {
              return GamesCoreMessages.DEATH_SPLEEF_FLOOR_FALL.with(
                  Games.Deaths.MESSAGE, name, attackerName);
            }
          } else {
            return GamesCoreMessages.DEATH_SPLEEF_BY_PLAYER.with(
                Games.Deaths.MESSAGE, name, attackerName);
          }
        }
      } else if (info instanceof MeleeDamageInfo) {
        MeleeDamageInfo melee = (MeleeDamageInfo) info;

        if (melee.getWeapon() == Material.AIR) {
          return GamesCoreMessages.DEATH_BY_MELEE_FISTS.with(
              Games.Deaths.MESSAGE, name, attackerName);
        } else {
          String pretty = pretty(melee.getWeapon());
          Localizable material = new UnlocalizedText(pretty);
          return GamesCoreMessages.DEATH_BY_MELEE.with(
              Games.Deaths.MESSAGE, name, attackerName, material);
        }
      } else if (info instanceof ProjectileDamageInfo) {
        ProjectileDamageInfo projectileInfo = (ProjectileDamageInfo) info;

        String pretty = pretty(projectileInfo.getProjectile().getType());
        Localizable entity = new UnlocalizedText(pretty);

        double distance = last.getLocation().distance(attacker.getLocation());
        Localizable number = new LocalizedNumber(distance, 0, 0);
        number.style().color(Games.Deaths.bowDistanceColor(distance));

        return GamesCoreMessages.DEATH_BY_PLAYER_PROJECTILE.with(
            Games.Deaths.MESSAGE, name, attackerName, entity, number);
      } else if (info instanceof VoidDamageInfo) {
        return GamesCoreMessages.DEATH_BY_PLAYER_VOID.with(
            Games.Deaths.MESSAGE, name, attackerName);
      }
    } else {
      if (info instanceof AnvilDamageInfo) {
        return GamesCoreMessages.DEATH_BY_ANVIL.with(Games.Deaths.MESSAGE, name);
      } else if (info instanceof BlockDamageInfo) {
        return GamesCoreMessages.DEATH_BY_BLOCK.with(Games.Deaths.MESSAGE, name);
      } else if (info instanceof ExplosiveDamageInfo) {
        return GamesCoreMessages.DEATH_BY_EXPLOSIVE.with(Games.Deaths.MESSAGE, name);
      } else if (info instanceof FallDamageInfo) {
        double distance = ((FallDamageInfo) info).getFallDistance();
        Localizable number = new LocalizedNumber(distance, 0, 0);

        return GamesCoreMessages.DEATH_BY_FALL.with(Games.Deaths.MESSAGE, name, number);
      } else if (info instanceof LavaDamageInfo) {
        return GamesCoreMessages.DEATH_BY_LAVA.with(Games.Deaths.MESSAGE, name);
      } else if (info instanceof VoidDamageInfo) {
        return GamesCoreMessages.DEATH_BY_VOID.with(Games.Deaths.MESSAGE, name);
      } else if (info instanceof ProjectileDamageInfo) {
        return GamesCoreMessages.DEATH_BY_PROJECTILE.with(Games.Deaths.MESSAGE, name);
      }
    }

    return GamesCoreMessages.DEATH_DIED.with(Games.Deaths.MESSAGE, name);
  }

  private static String pretty(EntityType type) {
    return type.name().toLowerCase().replace('_', ' ');
  }

  private static String pretty(Material type) {
    return type.name().toLowerCase().replace('_', ' ');
  }

  /**
   * Broadcast a death message based on a {@link PlayerDeathEvent} to a specific {@link
   * FacetHolder}.
   *
   * @param holder which the message should be sent to
   * @param event which represents the death
   */
  public static void broadcast(FacetHolder holder, EntityDeathEvent event) {
    final Set<UUID> involved = Sets.newHashSet();
    Localizable genericTranslation;
    try (Timing timing = generationTimer.startClosable()) {
      genericTranslation = create(holder, event, involved);
    }
    try (Timing timing = broadcastTimer.startClosable()) {
      for (Player player : holder.players()) {
        final boolean wasInvolved = involved.contains(player.getUniqueId());
        switch (PlayerSettings.get(player, SETTING)) {
          case NONE:
            continue;
          case OWN:
            if (!wasInvolved) {
              continue;
            }
            // fall-through
          case ALL:
            Localizable translation = genericTranslation;
            if (wasInvolved) {
              translation = translation.duplicate();
              translation.style().bold(true);
            }

            player.sendMessage(translation);
        }
      }

      Bukkit.getConsoleSender().sendMessage(genericTranslation);
    }
  }

  public enum DeathMessageSetting {
    ALL,
    OWN,
    NONE;
  }
}
