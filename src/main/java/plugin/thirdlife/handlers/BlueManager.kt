package plugin.thirdlife.handlers

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.event.entity.PlayerDeathEvent
import plugin.thirdlife.logger
import plugin.thirdlife.types.*
import java.util.*

object BlueManager {

    fun checkDeath(event: PlayerDeathEvent){
        val player = LifePlayer(event.player)
        if(player.lives <= 3 || !player.allowedUse()!!)
            return

        var deathMsg: Component = Component.text("(Silenced) ").color(NamedTextColor.AQUA)
            .append(event.deathMessage()!!)
        Bukkit.getOnlinePlayers()
            .map { it.uniqueId }
            .forEach {
                val onlinePlayer = LifePlayer(it)
                deathMsg = deathMsg.replaceText(TextReplacementConfig.builder().match(onlinePlayer.name)
                    .replacement( onlinePlayer.nick!!.append(componentWhite()) ).build())
            }
        event.deathMessage(null) //Set empty

        logger().info(deathMsg)
        Bukkit.getOnlinePlayers()
            .filter { LifePlayer(it).allowedAdmin()!! }
            .forEach { it.sendStatus(deathMsg) }
    }

    fun newSession(){
        LivesFile().getKeys(false)
            .map { LifePlayer(UUID.fromString(it)) }
            .filter { it.lives > 3 }
            .forEach { it.removeLife() }
    }

}