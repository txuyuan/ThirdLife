package plugin.ThirdLife.Data;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class Config {

    public static FileConfiguration getData(){
        File dataFile = new File(Bukkit.getPluginManager().getPlugin("ThirdLife").getDataFolder(), "lives.yml");
        FileConfiguration data = (FileConfiguration) YamlConfiguration.loadConfiguration(dataFile);
        return data;
    }

    public static void saveData(FileConfiguration data, Player player){
        File dataFile = new File(Bukkit.getPluginManager().getPlugin("ThirdLife").getDataFolder(), "lives.yml");
        try{
            data.save(dataFile);
        }catch(IOException i){
            System.out.println("(ThirdLife) Error saving to disk | " + player.getName());
            player.sendMessage("§c(Error)§f Error saving to disk");
        }
    }

    public static void saveDataS(FileConfiguration data, CommandSender sender){
        File dataFile = new File(Bukkit.getPluginManager().getPlugin("ThirdLife").getDataFolder(), "lives.yml");
        try{
            data.save(dataFile);
        }catch(IOException i){
            System.out.println("(ThirdLife) Error saving to disk | " + sender.getName());
            sender.sendMessage("§c(Error)§f Error saving to disk");
        }
    }

}
