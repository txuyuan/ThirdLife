package plugin.thirdlife.types

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.title.TitlePart
import org.bukkit.Bukkit
import org.bukkit.EntityEffect
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.attribute.Attribute
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import plugin.thirdlife.Main
import plugin.thirdlife.handlers.GhoulManager
import plugin.thirdlife.handlers.LifeManager
import plugin.thirdlife.handlers.NickManager
import plugin.thirdlife.logger
import java.util.*
import java.util.logging.Logger

class LifePlayer{
    var player: OfflinePlayer
    var name: String
    var uuid: UUID
    var lives: Int
        set(value){
            if(!(allowedUse() ?: false))
                throw PermissionException()

            var newLives = value
            if(newLives > 7)
                throw LifeException("You cannot have more than 7 lives")
            if(newLives < -1)
                throw LifeException("You cannot have less than 0 lives")

            if(newLives==0 && GhoulManager.getGhoul(this))
                newLives = -1

            LivesFile().set(uuid.toString(), newLives)
            logger().info("Player $name now has $newLives lives")
            save()
            update()
        }
        get(){
            val tmpLives =
                if( !LivesFile().getKeys(false).contains(uuid.toString())  ||
                    LivesFile().config.getInt(uuid.toString())<-1 || LivesFile().config.getInt(uuid.toString())>7 ){
                    LivesFile().set(uuid.toString(), 7)
                    7
                }else
                    LivesFile().config.getInt(uuid.toString())

            return tmpLives
        }
    var hasGhoul: Boolean
        set(ghoul){
            GhoulManager.setGhoul(this, ghoul)
        } get(){
            return GhoulManager.getGhoul(this)
        }
    var nick:Component?
        set(nick){
            NickManager.setNick(this, nick)
            player.player?.displayName(nick)
        } get(){
            return NickManager.getNick(this)
        }



    private fun addRemoveLife(isAdd: Boolean){
        if(isAdd && lives == 7)
            throw LifeException("You cannot have more than 7 lives")
        if(!isAdd && lives == 0)
            throw LifeException("You cannot have less than 0 lives")

        if(!hasGhoul && !isAdd && lives==1) // First time losing red life
            lives -= 2
        else
            lives += (if(isAdd) 1 else -1)
    }
    fun addLife(){ addRemoveLife(true) }
    fun removeLife(){ addRemoveLife(false) }

    fun giveLife(target: LifePlayer){
        if(lives <=1) //Cannot sacrifice to dead/ghoul
            throw LifeException("You cannot give away your last life")
        if(target.lives==7)
            throw LifeException("Target already has 7 lives")
        this.removeLife()
        target.addLife()
        giveNotifs(this, target)
    }
    private fun giveNotifs(player: LifePlayer, target: LifePlayer){
        val onlinePlayer = player.player.player!!
        onlinePlayer.playEffect(EntityEffect.TOTEM_RESURRECT)
        onlinePlayer.sendTitlePart(TitlePart.TITLE, Component.text("You gave a life to"))
        onlinePlayer.sendTitlePart(TitlePart.SUBTITLE, target.nick!!)

        if(target.player.isOnline){
            val onlineTarget = target.player.player!!
            onlineTarget.sendTitlePart(TitlePart.TITLE, Component.text("You have been given a life by"))
            onlineTarget.sendTitlePart(TitlePart.SUBTITLE, player.nick!!)
        }
    }


    constructor(name: String){
        val tmpUuid = LifeManager.getPlayerUUID(name)
        if(tmpUuid==null) throw CacheException("Playername $name not found in cache")
        this.name = name
        this.uuid = tmpUuid
        this.player = Bukkit.getOfflinePlayer(uuid)
    }
    constructor(uuid: UUID){
        val tmpName = LifeManager.getPlayerName(uuid)
        if(tmpName==null) throw CacheException("Uuid $uuid not found in cache")
        this.name = tmpName
        this.uuid = uuid
        this.player = Bukkit.getOfflinePlayer(uuid)
    }
    constructor(player: Player){
        LifeManager.savePlayer(player)
        this.name = player.name
        this.uuid = player.uniqueId
        this.player = player
    }


    // -------- PERMISSIONS --------
    //** Null if player not online */
    fun allowedUse(): Boolean?{
        return allowed("thirdlife.use")
    }
    //** Null if player not online */
    fun allowedAdmin(): Boolean?{
        return allowed("thirdlife.admin")
    }
    fun allowed(permission: String): Boolean?{
        val onlinePlayer =
            if(player is Player) player as Player
            else LifeManager.getOnlinePlayer(player) ?: return null
        return onlinePlayer.hasPermission(permission)
    }


    fun update(){
        val onlinePlayer = LifeManager.getOnlinePlayer(player) ?: return
        if(!allowedUse()!!) return //canUse() notnull ^

        if(lives==-1){
            if(!onlinePlayer.inventory.isEmpty){
                onlinePlayer.inventory.forEach {
                    onlinePlayer.location.world.dropItemNaturally(onlinePlayer.location, it)
                    onlinePlayer.inventory.remove(it)
                }
            }
            onlinePlayer.gameMode = GameMode.SPECTATOR
        }else
            onlinePlayer.gameMode = GameMode.SURVIVAL

        onlinePlayer.displayName(nick)

        if(lives==0) {
            setHealth(false)
            GhoulManager.setGhoul(this, true)
        }else
            setHealth(true)

        object : BukkitRunnable(){ override fun run() {
            onlinePlayer.sendStatus(
                when(lives){
                    -1 -> "You are dead"
                    0 -> "You are a ghoul"
                    else -> "You have $lives lives"
                }
            )
        }}.runTaskLater(Main.getInstance(), 1)
        save()
    }

    fun setHealth(isFull: Boolean){
        val onlinePlayer = LifeManager.getOnlinePlayer(player) ?: return
        onlinePlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue =
            if(isFull) 20.0 else 10.0
    }





    fun save(){
        LifeManager.savePlayer(name, uuid)
        LivesFile().set(uuid.toString(), lives)
    }

}



fun CommandSender.sendStatus(message: String){
    sendMessage(Component.text(message)/*.color(NamedTextColor.WHITE)*/)
}
fun CommandSender.sendInfo(message: String){
    sendMessage(Component.text(message)/*.color(NamedTextColor.WHITE)*/)
}
fun CommandSender.sendError(message: String){
    sendMessage(Component.text(message).color(NamedTextColor.RED))
}

fun CommandSender.sendStatus(message: Component){
    sendMessage(message/*.color(NamedTextColor.WHITE)*/)
}
fun CommandSender.sendInfo(message: Component){
    sendMessage(message/*.color(NamedTextColor.WHITE)*/)
}
fun CommandSender.sendError(message: Component){
    sendMessage(message.color(NamedTextColor.RED))
}

fun Logger.info(msg: Component){
    val serMsg = LegacyComponentSerializer.legacySection().serialize(msg)
    info(serMsg)
}

fun componentWhite(): Component{
    return LegacyComponentSerializer.legacyAmpersand().deserialize("§r")
}

fun getOnlinePlayerNames(): MutableList<String>{
    return Bukkit.getOnlinePlayers().map { it.name }.toMutableList()
}



fun Component.reset(): Component{
    decoration(TextDecoration.ITALIC, false)
    decoration(TextDecoration.BOLD, false)
    decoration(TextDecoration.OBFUSCATED, false)
    decoration(TextDecoration.STRIKETHROUGH, false)
    decoration(TextDecoration.UNDERLINED, false)
    color(NamedTextColor.WHITE)
    return this
}