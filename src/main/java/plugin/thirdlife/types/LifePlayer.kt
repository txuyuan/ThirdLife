package plugin.thirdlife.types

import net.kyori.adventure.text.Component
import net.kyori.adventure.title.TitlePart
import org.bukkit.Bukkit
import org.bukkit.EntityEffect
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import plugin.thirdlife.Main
import plugin.thirdlife.handlers.LifeManager
import plugin.thirdlife.handlers.NickManager
import plugin.thirdlife.handlers.sendStatus
import plugin.thirdlife.logger
import java.util.*

class LifePlayer{
    val offlinePlayer: OfflinePlayer
        get() {
            return Bukkit.getOfflinePlayer(uuid)
        }
    val isOnline: Boolean
        get() {
            return onlinePlayer != null
        }
    val onlinePlayer: Player?
        get() {
            return LifeManager.getOnlinePlayer(offlinePlayer)
        }
    val name: String
        get() {
            return PlayersFile().getName(uuid)!!
        }
    var uuid: UUID
    var nick:Component?
        set(nick){
            NickManager.setNick(this, nick)
        }
        get(){
            return NickManager.getNick(this)
        }
    var lives: Int
        //Forced to go through addLife()/removeLife()
        set(value){
            if(lives > 3)
                throw LifeException("You cannot have more than 3 lives")
            if(lives < -1)
                throw LifeException("You cannot have less than 0 lives")

            PlayersFile().setLives(uuid, value)
            logger().info("Player $name now has $value lives")
            update()
        }
        get(){
            val lives = PlayersFile().getLives(uuid)
            if( lives < -1 || lives > 7 ){
                PlayersFile().setLives(uuid, 7)
                return 7
            } else
                return lives
        }
    var isGhoul: Boolean
        set(ghoul){
            PlayersFile().setIsGhoul(uuid, ghoul)
            isOldGhoul = true
            setHealth(!isGhoul)
        }
        get(){
            return PlayersFile().getIsGhoul(uuid)
        }
    var isOldGhoul: Boolean
        set(ghoul){
            PlayersFile().setIsOldGhoul(uuid, ghoul)
        }
        get(){
            return PlayersFile().getIsOldGhoul(uuid)
        }
    var isShadow: Boolean
        set(ghoul){
            PlayersFile().setIsShadow(uuid, ghoul)
            isOldShadow = true
        }
        get(){
            return PlayersFile().getIsOldShadow(uuid)
        }
    var isOldShadow: Boolean
        set(ghoul){
            PlayersFile().setIsOldShadow(uuid, ghoul)
        }
        get(){
            return PlayersFile().getIsOldShadow(uuid)
        }



    private fun addRemoveLife(isAdd: Boolean){
        if(isAdd && lives == 7)
            throw LifeException("You cannot have more than 7 lives")
        if(!isAdd && lives == -1)
            throw LifeException("You cannot have less than 0 lives")

        if(isGhoul && !isAdd && lives==1) // First time losing red life
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
        val onlinePlayer = player.offlinePlayer.player!!
        onlinePlayer.playEffect(EntityEffect.TOTEM_RESURRECT)
        onlinePlayer.sendTitlePart(TitlePart.TITLE, Component.text("You gave a life to"))
        onlinePlayer.sendTitlePart(TitlePart.SUBTITLE, target.nick!!)

        if(target.offlinePlayer.isOnline){
            val onlineTarget = target.offlinePlayer.player!!
            onlineTarget.sendTitlePart(TitlePart.TITLE, Component.text("You have been given a life by"))
            onlineTarget.sendTitlePart(TitlePart.SUBTITLE, player.nick!!)
        }
    }

    fun setHealth(isFull: Boolean){
        val onlinePlayer = LifeManager.getOnlinePlayer(offlinePlayer) ?: return
        onlinePlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue =
            if(isFull) 20.0 else 10.0
    }



    // -------- PERMISSIONS --------
    //** Null if player not online */
    fun checkAllowedUse() {
        if(!(allowedUse() ?: false))
            throw PermissionException()
    }
    fun allowedUse(): Boolean?{
        return allowed("thirdlife.use")
    }
    //** Null if player not online */
    fun allowedAdmin(): Boolean?{
        return allowed("thirdlife.admin")
    }
    fun allowed(permission: String): Boolean?{
        val onlinePlayer = LifeManager.getOnlinePlayer(offlinePlayer) ?: throw Exception("Requested player is not online")
        return onlinePlayer.hasPermission(permission)
    }




    constructor(uuid: UUID){
        this.uuid = uuid
        checkAllowedUse()
        save()
    }
    constructor(name: String){
        val newUUID = LifeManager.playerNameToUUID(name)
        if(newUUID==null) throw CacheException("Player $name not found")
        this.uuid = newUUID
        checkAllowedUse()
        save()
    }
    constructor(player: Player){
        this.uuid = player.uniqueId
        checkAllowedUse()
        save()
    }

    fun update(){
        val onlinePlayer = LifeManager.getOnlinePlayer(offlinePlayer) ?: return
        if(!allowedUse()!!) return

        // Check if dead - simulate death without moving player
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

        // Re-update nick
        onlinePlayer.displayName(nick)

        // Check if ghoul
        if(lives==0) {
            this.isGhoul = true
        }

        // Send notifications
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



    fun save() {
        //TODO: save all details

    }

    companion object {
        fun init() {
            Bukkit.getOnlinePlayers().forEach {
                LifePlayer(it)
            }
        }
    }


}