package plugin.thirdlife.handlers

import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import plugin.thirdlife.logger
import plugin.thirdlife.scoreboards.ScoreboardManager
import plugin.thirdlife.types.LifePlayer
import plugin.thirdlife.types.PlayersFile

object ShadowManager {

    fun endSession() {
        // Kill remaining shadow
        val shadow = getShadow()

        killOfflinePlayer(shadow)
        shadow.isShadow = false
    }

    fun newSession() {
        LifeManager.getAllPlayers().forEach {
            PlayersFile().setIsShadow(it.uuid, false)
        }
        // Assign shadow to random player
        val newShadow = LifeManager.getAllPlayers().random()
        newShadow.isShadow = true
        ScoreboardManager.updatePlayerBoards() //Force board update

        // Notify admins of shadow
        val admins = LifeManager.getAllPlayers().filter { it.allowedAdmin()==true }
        val shadowName = newShadow.name
        admins.forEach {
            it.onlinePlayer?.sendStatus("Assigned shadow is $shadowName")
        }
    }

    fun checkShadowPunch(event: EntityDamageByEntityEvent) {
        val player = LifePlayer(event.entity as Player)
        val damager = LifePlayer(event.damager as Player)
        if (!damager.isShadow)
            return

        damager.isShadow = false
        player.isShadow = true

        // Set damager only as old shadow
        getOldShadow().isOldShadow = false
        damager.isOldShadow = true
    }

    fun getShadow(): LifePlayer {
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

    fun getOldShadow(): LifePlayer {
        val oldShadows = LifeManager.getAllPlayers()
            .filter { it.isOldShadow }
        if (oldShadows.size > 1) {
            val errorMsg = "Error. Multiple old shadows are saved"
            //Log for debug & report
            val shadowNames = oldShadows.map { it.name }
            logger().severe(errorMsg + ": " + shadowNames.joinToString(", ")) //Log detected shadows' names
            //Error message for user
            throw Exception(errorMsg)
        }
        return oldShadows[0]
    }

}
