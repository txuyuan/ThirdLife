package plugin.ThirdLife.listeners;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import plugin.ThirdLife.Main;
import plugin.ThirdLife.data.Data;
import plugin.ThirdLife.managers.GhoulManager;
import plugin.ThirdLife.managers.LifeUpdate;

import java.util.List;
import java.util.stream.Collectors;

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

    public static void checkBlueDeath(PlayerDeathEvent event){
        FileConfiguration fileC = Data.getLivesData();
        int lives = fileC.getInt(event.getEntity().getUniqueId().toString());

        if(lives <= 3) return;

        String msg = event.getDeathMessage();
        event.setDeathMessage(null);
        List<Player> ops = Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("thirdlife.admin")).collect(Collectors.toList());

        ops.forEach(player -> player.sendMessage("§b(Silenced)§f " + msg));
    }

}
