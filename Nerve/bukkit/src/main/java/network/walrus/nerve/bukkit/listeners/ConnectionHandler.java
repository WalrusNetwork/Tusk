package network.walrus.nerve.bukkit.listeners;

import com.apollographql.apollo.ApolloCall.Callback;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import gg.walrus.javaapiclient.UserJoinMutation;
import gg.walrus.javaapiclient.UserJoinMutation.Data;
import gg.walrus.javaapiclient.UserJoinMutation.UserJoin;
import network.walrus.nerve.bukkit.NerveBukkitPlugin;
import network.walrus.nerve.core.api.exception.ApiException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.jetbrains.annotations.NotNull;

/**
 * Class which handles player login events and makes various API calls.
 *
 * @author Rafi Baum
 */
public class ConnectionHandler implements Listener {

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onLogin(PlayerLoginEvent event) {
    if (event.getResult() != Result.ALLOWED) {
      return;
    }

    ApolloClient client = NerveBukkitPlugin.instance().getApiClient();
    if (client == null) {
      return;
    }

    client
        .mutate(
            UserJoinMutation.builder()
                .uuid(event.getPlayer().getUniqueId().toString())
                .username(event.getPlayer().getName())
                .build())
        .enqueue(
            new Callback<Data>() {
              @Override
              public void onResponse(@NotNull Response<Data> response) {
                if (response.hasErrors()) {
                  throw new ApiException(response.errors());
                }

                Bukkit.getScheduler()
                    .runTask(
                        NerveBukkitPlugin.instance(),
                        () -> syncOnResponse(event.getPlayer(), response.data().userJoin()));
              }

              @Override
              public void onFailure(@NotNull ApolloException e) {
                e.printStackTrace();
                Bukkit.getScheduler()
                    .runTask(
                        NerveBukkitPlugin.instance(),
                        () -> {
                          if (event.getPlayer().isOnline()) {
                            event.getPlayer().kickPlayer("Internal API error");
                          }
                        });
              }
            });
  }

  private void syncOnResponse(Player player, UserJoin userJoin) {
    if (!player.isOnline()) {
      return;
    }

    NerveBukkitPlugin.instance().getPermissionHandler().attachPerms(player, userJoin.permissions());
    NerveBukkitPlugin.instance()
        .getPrefixHandler()
        .attachPrefix(player, userJoin.flair(), userJoin.tag());
  }
}
