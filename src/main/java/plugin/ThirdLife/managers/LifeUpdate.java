package plugin.ThirdLife.managers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import plugin.ThirdLife.Main;
import plugin.ThirdLife.data.Config;

public class LifeUpdate {

    public static void removeOne(Player player) {
        if (player.hasPermission("thirdlife.bypass"))
            return;
        FileConfiguration data = Config.getData();
        String uuid = player.getUniqueId().toString();

        int lives = data.getInt(uuid);
        if (lives == 1) {
            data.set(uuid, 0); Config.saveData(data, player);
            updateColour(player, 0);
        } else if (lives > 0 && lives <= 3) {
            data.set(uuid, lives - 1); Config.saveData(data, player);
            updateColour(player, lives - 1);
        }
    }

    public static void loadPlayer(Player player){
        FileConfiguration data = Config.getData();
        if (player.hasPermission("thirdlife.bypass")) {
            player.setDisplayName(player.getName());
            return;
        }
        Main.logTest(player.getName() + " no permission");
        String uuid = player.getUniqueId().toString();
        if (!data.getKeys(true).contains(uuid)) {
            data.set(uuid, 3);
            Config.saveData(data, player);
            updateColour(player, 3);
        } else {
            int lives = data.getInt(uuid);
            Main.logTest("lives=" + lives);
            updateColour(player, lives);
        }
    }

    public static void updateColour(Player player, int lives) {
        if (player.hasPermission("thirdlife.bypass")) {
            player.setDisplayName(player.getName());
            return;
        }
        if (lives == 0) {
            player.setDisplayName("§7" + player.getName());
            player.setGameMode(GameMode.SPECTATOR);
        } else if (!player.isOp()) player.setGameMode(GameMode.SURVIVAL);
        if (lives == 3) {
            player.setDisplayName("§a" + player.getName());
            updateTabColour(player, 3);
        }
        if (lives == 2) {
            player.setDisplayName("§e" + player.getName());
            updateTabColour(player, 2);
        }
        if (lives == 1) {
            player.setDisplayName("§c" + player.getName());
            updateTabColour(player, 1);
        }
        Main.logTest(player.getDisplayName());
    }

    private static void updateTabColour(Player player, int lives) {
        String colour = "&a";
        if (lives == 1) colour = "&c";
        if (lives == 2) colour = "&e";
        if (lives == 3) colour = "&a";

        Main.logTest("Colour : " + colour);

        String colouredName = colour + player.getName();
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "nick " + player.getName() + " " + colouredName);

        Main.logTest("Colouredname " + colouredName);
    }

}
