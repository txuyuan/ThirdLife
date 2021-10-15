package plugin.ThirdLife.data;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import plugin.ThirdLife.Main;

import java.io.File;
import java.io.IOException;

public class Config {

    public static FileConfiguration getData() {
        File dataFile = new File(Bukkit.getPluginManager().getPlugin("ThirdLife").getDataFolder(), "lives.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
        return data;
    }

    public static void saveData(FileConfiguration data, Player player) {
        File dataFile = new File(Bukkit.getPluginManager().getPlugin("ThirdLife").getDataFolder(), "lives.yml");
        try {
            data.save(dataFile);
        } catch (IOException i) {
            Main.logDiskError(i);
            player.sendMessage("§c(Error)§f Error writing to disk");
        }
    }

    public static void saveDataS(FileConfiguration data, CommandSender sender) {
        File dataFile = new File(Bukkit.getPluginManager().getPlugin("ThirdLife").getDataFolder(), "lives.yml");
        try {
            data.save(dataFile);
        } catch (IOException i) {
            Main.logDiskError(i);
            sender.sendMessage("§c(Error)§f Error writing to disk");
        }
    }

}
