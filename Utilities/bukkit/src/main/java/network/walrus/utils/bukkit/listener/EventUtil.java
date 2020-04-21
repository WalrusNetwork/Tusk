package network.walrus.utils.bukkit.listener;

import com.google.common.base.Preconditions;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

/**
 * Utility which is used to fire an event with specific targets.
 *
 * @author Overcast Network
 */
public final class EventUtil {

  private EventUtil() {}

  /**
   * Fire an event to a specfic set of handlers at a specified priority.
   *
   * @param event to fire
   * @param handlers to send the event to
   * @param priority to call the event at
   */
  public static void callEvent(
      @Nonnull Event event, @Nonnull HandlerList handlers, @Nonnull EventPriority priority) {
    Preconditions.checkNotNull(event, "event");
    Preconditions.checkNotNull(handlers, "handlers");
    Preconditions.checkNotNull(priority, "priority");

    // CraftBukkit does not expose the event calling logic in a flexible
    // enough way, so we have to do a bit of copy and paste.
    //
    // The following is copied from SimplePluginManager#fireEvent with
    // modifications
    for (RegisteredListener registration : handlers.getRegisteredListeners()) {
      if (!registration.getPlugin().isEnabled()) {
        continue;
      }

      // skip over registrations that are not in the correct priority
      if (registration.getPriority() != priority) {
        continue;
      }

      try {
        registration.callEvent(event);
      } catch (AuthorNagException ex) {
        Plugin plugin = registration.getPlugin();

        if (plugin.isNaggable()) {
          plugin.setNaggable(false);

          Bukkit.getLogger()
              .log(
                  Level.SEVERE,
                  String.format(
                      "Nag author(s): '%s' of '%s' about the following: %s",
                      plugin.getDescription().getAuthors(),
                      plugin.getDescription().getFullName(),
                      ex.getMessage()));
        }
      } catch (Throwable ex) {
        Bukkit.getLogger()
            .log(
                Level.SEVERE,
                "Could not pass event "
                    + event.getEventName()
                    + " to "
                    + registration.getPlugin().getDescription().getFullName(),
                ex);
      }
    }
  }

  /**
   * Helper to call an event using the Bukkit API.
   *
   * @param event which should be fired
   * @param <T> type of event being called
   * @return the event which was called
   */
  public static <T extends Event> T call(T event) {
    Bukkit.getPluginManager().callEvent(event);
    return event;
  }
}
