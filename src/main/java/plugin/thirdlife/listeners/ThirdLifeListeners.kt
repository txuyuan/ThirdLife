package plugin.thirdlife.listeners

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scheduler.BukkitRunnable
import plugin.thirdlife.Main
import plugin.thirdlife.handlers.GhoulManager
import plugin.thirdlife.handlers.sendError
import plugin.thirdlife.types.LifePlayer

class ThirdLifeListeners : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onDeath(event: PlayerDeathEvent){
        object : BukkitRunnable() {
            override fun run() {
                LifePlayer(event.player).removeLife()
                GhoulManager.checkGhoulKiller(event)
                event.isCancelled = true
            }
        }.runTaskLater(Main.getInstance(), 1)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent){
        LifePlayer(event.player).update()
    }

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent){
        if(!(event.entity is Player) || !(event.damager is Player))
            return
        val player = LifePlayer(event.entity as Player)
        val damager = LifePlayer(event.damager as Player)

        if (!(player.lives==0 && damager.lives==0)) return //Not both ghouls

        event.isCancelled = true
        (damager.offlinePlayer as Player).sendError(Component.text("You cannot damage another ghoul"))
    }


}
