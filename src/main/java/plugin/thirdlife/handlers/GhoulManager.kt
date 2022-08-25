package plugin.thirdlife.handlers

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import plugin.thirdlife.types.LifePlayer
import java.util.DoubleSummaryStatistics

object GhoulManager {

    fun endSession(ghouls: List<LifePlayer>){
        // Specify list of ghouls to kill
        ghouls.forEach{
            it.removeLife()
        }
    }
    fun endSession(){
        // Kill all remaining ghouls
        val ghouls = LifeManager.getAllPlayers()
            .filter { it.isGhoul }
        ghouls.forEach{
            killOfflinePlayer(it)
        }
    }


    fun checkGhoulKiller(event: PlayerDeathEvent){
        val player = LifePlayer(event.player)
        val killer = LifePlayer(event.player.killer ?: return)
        if(killer.lives != 0 || player.lives == 0) return //Killer is ghoul || killing other ghoul

        killer.addLife()
    }
    fun checkGhoulPunch(event: EntityDamageByEntityEvent) {
        val player = LifePlayer(event.entity as Player)
        val damager = LifePlayer(event.damager as Player)

        if (!(player.lives==0 && damager.lives==0)) return //Not both ghouls

        event.isCancelled = true
        (damager.offlinePlayer as Player).sendError(Component.text("You cannot damage another ghoul"))
    }

    fun getGhouls(): List<LifePlayer> {
        val ghouls = LifeManager.getAllPlayers()
            .filter { it.isGhoul }
        return ghouls
    }

}
