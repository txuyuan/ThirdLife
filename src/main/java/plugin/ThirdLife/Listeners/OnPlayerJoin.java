package plugin.ThirdLife.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import plugin.ThirdLife.Data.Config;

public class OnPlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        FileConfiguration data = Config.getData();
        Player player = event.getPlayer();
        if(player.hasPermission("thirdlife.bypass")) {
            player.setDisplayName(player.getName());
            return;
        }
        String uuid = player.getUniqueId().toString();
        if(!data.getKeys(true).contains(uuid)) {
            player.setDisplayName("§a" + player.getName());
            data.set(uuid, 3); Config.saveData(data, player);
            updateTabColour(player);
        }
        else{
            int lives = data.getInt(event.getPlayer().getUniqueId().toString());
            updateColour(lives, player);
        }

    }

    public static void updateColour(int lives, Player player){
        if(player.hasPermission("thirdlife.bypass")) {
            player.setDisplayName(player.getName());
            return;
        }
        if(lives == 0) {
            player.setDisplayName(player.getName());
            player.setGameMode(GameMode.SPECTATOR);
        }else if(!player.isOp()) player.setGameMode(GameMode.SURVIVAL);
        if(lives == 3){
            player.setDisplayName("§a" + player.getName());
        }
        if(lives == 2){
            player.setDisplayName("§e" + player.getName());
        }
        if(lives == 1){
            player.setDisplayName("§c" + player.getName());
        }
        updateTabColour(player);
    }

    private static void updateTabColour(Player player){
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        String command = "nick " + player.getName() + " " + player.getDisplayName();
        Bukkit.dispatchCommand(console, command);
    }

}
