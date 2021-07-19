package plugin.ThirdLife.Listeners;

import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import plugin.ThirdLife.Data.Config;

public class OnPlayerDeath implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        System.out.println("Death event processing");
        Player player = event.getEntity();
        if(player.hasPermission("thirdlife.bypass"))
            return;
        FileConfiguration data = Config.getData();
        String uuid = player.getUniqueId().toString();
        System.out.println(data.getInt(uuid));
        switch(data.getInt(uuid)){
            case 1:
                data.set(uuid, 0);  Config.saveData(data, player);
                player.setDisplayName("§r" + player.getName());
                player.setGameMode(GameMode.SPECTATOR);
                break;
            case 2:
                data.set(uuid, 1); Config.saveData(data, player);
                player.setDisplayName("§c" + player.getName());
                OnPlayerJoin.updateColour(1, player);
                break;
            case 3:
                data.set(uuid, 2); Config.saveData(data, player);
                player.setDisplayName("§e" + player.getName());
                OnPlayerJoin.updateColour(2, player);
                break;
            default:
                data.set(uuid, 2); Config.saveData(data, player);
                player.setDisplayName("§e" + player.getName());
                OnPlayerJoin.updateColour(3, player);
                break;
        }
    }

}
