package plugin.ThirdLife.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import plugin.ThirdLife.managers.LifeUpdate;

public class OnPlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        LifeUpdate.loadPlayer(event.getPlayer());
    }


}
