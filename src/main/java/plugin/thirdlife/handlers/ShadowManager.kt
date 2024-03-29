package plugin.thirdlife.handlers

import net.kyori.adventure.text.Component
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

        if (shadow!=null){
            killOfflinePlayer(shadow)
            shadow.isShadow = false
        }
    }

    fun newSession() {
        LifeManager.getAllPlayers().forEach {
            PlayersFile().setIsShadow(it.uuid, false)
        }
        // Assign shadow to random player
        val newShadow = LifeManager.getAllPlayers()
            .filter { it.allowedAdmin() != true }
            .random()
        newShadow.isShadow = true
        ScoreboardManager.updatePlayerBoards() //Force board update

        // Notify admins of shadow
        val admins = LifeManager.getAllPlayers().filter { it.allowedAdmin()==true }
        val shadowName = newShadow.name
        val msg = "Assigned shadow is $shadowName"
        logger().info(msg)
        admins.forEach {
            it.onlinePlayer?.sendStatus(msg)
        }
    }

    fun checkShadowPunch(event: EntityDamageByEntityEvent) {
        val player = LifePlayer(event.entity as Player)
        val damager = LifePlayer(event.damager as Player)
        if (!damager.isShadow) // Only continue if damager is shadow
            return
        if (player.isOldShadow) // Reject if damagee is old shadow
            return

        damager.isShadow = false
        player.isShadow = true

        // Set damager only as old shadow
        getOldShadow()?.isOldShadow = false
        damager.isOldShadow = true

        damager.onlinePlayer?.sendMessage(formatStatus(Component.text("You have been shadow touched!")))
    }

    fun getShadow(): LifePlayer? {
        val currentShadows = LifeManager.getAllPlayers()
            .filter { it.isShadow }
        if (currentShadows.size < 1)
            return null
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

    fun getOldShadow(): LifePlayer? {
        val oldShadows = LifeManager.getAllPlayers()
            .filter { it.isOldShadow }
        if (oldShadows.size < 0)
            return null
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
