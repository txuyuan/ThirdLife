package plugin.ThirdLife.data;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
        String error = saveConfig(data);
        if(error!=null) sender.sendMessage(error);
    }
    public static void saveLivesData(FileConfiguration data, OfflinePlayer player){
        String error = saveConfig(data);
        if(error!=null) Main.logInfo(error);
    }

    private static String saveConfig(FileConfiguration data){
        File dataFile = new File(Bukkit.getPluginManager().getPlugin("ThirdLife").getDataFolder(), "lives.yml");
        try {
            data.save(dataFile);
        } catch (IOException i) {
            Main.logDiskError(i);
            return "§c(Error)§f Error writing to disk";
        }
        return null;
    }


}
