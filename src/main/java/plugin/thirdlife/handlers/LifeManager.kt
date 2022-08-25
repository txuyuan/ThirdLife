package plugin.thirdlife.handlers

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import plugin.thirdlife.types.LifePlayer
import plugin.thirdlife.types.PlayersFile
import java.util.*

object LifeManager {

    fun resetAll(){
        getAllPlayers()
            .forEach {

            it.lives = LifeManager.getMaxLives()
            it.isOldGhoul = false
            it.isShadow = false
            it.isOldShadow = false

            it.update()
        }
    }

    fun playerNameToUUID(name: String): UUID?{
        for (player in getAllPlayers()) {
            if (player.name == name) {
                return player.uuid
            }
        }
        return null
    }

    fun getAllPlayers(): List<LifePlayer>{
        val uuids = PlayersFile().getKeys(false)
        return uuids.map { LifePlayer(UUID.fromString(it)) }
    }

    fun getOnlinePlayer(offlinePlayer: OfflinePlayer): Player?{
        return getOnlinePlayer(offlinePlayer.uniqueId)
    }
    fun getOnlinePlayer(uuid: UUID): Player? {
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.uniqueId.equals(uuid))
              return player
        }
        return null;
    }

    fun getLifeColours(lives: Int): TextColor{
        return when(lives){
            -1 -> NamedTextColor.GRAY
            0 -> NamedTextColor.DARK_RED
            1 -> NamedTextColor.RED
            2 -> NamedTextColor.YELLOW
            3 -> NamedTextColor.GREEN
            else -> NamedTextColor.WHITE
        }
    }
    fun getMaxLives(): Int {
        return 3
    }
}
