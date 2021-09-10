package plugin.ThirdLife;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.ThirdLife.Commands.TLComplete;
import plugin.ThirdLife.Commands.TLExec;
import plugin.ThirdLife.Listeners.OnPlayerDeath;
import plugin.ThirdLife.Listeners.OnPlayerJoin;


public class Main extends JavaPlugin {

    public void onEnable(){
        this.getServer().getPluginManager().registerEvents(new OnPlayerDeath(), this);
        this.getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
        this.getCommand("thirdlife").setExecutor(new TLExec());
        this.getCommand("thirdlife").setTabCompleter(new TLComplete());

        System.out.println("(ThirdLife) Successfully enabled");
    }

    public void onDisable(){
        HandlerList.unregisterAll(new OnPlayerDeath());
        HandlerList.unregisterAll(new OnPlayerJoin());
        System.out.println("(ThirdLife) Successfully disabled");
    }
}
