package plugin.ThirdLife.listeners

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.scheduler.BukkitRunnable
import plugin.ThirdLife.Main
import plugin.ThirdLife.data.Data
import plugin.ThirdLife.managers.BlueManager
import plugin.ThirdLife.managers.GhoulManager
import plugin.ThirdLife.managers.LifeUpdate
import java.util.function.Consumer
import java.util.stream.Collectors

class PlayerDeathListener : Listener {


    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        BlueManager.checkBlueDeath(event)
        object : BukkitRunnable() {
            override fun run() {
                LifeUpdate.addLife(event.entity, false)
                GhoulManager.checkGhoulKill(event)
            }
        }.runTaskLater(Main.getInstance(), 1)
    }





}