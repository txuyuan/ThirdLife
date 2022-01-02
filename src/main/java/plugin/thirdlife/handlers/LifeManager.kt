package plugin.thirdlife.handlers

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import plugin.thirdlife.types.CacheFile
import plugin.thirdlife.types.LifePlayer
import plugin.thirdlife.types.LivesFile
import plugin.thirdlife.types.PermissionException
import java.util.*

object LifeManager {

    fun init(){
        saveOnlinePlayers()
    }

    fun reset(){
        LivesFile().getKeys(false).forEach {
            try{
                LifePlayer(UUID.fromString(it)).lives = 7
            }catch(exception: PermissionException){}
            GhoulManager.reset()
        }
    }

    fun savePlayer(player: Player){
        CacheFile().set(player.uniqueId.toString(), player.name)
    }
    fun savePlayer(name: String, uuid: UUID){
        CacheFile().set(uuid.toString(), name)
    }
    fun saveOnlinePlayers(){
        Bukkit.getOnlinePlayers().forEach {
            savePlayer(it)
        }
    }

    fun getAllPlayers(): List<LifePlayer>{
        return LivesFile().getKeys(false).map { LifePlayer(UUID.fromString(it)) }
    }

    fun getPlayerUUID(name: String): UUID?{
        for (key : String in CacheFile().getKeys(false)){
            if(CacheFile().get(key)==name)
                return UUID.fromString(key)
        }
        return null
    }
    fun getPlayerName(uuid: UUID): String?{
        return CacheFile().config.getString(uuid.toString())
    }

    fun getLifeColours(lives: Int): TextColor{
        return when(lives){
            -1 -> NamedTextColor.GRAY
            0 -> NamedTextColor.DARK_RED
            1 -> NamedTextColor.RED
            2 -> NamedTextColor.YELLOW
            3 -> NamedTextColor.GREEN
            4,5,6,7 -> NamedTextColor.BLUE
            else -> NamedTextColor.WHITE
        }
    }

    fun getOnlinePlayer(offlinePlayer: OfflinePlayer): Player?{
        if(offlinePlayer is Player) return offlinePlayer
        if(!offlinePlayer.isOnline)
            return null
        else return offlinePlayer as Player
    }
}