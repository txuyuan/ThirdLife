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
        this.getServer().getPluginManager().registerEvents((Listener)new OnPlayerDeath(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new OnPlayerJoin(), (Plugin)this);
        this.getCommand("thirdlife").setExecutor((CommandExecutor)new TLExec());
        this.getCommand("thirdlife").setTabCompleter((TabCompleter)new TLComplete());

        System.out.println("(ThirdLife) Successfully enabled");
    }

    public void onDisable(){
        HandlerList.unregisterAll((Listener)new OnPlayerDeath());
        HandlerList.unregisterAll((Listener)new OnPlayerJoin());
        System.out.println("(ThirdLife) Successfully disabled");
    }
}
