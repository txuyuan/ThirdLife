package plugin.ThirdLife.managers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import plugin.ThirdLife.Main;
import plugin.ThirdLife.data.Data;

public class LifeUpdate {

    public static FileConfiguration data = Data.getLivesData();

    public static void load(){
        data = Data.getLivesData();
    }



    public static void loadPlayer(Player player) {
        if (player.hasPermission("thirdlife.bypass"))
            return;
        String uuid = player.getUniqueId().toString();
        if (!data.getKeys(false).contains(uuid)) {
            data.set(uuid, 7);
            Data.saveLivesData(data, (CommandSender)player);
            updateColour(player, 7);
        } else {
            int lives = data.getInt(uuid);
            Main.logTest("lives=" + lives);
            updateColour(player, lives);
        }
    }


    public static int getLives(OfflinePlayer player){
        return data.getInt(player.getUniqueId().toString());
    }

    public static String removeLife(OfflinePlayer player){
        return addLife(player, false);
    }
    public static String addLife(OfflinePlayer player, boolean isAdd) {
        if(player instanceof Player && ((Player) player).hasPermission("thirdlife.bypass")) return "§c(Error)§f Target player has bypass node";
        String uuid = player.getUniqueId().toString();

        int lives = data.getInt(uuid);
        if (isAdd && lives >= 7){
            Data.saveLivesData(data, player);
            return "§b(Status)§f " + player.getName() + " already has 7 or more lives";
        }
        if (!isAdd && lives <= -1){
            data.set(uuid, -1);
            Data.saveLivesData(data, player);
            return "§b(Status)§f " + player.getName() + " is already dead";
        }


        int newLives = lives + (isAdd ? 1 : -1);
        if (lives == 1 && !isAdd && GhoulManager.getHasBeenGhoul(player))
            newLives = -1;

        data.set(uuid, newLives);
        Data.saveLivesData(data, Bukkit.getConsoleSender());
        if(player instanceof Player) updateColour((Player)player, newLives);
        return "§b(Status)§f They now have " + lives + " lives";
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
        if (lives > 7) {
            lives = 7;
            saveLives(player, 7);
        }

        player.setGameMode(lives == -1 ? GameMode.SPECTATOR : GameMode.SURVIVAL);
        player.setDisplayName("§" + getColour(lives) + player.getName() + "§f");

        if (lives == 0) {
            setHealth(player, false);
            GhoulManager.setHasBeenGhoul(player, true);
        } else setHealth(player, true);

        Main.logTest(player.getDisplayName());
    }


    private static void setHealth(Player player, boolean isFull) {
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(isFull ? 20 : 10);
    }


    private static char getColour(int lives) {
        return switch(lives){
            case -1 -> '7';
            case 0 -> '4';
            case 1 -> 'c';
            case 2 -> 'e';
            case 3 -> 'a';
            case 4,5,6,7 -> '9';
            default -> 'f';
        };

    }

    private static void saveLives(Player player, int lives) {
        FileConfiguration fileC = Data.getLivesData();
        fileC.set(player.getUniqueId().toString(), lives);
        Data.saveLivesData(fileC, Bukkit.getConsoleSender());
    }



}
