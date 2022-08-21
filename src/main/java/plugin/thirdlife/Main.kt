package plugin.thirdlife

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import plugin.thirdlife.commands.completers.TLComplete
import plugin.thirdlife.commands.exec.TLExec
import plugin.thirdlife.listeners.ThirdLifeListeners
import plugin.thirdlife.types.ConfigFile
import plugin.thirdlife.types.LifePlayer
import java.util.logging.Logger

class Main : JavaPlugin(){

    override fun onEnable(){
        LifePlayer.init()

        server.pluginManager.registerEvents(ThirdLifeListeners(),this)

        getCommand("thirdlife")?.setExecutor(TLExec())
        getCommand("thirdlife")?.setTabCompleter(TLComplete())

        Bukkit.getOnlinePlayers().forEach { LifePlayer(it).update() }
        logger().info("§aPlugin enabled")
    }

    override fun onDisable() {
        HandlerList.unregisterAll(this)
        logger().info("§aPlugin disabled")
    }

    companion object{
        fun getInstance(): Plugin{
            return Bukkit.getPluginManager().getPlugin("ThirdLife")!!
        }
    }


}

fun logger(): Logger{
    return Main.getInstance().logger
}
@Suppress("unused")
fun Logger.debug(mesage: String){
    var isDebug = false
    try{
        isDebug = ConfigFile().config.getBoolean("debug")
    }catch(exception: Exception){
        ConfigFile().set("debug", false)
        isDebug = false
    }
    if(isDebug)
        info(mesage)
}

fun getOnlinePlayerNames(): MutableList<String>{
    return Bukkit.getOnlinePlayers().map{ it.name }.toMutableList();
}