package plugin.ThirdLife.listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import plugin.ThirdLife.data.Data;

public class GhoulDamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;

        FileConfiguration fileC = Data.getLivesData();
        String damagerUUID = event.getDamager().getUniqueId().toString();
        String damagedUUID = event.getEntity().getUniqueId().toString();

        if (fileC.getInt(damagedUUID) != 0 || fileC.getInt(damagerUUID) != 0) return;

        event.setCancelled(true);
        event.getDamager().sendMessage("§c(Error)§f You cannot damage another ghoul");
    }

}
