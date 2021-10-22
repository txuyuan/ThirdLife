package plugin.ThirdLife.managers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import plugin.ThirdLife.Main;
import plugin.ThirdLife.data.Data;

public class LifeUpdate {

    public static void loadPlayer(Player player) {
        FileConfiguration data = Data.getLivesData();
        if (player.hasPermission("thirdlife.bypass")) {
            player.setDisplayName(player.getName());
            return;
        }
        Main.logTest(player.getName() + " no permission");
        String uuid = player.getUniqueId().toString();
        if (!data.getKeys(false).contains(uuid)) {
            data.set(uuid, 3);
            Data.saveLivesData(data, player);
            updateColour(player, 3);
        } else {
            int lives = data.getInt(uuid);
            Main.logTest("lives=" + lives);
            updateColour(player, lives);
        }
    }


    public static void addLife(Player player, boolean isAdd) {
        if (player.hasPermission("thirdlife.bypass"))
            return;
        FileConfiguration data = Data.getLivesData();
        String uuid = player.getUniqueId().toString();

        int lives = data.getInt(uuid);
        if (isAdd && lives > 2) return;
        if (!isAdd && lives < 0) return;


        int newLives = lives + (isAdd ? 1 : -1);
        if (lives == 1 && !isAdd && GhoulManager.getHasBeenGhoul(player))
            newLives = -1;

        data.set(uuid, newLives);
        Data.saveLivesData(data, Bukkit.getConsoleSender());
        updateColour(player, newLives);
    }


    public static void updateColour(Player player, int lives) {
        if (player.hasPermission("thirdlife.bypass")) {
            player.setDisplayName(player.getName());
            return;
        }

        if (lives < -1) {
            lives = -1;
            saveLives(player, -1);
        }
        if (lives > 3) {
            lives = 3;
            saveLives(player, 3);
        }

        player.setGameMode(lives == -1 ? GameMode.SPECTATOR : GameMode.SURVIVAL);
        player.setDisplayName("§" + getColour(lives) + player.getName());
        updateNick(player, lives);

        if (lives == 0) {
            setHealth(player, false);
            GhoulManager.setHasBeenGhoul(player, true);
        } else setHealth(player, true);

        Main.logTest(player.getDisplayName());
    }

    private static void updateNick(Player player, int lives) {
        String playerName = player.getName();
        String colouredName = "&" + getColour(lives) + playerName;
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "nick " + playerName + " " + colouredName);
    }


    private static void setHealth(Player player, boolean isFull) {
        String command = "attribute " + player.getName() + " minecraft:generic.max_health base set " + (isFull ? "20" : "10");
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    }


    private static char getColour(int lives) {
        if (lives == -1) return '7';
        if (lives == 0) return '4';
        if (lives == 1) return 'c';
        if (lives == 2) return 'e';
        if (lives == 3) return 'a';
        return '0';
    }

    private static void saveLives(Player player, int lives) {
        FileConfiguration fileC = Data.getLivesData();
        fileC.set(player.getUniqueId().toString(), lives);
        Data.saveLivesData(fileC, Bukkit.getConsoleSender());
    }


}
