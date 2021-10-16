package plugin.ThirdLife.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import plugin.ThirdLife.Main;
import plugin.ThirdLife.managers.GhoulManager;
import plugin.ThirdLife.managers.LifeUpdate;

public class OnPlayerDeath implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        new BukkitRunnable() {
            public void run() {
                LifeUpdate.addLife(event.getEntity(), false);
                GhoulManager.checkGhoulKill(event);
            }
        }.runTaskLater(Main.getInstance(), 1);
    }

}
