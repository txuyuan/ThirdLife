package plugin.thirdlife.handlers

import org.bukkit.event.entity.PlayerDeathEvent
import plugin.thirdlife.types.GhoulsFile
import plugin.thirdlife.types.LifePlayer
import plugin.thirdlife.types.LivesFile
import java.util.*

object GhoulManager {

    fun newSession(){
        val ghouls = LivesFile().getKeys(false)
            .filter { LivesFile().config.getInt(it) == 0 }
            .map { LifePlayer(UUID.fromString(it)) }
        ghouls.forEach{
            it.lives = 0
        }
    }

    fun setGhoul(player: LifePlayer, isGhoul: Boolean){
        GhoulsFile().set(player.uuid.toString(), isGhoul)
    }

    fun getGhoul(player: LifePlayer): Boolean{
        return GhoulsFile().config.getBoolean(player.uuid.toString())
    }

    fun checkGhoulKiller(event: PlayerDeathEvent){
        val player = LifePlayer(event.player)
        val killer = LifePlayer(event.player.killer ?: return)
        if(killer.lives != 0 || player.lives == 0) return //Killer is ghoul || killing other ghoul

        killer.addLife()
    }

    fun reset(){
        GhoulsFile().getKeys(false).forEach {
            LifePlayer(UUID.fromString(it)).hasGhoul = false
        }
    }

}