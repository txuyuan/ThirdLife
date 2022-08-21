package plugin.thirdlife.handlers

import org.bukkit.event.entity.PlayerDeathEvent
import plugin.thirdlife.types.LifePlayer

object GhoulManager {

    fun endSession(){
        // Kill all remaining ghouls
        val ghouls = LifeManager.getAllPlayers()
            .filter { it.isGhoul }
        ghouls.forEach{
            it.lives = 0
        }
    }

    fun checkGhoulKiller(event: PlayerDeathEvent){
        val player = LifePlayer(event.player)
        val killer = LifePlayer(event.player.killer ?: return)
        if(killer.lives != 0 || player.lives == 0) return //Killer is ghoul || killing other ghoul

        killer.addLife()
    }

    fun reset(){
        LifeManager.getAllPlayers().forEach {
            it.isGhoul = false
        }
    }

}
