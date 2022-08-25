package plugin.thirdlife.handlers

import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import plugin.thirdlife.logger
import plugin.thirdlife.types.LifePlayer

object ShadowManager {

    fun endSession() {
        // Kill remaining shadow
        val shadow = getCurrentShadow()
        shadow.lives = 0
        shadow.isShadow = false
    }

    fun getCurrentShadow(): LifePlayer {
        val currentShadows = LifeManager.getAllPlayers()
            .filter { it.isShadow }
        if (currentShadows.size > 1) {
            val errorMsg = "Error. Multiple shadows are saved"
            //Log for debug & report
            val shadowNames = currentShadows.map { it.name }
            logger().severe(errorMsg + ": " + shadowNames.joinToString(", ")) //Log detected shadows' names
            //Error message for user
            throw Exception(errorMsg)
        }
        return currentShadows[0]
    }

    fun getOldShadows(): List<LifePlayer> {
        return LifeManager.getAllPlayers()
            .filter { it.isOldGhoul }
    }

    fun checkShadowPunch(event: EntityDamageByEntityEvent) {
        val player = LifePlayer(event.entity as Player)
        val damager = LifePlayer(event.damager as Player)
      
        if (damager.isShadow) {
          damager.isShadow = false
          player.isShadow = true
        }
    }

}
