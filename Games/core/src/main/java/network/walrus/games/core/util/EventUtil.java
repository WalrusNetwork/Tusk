package network.walrus.games.core.util;

import network.walrus.games.core.GamesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventCallback;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Wrapper around the {@link org.bukkit.plugin.PluginManager} event system to make changing out
 * event APIs easier down the road.
 *
 * @author Austin Mayes
 */
public class EventUtil {

  /**
   * Register a listener with the bukkit event system.
   *
   * @param listener to register
   * @param <T> type of listener being registered
   * @return the registered listener
   */
  public static <T extends Listener> T register(T listener) {
    Bukkit.getPluginManager().registerEvents(listener, GamesPlugin.instance);
    return listener;
  }

  /**
   * Register a collection of listeners using {@link #register(Listener)}.
   *
   * @param listeners to register
   */
  public static void register(Iterable<Listener> listeners) {
    for (Listener listener : listeners) {
      register(listener);
    }
  }

  /**
   * Unregister a listener from the bukkit event system.
   *
   * @param listener to unregister
   * @param <T> type of listener
   * @return the unregistered listener
   */
  public static <T extends Listener> T unregister(T listener) {
    HandlerList.unregisterAll(listener);
    return listener;
  }

  /**
   * Unregister a collection of listeners using {@link #unregister(Listener)}.
   *
   * @param listeners to unregister
   */
  public static void unregister(Iterable<? extends Listener> listeners) {
    for (Listener listener : listeners) {
      unregister(listener);
    }
  }

  /**
   * Call an event with an empty {@link EventCallback}. Callers should note that the event is still
   * being fired, and needs to be {@link Event#yield()}ed if that behaviour is needed.
   *
   * @param event to call
   * @param <T> type of event being called
   * @return the called event
   */
  public static <T extends Event> T call(T event) {
    return call(event, (e) -> {});
  }

  /**
   * Call an event with the supplied callback. Callers should note that the event is still being
   * fired, and needs to be {@link Event#yield()}ed if that behaviour is needed.
   *
   * @param event to call
   * @param callback to be called after the event is finished firing
   * @param <T> type of event being called
   * @return the called event
   */
  public static <T extends Event> T call(T event, EventCallback callback) {
    Bukkit.getPluginManager().callEvent(event, callback);
    return event;
  }
}
