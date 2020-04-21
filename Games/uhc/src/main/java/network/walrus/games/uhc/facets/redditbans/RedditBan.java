package network.walrus.games.uhc.facets.redditbans;

import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Representation of a single entry within the UBL.
 *
 * @author Rafi Baum
 */
public class RedditBan {

  private final UUID uuid;
  private final String reason;
  @Nullable private final Date dateBanned;
  @Nullable private final Date dateExpires;
  private final String caseLink;

  /**
   * Constructor
   *
   * @param uuid of user
   * @param reason for ban
   * @param dateExpires or null for never
   * @param caseLink of uhc courtroom
   */
  RedditBan(
      UUID uuid,
      String reason,
      @Nullable Date dateBanned,
      @Nullable Date dateExpires,
      String caseLink) {
    this.uuid = uuid;
    this.reason = reason;
    this.dateBanned = dateBanned;
    this.dateExpires = dateExpires;
    this.caseLink = caseLink;
  }

  UUID getUuid() {
    return uuid;
  }

  String getReason() {
    return reason;
  }

  Date getDateBanned() {
    return dateBanned;
  }

  Date getDateExpires() {
    return dateExpires;
  }

  String getCaseLink() {
    return caseLink;
  }
}
