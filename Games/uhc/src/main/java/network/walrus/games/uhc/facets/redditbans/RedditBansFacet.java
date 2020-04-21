package network.walrus.games.uhc.facets.redditbans;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.UHCPermissions;
import network.walrus.games.uhc.config.UHCConfig;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.RedditBans;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Facet which enforces /r/UHC's Unified Ban List. Pulls the list on server startup and kicks
 * players who have active bans on the list.
 *
 * @author Rafi Baum
 */
public class RedditBansFacet extends Facet implements Listener {

  private static final GenericUrl bansUrl =
      new GenericUrl(
          "https://docs.google.com/spreadsheet/ccc?key=0AjACyg1Jc3_GdEhqWU5PTEVHZDVLYWphd2JfaEZXd2c&output=csv");
  private static final HttpRequestFactory factory = new NetHttpTransport().createRequestFactory();
  private static final DateFormat banDates = new SimpleDateFormat("\"d MMM, yyyy\"");

  private final FacetHolder holder;
  private final UHCConfig config;
  private Multimap<UUID, RedditBan> bans;
  private Set<UUID> exemptPlayers;

  public RedditBansFacet(FacetHolder holder) {
    this.holder = holder;
    this.config = UHCManager.instance.getConfig();
    this.bans = ArrayListMultimap.create();
    this.exemptPlayers = Sets.newHashSet();
  }

  @Override
  public void load() {
    updateBanList();
  }

  /**
   * @param uuid of the user to be looked up
   * @return all applicable ban entries (if any) for that user
   */
  Collection<RedditBan> getBans(UUID uuid) {
    return bans.get(uuid);
  }

  /**
   * @param uuid of the user to check
   * @return if the user has an active ban on the UBL
   */
  boolean isBanned(UUID uuid) {
    if (!config.redditBanListEnabled.get()) {
      return false;
    }

    if (exemptPlayers.contains(uuid)) {
      return false;
    }

    if (Bukkit.getPlayer(uuid) != null
        && Bukkit.getPlayer(uuid).hasPermission(UHCPermissions.UBL_EXEMPT)) {
      return false;
    }

    Collection<RedditBan> bans = getBans(uuid);
    if (!bans.isEmpty()) {
      Date currDate = new Date();

      boolean anyPermanent = false;
      for (RedditBan b : bans) {
        if (b.getDateExpires() == null) {
          anyPermanent = true;
          break;
        }
      }
      boolean anyOngoing = false;
      for (RedditBan b : bans) {
        if (b.getDateExpires() != null) {
          if (b.getDateExpires().after(currDate)) {
            anyOngoing = true;
            break;
          }
        }
      }

      return anyPermanent || anyOngoing;
    }

    return false;
  }

  void addExemptedPlayer(UUID uuid) {
    exemptPlayers.add(uuid);
  }

  void removeExemptedPlayer(UUID uuid) {
    exemptPlayers.remove(uuid);
  }

  boolean isExempt(UUID uuid) {
    return exemptPlayers.contains(uuid);
  }

  /** Launches an async task to update the ban list with the latest version of the spreadsheet. */
  void updateBanList() {
    GameTask updateTask =
        GameTask.of(
                "UBL parse",
                () -> {
                  try {
                    HttpRequest request = factory.buildRequest("GET", bansUrl, null);

                    String response = request.execute().parseAsString();
                    Multimap<UUID, RedditBan> newBans = ArrayListMultimap.create();

                    String[] lines = response.split("\n");
                    int badDates = 0;
                    for (int i = 1; i < lines.length; i++) {
                      // Split on the comma if there's even or zero " ahead
                      String[] parts = lines[i].split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                      for (int j = 0; j < parts.length; j++) {
                        parts[j] = parts[j].trim();
                      }

                      UUID uuid;
                      try {
                        uuid = UUID.fromString(parts[1]);
                      } catch (IllegalArgumentException e) {
                        UHCManager.instance
                            .hostLogger()
                            .warning("Cannot parse UUID " + parts[1] + " from UBL");
                        continue;
                      }

                      String reason = parts[2];

                      Date dateBanned = null;
                      Date dateExpires = null;

                      try {
                        dateBanned = banDates.parse(parts[3]);
                      } catch (ParseException e) {
                        // Usually not an issue
                      }

                      try {
                        if (!parts[5].equalsIgnoreCase("Never")) {
                          dateExpires = banDates.parse(parts[5]);
                        }
                      } catch (ParseException e) {

                        if (dateBanned != null) {
                          // Try alternative approach
                          if (!parts[4].equalsIgnoreCase("Permanent")) {
                            // Get part of duration before space as length in months
                            int monthDuration = Integer.parseInt(parts[4].split(" ")[0]);
                            Calendar calExpiry = Calendar.getInstance();
                            calExpiry.setTime(dateBanned);
                            calExpiry.add(Calendar.MONTH, monthDuration);
                            dateExpires = calExpiry.getTime();
                          }
                        } else {
                          UHCManager.instance
                              .hostLogger()
                              .warning(
                                  "User "
                                      + uuid.toString()
                                      + " may be banned wrongfully due to malformed data. Please refer to the UBL spreadsheet.");
                        }
                      }

                      String caseLink = parts[6];
                      newBans.put(
                          uuid, new RedditBan(uuid, reason, dateBanned, dateExpires, caseLink));
                    }

                    GameTask.of(
                            "UBL ban",
                            () -> {
                              bans = newBans;
                              Bukkit.getLogger().info("Parsed " + bans.size() + " bans from UBL");
                              kickBannedPlayers();
                            })
                        .now();

                  } catch (IOException e) {
                    Bukkit.getLogger().warning("UBL Ban parsing failed!");
                    e.printStackTrace();
                  }
                })
            .nowAsync();
  }

  /** Kicks any players currently logged in that are banned on the UBL */
  public void kickBannedPlayers() {
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (isBanned(p.getUniqueId())) {
        p.kickPlayer(UHCMessages.REDDIT_BANNED.with(RedditBans.BANNED).render(p).toPlainText());
      }
    }
  }

  @EventHandler
  public void onLogin(PlayerLoginEvent event) {
    if (isBanned(event.getPlayer().getUniqueId())) {
      event.disallow(
          PlayerLoginEvent.Result.KICK_BANNED,
          UHCMessages.REDDIT_BANNED
              .with(RedditBans.BANNED)
              .render(event.getPlayer())
              .toPlainText());
    }
  }
}
