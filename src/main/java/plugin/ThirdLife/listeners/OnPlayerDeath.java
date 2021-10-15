package plugin.ThirdLife.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import plugin.ThirdLife.managers.LifeUpdate;

public class OnPlayerDeath implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        LifeUpdate.removeOne(player);
    }

}
