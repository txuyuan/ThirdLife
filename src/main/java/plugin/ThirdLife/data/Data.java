package plugin.ThirdLife.data;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import plugin.ThirdLife.Main;

import java.io.File;
import java.io.IOException;

public class Data {

    public static FileConfiguration getLivesData() {
        File dataFile = new File(Bukkit.getPluginManager().getPlugin("ThirdLife").getDataFolder(), "lives.yml");
        FileConfiguration fileC = YamlConfiguration.loadConfiguration(dataFile);
        return fileC;
    }

    public static void saveLivesData(FileConfiguration data, CommandSender sender) {
        File dataFile = new File(Bukkit.getPluginManager().getPlugin("ThirdLife").getDataFolder(), "lives.yml");
        try {
            data.save(dataFile);
        } catch (IOException i) {
            Main.logDiskError(i);
            sender.sendMessage("§c(Error)§f Error writing to disk");
        }
    }


}
