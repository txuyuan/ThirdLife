package plugin.ThirdLife.managers

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import plugin.ThirdLife.Main
import plugin.ThirdLife.data.Data
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

object BlueManager {

    fun checkBlueDeath(event: PlayerDeathEvent) {
        val fileC = LifeUpdate.data
        val lives = fileC.getInt(event.entity.uniqueId.toString())
        if (lives <= 3) return

        val msg = event.deathMessage
        event.deathMessage = ""
        val ops = Bukkit.getOnlinePlayers().stream().filter { player: Player -> player.hasPermission("thirdlife.admin") }.collect(
            Collectors.toList())

        ops.forEach(Consumer { player: Player -> player.sendMessage("§b(Silenced)§f $msg") })
    }

    fun newSession(){
        val fileC = LifeUpdate.data

        fileC.getKeys(false).forEach(Consumer{
            val lives = fileC.getInt(it)
            val player = Bukkit.getOfflinePlayer(UUID.fromString(it))
            if(lives > 3){
                LifeUpdate.removeLife(player)
            }
        })
        Main.logInfo("§b(Status)§f New ThirdLife session started!")
    }

}