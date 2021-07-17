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
                System.out.println("case 1");
                break;
            case 2:
                data.set(uuid, 1); Config.saveData(data, player);
                player.setDisplayName("§c" + player.getName());
                System.out.println("case 2");
                break;
            case 3:
                data.set(uuid, 2); Config.saveData(data, player);
                player.setDisplayName("§e" + player.getName());
                System.out.println("case 3");
                break;
            default:
                data.set(uuid, 2); Config.saveData(data, player);
                player.setDisplayName("§e" + player.getName());
                break;
        }
    }

}
