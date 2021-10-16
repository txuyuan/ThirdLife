package plugin.ThirdLife;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.ThirdLife.commands.TLComplete;
import plugin.ThirdLife.commands.TLExec;
import plugin.ThirdLife.listeners.GhoulDamageListener;
import plugin.ThirdLife.listeners.OnPlayerDeath;
import plugin.ThirdLife.listeners.OnPlayerJoin;
import plugin.ThirdLife.managers.LifeUpdate;

import java.io.IOException;
import java.util.logging.Level;


public class Main extends JavaPlugin {

    public static Plugin getInstance() {
        return Bukkit.getPluginManager().getPlugin("ThirdLife");
    }

    public static void logInfo(String msg) {
        getInstance().getLogger().log(Level.INFO, msg);
    }

    public static void logDiskError(IOException e) {
        getInstance().getLogger().log(Level.SEVERE, "§c(Error)§f Error writing to disk: \n" + e.getStackTrace().toString());
    }

    public static void logTest(String msg) {
        boolean isDebug = false;
        if (isDebug)
            logInfo("§aTest: " + msg);
    }

    public void onEnable() {
        getServer().getPluginManager().registerEvents(new OnPlayerDeath(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new GhoulDamageListener(), this);
        getCommand("thirdlife").setExecutor(new TLExec());
        getCommand("thirdlife").setTabCompleter(new TLComplete());

        Bukkit.getOnlinePlayers().forEach(player -> LifeUpdate.loadPlayer(player));

        logInfo("§b(Status)§f Plugin enabled");
    }

    public void onDisable() {
        HandlerList.unregisterAll(new OnPlayerDeath());
        HandlerList.unregisterAll(new OnPlayerJoin());
        logInfo("§b(Status)§f Plugin enabled");
    }


}
