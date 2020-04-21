package network.walrus.duels;

/**
 * Listener which assigns players to, and passes events to {@link Duel}s.
 *
 * @author Austin Mayes
 */
public class DuelListener {

  private final DuelsManager manager;

  /** @param manager used to get duel information from */
  public DuelListener(DuelsManager manager) {
    this.manager = manager;
  }
}
