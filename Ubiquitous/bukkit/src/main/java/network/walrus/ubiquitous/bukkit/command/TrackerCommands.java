package network.walrus.ubiquitous.bukkit.command;

import app.ashcon.intake.Command;
import app.ashcon.intake.CommandException;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.parametric.annotation.Default;
import app.ashcon.intake.parametric.annotation.Switch;
import java.sql.Date;
import java.util.Collection;
import java.util.List;
import network.walrus.ubiquitous.bukkit.UbiquitousMessages;
import network.walrus.ubiquitous.bukkit.tracker.Damage;
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
import network.walrus.ubiquitous.bukkit.tracker.lifetime.LifetimeManager;
import network.walrus.ubiquitous.bukkit.tracker.trackers.base.gravity.Fall.Cause;
import network.walrus.ubiquitous.bukkit.tracker.trackers.base.gravity.Fall.From;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.command.exception.InvalidPaginationPageException;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.LocalizedTime;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.util.Paginator;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Commands for working with the damage tracker.
 *
 * @author Austin Mayes
 */
public class TrackerCommands {

  private final LifetimeManager manager;

  /** @param manager used to get damage from */
  public TrackerCommands(LifetimeManager manager) {
    this.manager = manager;
  }

  private static String pretty(EntityType type) {
    return type.name().toLowerCase().replace('_', ' ');
  }

  private static String pretty(Material type) {
    return type.name().toLowerCase().replace('_', ' ');
  }

  /**
   * View a player's damage from their current lifetime
   *
   * @param sender requesting the information
   * @param player being viewed
   * @param page of the result
   * @param location if locations should be shown
   * @param time if time should be shown
   * @throws CommandException if the page specified is out of range
   */
  @Command(
      aliases = {"damage", "recentdamage", "rd"},
      desc = "View a player's damage from their current lifetime")
  public void lifetimeInfo(
      @Sender CommandSender sender,
      Player player,
      @Default("1") int page,
      @Switch('l') boolean location,
      @Switch('t') boolean time)
      throws CommandException {
    List<Damage> damages = manager.getLifetime(player).getDamage();
    page = page - 1;
    Paginator<Damage> paginator = new Paginator<>(damages, 5);
    Collection<Damage> list;
    try {
      list = paginator.getPage(page);
    } catch (IllegalArgumentException e) {
      throw new InvalidPaginationPageException(paginator);
    }

    Localizable page1 =
        new UnlocalizedText((page + 1) + "", NetworkColorConstants.Damage.List.CURRENT_PAGE_COLOR);
    Localizable page2 =
        new UnlocalizedText(
            paginator.getPageCount() + "", NetworkColorConstants.Damage.List.TOTAL_PAGES_COLOR);

    Localizable header =
        UbiquitousMessages.DAMAGE_HEADER.with(
            NetworkColorConstants.Damage.List.HEADER_COLOR,
            new PersonalizedBukkitPlayer(player),
            page1,
            page2);
    sender.sendMessage(header);
    for (Damage damage : list) {
      sender.sendMessage(describeDamage(damage, location, time));
    }
  }

  private Localizable describeDamage(Damage damage, boolean showLocation, boolean showTime) {
    Localizable damageDesc = new UnlocalizedText("unknown damage");
    Localizable time =
        new LocalizedTime(Date.from(damage.getTime()), NetworkColorConstants.Damage.TIME);
    Localizable rawDamage =
        new LocalizedNumber(damage.getDamage(), NetworkColorConstants.Damage.DAMAGE);
    Localizable location =
        new UnlocalizedFormat("{0}, {1}, {2}")
            .with(
                NetworkColorConstants.Damage.LOCATION,
                new LocalizedNumber(damage.getLocation().getX()),
                new LocalizedNumber(damage.getLocation().getY()),
                new LocalizedNumber(damage.getLocation().getZ()));
    DamageInfo info = damage.getInfo();
    if (info.getResolvedDamager() != null) {
      if (!(info.getResolvedDamager() instanceof Player)) {
        String pretty = pretty(info.getResolvedDamager().getType());
        Localizable entity = new UnlocalizedText(pretty);
        damageDesc = UbiquitousMessages.DAMAGE_BY_MOB.with(entity);
        if (info instanceof OwnedMobDamageInfo) {
          OwnedMobDamageInfo owned = (OwnedMobDamageInfo) info;

          if (owned.getMobOwner() != null) {
            Localizable attackerName = new UnlocalizedText(owned.getMobOwner().getName());
            damageDesc = UbiquitousMessages.DAMAGE_BY_PLAYER_MOB.with(attackerName, entity);
          }
        }
      }

      Player attacker = (Player) info.getResolvedDamager();
      Localizable attackerName = new PersonalizedBukkitPlayer(attacker);

      if (info instanceof AnvilDamageInfo) {
        damageDesc = UbiquitousMessages.DAMAGE_BY_PLAYER_ANVIL.with(attackerName);
      } else if (info instanceof OwnedMobDamageInfo) {
        String pretty = pretty(info.getResolvedDamager().getType());
        Localizable entity = new UnlocalizedText(pretty);
        damageDesc = UbiquitousMessages.DAMAGE_BY_PLAYER_MOB.with(attackerName, entity);
      } else if (info instanceof ExplosiveDamageInfo) {
        damageDesc = UbiquitousMessages.DAMAGE_BY_PLAYER_TNT.with(attackerName);
      } else if (info instanceof GravityDamageInfo) {
        GravityDamageInfo gravity = (GravityDamageInfo) info;

        if (gravity.getCause() == Cause.HIT) {
          if (gravity.getFrom() == From.FLOOR) {
            damageDesc = UbiquitousMessages.DAMAGE_HIT_FLOOR_FALL.with(attackerName);
          } else if (gravity.getFrom() == From.LADDER) {
            damageDesc = UbiquitousMessages.DAMAGE_HIT_LADDER_FALL.with(attackerName);
          } else if (gravity.getFrom() == From.WATER) {
            damageDesc = UbiquitousMessages.DAMAGE_HIT_WATER_FALL.with(attackerName);
          }
        } else if (gravity.getCause() == Cause.SHOOT) {
          Localizable shotMessage = null;

          if (gravity.getFrom() == From.FLOOR) {
            shotMessage = UbiquitousMessages.DAMAGE_SHOT_FLOOR_FALL.with(attackerName);
          } else if (gravity.getFrom() == From.LADDER) {
            shotMessage = UbiquitousMessages.DAMAGE_SHOT_LADDER_FALL.with(attackerName);

          } else if (gravity.getFrom() == From.WATER) {
            shotMessage = UbiquitousMessages.DAMAGE_SHOT_WATER_FALL.with(attackerName);
          }

          damageDesc = shotMessage;
        } else if (gravity.getCause() == Cause.SPLEEF) {
          if (gravity.getFrom() == From.FLOOR) {
            damageDesc = UbiquitousMessages.DAMAGE_SPLEEF_FLOOR_FALL.with(attackerName);
          } else {
            damageDesc = UbiquitousMessages.DAMAGE_SPLEEF_BY_PLAYER.with(attackerName);
          }
        }
      } else if (info instanceof MeleeDamageInfo) {
        MeleeDamageInfo melee = (MeleeDamageInfo) info;

        if (melee.getWeapon() == Material.AIR) {
          damageDesc = UbiquitousMessages.DAMAGE_BY_MELEE_FISTS.with(attackerName);
        } else {
          String pretty = pretty(melee.getWeapon());
          Localizable material = new UnlocalizedText(pretty);
          damageDesc = UbiquitousMessages.DAMAGE_BY_MELEE.with(attackerName, material);
        }
      } else if (info instanceof ProjectileDamageInfo) {
        ProjectileDamageInfo projectileInfo = (ProjectileDamageInfo) info;

        String pretty = pretty(projectileInfo.getProjectile().getType());
        Localizable entity = new UnlocalizedText(pretty);

        double distance = damage.getLocation().distance(attacker.getLocation());
        Localizable number = new LocalizedNumber(distance, 0, 0);
        number.style().color(Games.Deaths.bowDistanceColor(distance));

        damageDesc =
            UbiquitousMessages.DAMAGE_BY_PLAYER_PROJECTILE.with(attackerName, entity, number);
      } else if (info instanceof VoidDamageInfo) {
        damageDesc = UbiquitousMessages.DAMAGE_BY_PLAYER_VOID.with(attackerName);
      }
    } else {
      if (info instanceof AnvilDamageInfo) {
        damageDesc = UbiquitousMessages.DAMAGE_BY_ANVIL.with();
      } else if (info instanceof BlockDamageInfo) {
        damageDesc = UbiquitousMessages.DAMAGE_BY_BLOCK.with();
      } else if (info instanceof ExplosiveDamageInfo) {
        damageDesc = UbiquitousMessages.DAMAGE_BY_EXPLOSIVE.with();
      } else if (info instanceof FallDamageInfo) {
        double distance = ((FallDamageInfo) info).getFallDistance();
        Localizable number = new LocalizedNumber(distance, 0, 3);

        damageDesc = UbiquitousMessages.DAMAGE_BY_FALL.with(number);
      } else if (info instanceof LavaDamageInfo) {
        damageDesc = UbiquitousMessages.DAMAGE_BY_LAVA.with();
      } else if (info instanceof VoidDamageInfo) {
        damageDesc = UbiquitousMessages.DAMAGE_BY_VOID.with();
      } else if (info instanceof ProjectileDamageInfo) {
        damageDesc = UbiquitousMessages.DAMAGE_BY_PROJECTILE.with();
      }
    }

    if (damageDesc == null) return new UnlocalizedText("bruh");

    String format = "{0} damage by {1} ";
    if (showLocation && showTime) {
      return new UnlocalizedFormat(format + "at {2} {3}")
          .with(NetworkColorConstants.Damage.DESC, rawDamage, damageDesc, location, time);
    } else if (showLocation) {
      return new UnlocalizedFormat(format + "at {2}")
          .with(NetworkColorConstants.Damage.DESC, rawDamage, damageDesc, location);
    } else if (showTime) {
      return new UnlocalizedFormat(format + "{2}")
          .with(NetworkColorConstants.Damage.DESC, rawDamage, damageDesc, time);
    }

    return new UnlocalizedFormat(format).with(rawDamage, damageDesc);
  }
}
