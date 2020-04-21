package network.walrus.utils.bukkit.visual;

import java.util.Objects;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import network.walrus.utils.core.color.NetworkColorConstants.Branding;
import org.apache.commons.lang.StringUtils;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * A utility class that allows the manipulation of a single {@link Scoreboard}.
 *
 * <p>This was designed to only interact with a single scoreboard to allow implementations to be
 * extremely versatile. An example of this would be to send different lines to each player (for
 * localization).
 *
 * <p>This class only updates the prefix/suffix of teams, so it will never flash. Because of this,
 * the character limit of scoreboard line is 32 characters (including formatting).
 *
 * <p>A scoreboard can have a max of 14 lines. This is so that there will always be room for the
 * URL.
 *
 * <p>The data arrays used throughout this class intentionally start at 1 instead of 0 to align with
 * the actual row on the scoreboard.
 *
 * <p>Use the {@link #addIP()} method to add the IP to the last line of the sidebar.
 *
 * @author Austin Mayes
 */
public class Sidebar {

  /** Array of fake players attacked to each team. Without players, a team will not show. */
  protected final String[] players = new String[Constants.MAX_ROWS + 3];
  /** The rows on the scoreboard. */
  private final String[] rows = new String[Constants.MAX_ROWS + 3];
  /** The score of each line. */
  private final int[] scores = new int[Constants.MAX_ROWS + 3];
  /** Teams for each line. */
  private final Team[] teams = new Team[Constants.MAX_ROWS + 3];
  /** The scoreboard that is being manipulated. */
  private final Scoreboard scoreboard;
  /** The single objective on the scoreboard. */
  private Objective objective;

  /**
   * Constructor.
   *
   * @param scoreboard that is being manipulated
   */
  public Sidebar(Scoreboard scoreboard) {
    this(scoreboard, UUID.randomUUID().toString().substring(0, 5));
  }

  /**
   * Constructor.
   *
   * @param scoreboard that is being manipulated
   * @param title at the top of the scoreboard
   */
  public Sidebar(Scoreboard scoreboard, String title) {
    this.scoreboard = scoreboard;
    this.objective = null;
    this.objective = this.scoreboard.getObjective(Constants.IDENTIFIER);
    if (this.objective == null) {
      this.objective = this.scoreboard.registerNewObjective(Constants.IDENTIFIER, "dummy");
    }
    this.objective.setDisplayName(title);
    this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

    for (int i = 1; i <= Constants.MAX_ROWS + 2; ++i) {
      this.rows[i] = null;
      this.scores[i] = -1;

      this.players[i] = String.valueOf(ChatColor.COLOR_CHAR) + (char) i;
      this.teams[i] = this.scoreboard.registerNewTeam(Constants.IDENTIFIER + "-row-" + i);
      this.teams[i].setPrefix("");
      this.teams[i].setSuffix("");
      this.teams[i].addEntry(this.players[i]);
    }
  }

  /** Unregister all teams */
  public void destroy() {
    for (Team team : this.teams) {
      try {
        if (team != null) team.unregister();
      } catch (IllegalStateException e) {
        // Ignored
      }
    }
  }

  /** Add a default footer of the server IP. */
  public void addIP() {
    // TODO
    addFooter(Branding.IP.apply("walrus.gg").toLegacyText());
  }

  /**
   * Add a footer to the last line of scoreboard, and a space above it.
   *
   * @param footer the colored text string to add
   */
  public void addFooter(String footer) {
    this.rows[15] = "";
    this.scores[15] = 2;

    this.players[15] = String.valueOf(ChatColor.COLOR_CHAR) + (char) 15;

    this.teams[15].setPrefix("");
    this.teams[15].setSuffix("");
    this.objective.getScore(this.players[15]).setScore(2);
    this.teams[15].addEntry(this.players[15]);

    this.rows[16] = footer;
    this.scores[16] = 1;

    this.players[16] = String.valueOf(ChatColor.COLOR_CHAR) + (char) 16;

    int split = Constants.MAX_PREFIX - 1;
    String prefix = StringUtils.substring(footer, 0, split);
    String lastColors = org.bukkit.ChatColor.getLastColors(prefix);
    String suffix =
        lastColors
            + StringUtils.substring(
                footer, split, split + Constants.MAX_SUFFIX - lastColors.length());

    this.teams[16].setPrefix(prefix);
    this.teams[16].setSuffix(suffix);
    this.objective.getScore(this.players[16]).setScore(1);
    this.teams[16].addEntry(this.players[16]);
  }

  /** Set the title of the scoreboard. */
  public void setTitle(String title) {
    this.objective.setDisplayName(title);
  }

  /**
   * Set a row's text.
   *
   * @param maxScore the maximum number of rows currently being displayed not including the URL
   * @param row the row number
   * @param text the text being displayed, null for the line to be removed
   */
  public void setRow(int maxScore, int row, String text) {
    if (row <= 0 || row > Constants.MAX_ROWS) {
      return;
    }

    int score = text == null ? -1 : maxScore - row + 2;
    if (this.scores[row] != score) {
      this.scores[row] = score;

      if (text == null) {
        this.scoreboard.resetScores(this.players[row]);
        return;
      }

      if (score == -1) {
        this.scoreboard.resetScores(this.players[row]);
      } else {
        this.objective.getScore(this.players[row]).setScore(score);
      }
    }

    if (!Objects.equals(this.rows[row], text)) {
      this.rows[row] = text;

      if (text != null) {
        /*
        Split the row text into prefix and suffix, limited to 16 chars each. Because the player name
             is a color code, we have to restore the color at the split in the suffix. We also have to be
             careful not to split in the middle of a color code.
            */
        int split =
            Constants.MAX_PREFIX - 1; // Start by assuming there is a color code right on the split
        if (text.length() < Constants.MAX_PREFIX || text.charAt(split) != ChatColor.COLOR_CHAR) {
          // If there isn't, we can fit one more char in the prefix
          split++;
        }
        // Split and truncate the text, and restore the color in the suffix
        String prefix = StringUtils.substring(text, 0, split);
        String lastColors = org.bukkit.ChatColor.getLastColors(prefix);
        String suffix =
            lastColors
                + StringUtils.substring(
                    text, split, split + Constants.MAX_SUFFIX - lastColors.length());

        this.teams[row].setPrefix(prefix);
        this.teams[row].setSuffix(suffix);
      }
    }
  }

  /**
   * Gets the Bukkit scoreboard associated with this sidebar.
   *
   * @return the scoreboard.
   */
  public Scoreboard getBukkitScoreboard() {
    return scoreboard;
  }

  /** Helpful constants for implementations. */
  public class Constants {

    /** Max rows that implementations are allowed to scoreboard. */
    public static final int MAX_ROWS = 14;
    /** Max prefix length. */
    public static final int MAX_PREFIX = 16;
    /** Max suffix length. */
    public static final int MAX_SUFFIX = 16;
    /** Prefix used for all teams created by this class. */
    public static final String IDENTIFIER = "sidebar";
  }
}
