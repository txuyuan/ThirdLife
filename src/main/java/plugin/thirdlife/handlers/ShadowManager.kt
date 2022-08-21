package plugin.thirdlife.handlers

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
            logger().severe(errorMsg + ": " + currentShadows.joinToString(", ")) //Log detected shadows' names
            //Error message for user
            throw Exception(errorMsg)
        }
        return currentShadows[0]
    }

    fun getOldShadows(): List<LifePlayer> {
        return LifeManager.getAllPlayers()
            .filter { it.isOldGhoul }
    }

}