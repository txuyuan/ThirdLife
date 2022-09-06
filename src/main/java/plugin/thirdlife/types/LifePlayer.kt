package plugin.thirdlife.types

import net.kyori.adventure.text.Component
import net.kyori.adventure.title.TitlePart
import org.bukkit.Bukkit
import org.bukkit.EntityEffect
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import plugin.thirdlife.handlers.LifeManager
import plugin.thirdlife.handlers.NickManager
import plugin.thirdlife.logger
import plugin.thirdlife.scoreboards.ScoreboardManager
import java.util.*

class LifePlayer{
    val offlinePlayer: OfflinePlayer
        get() {
            return Bukkit.getOfflinePlayer(uuid)
        }
    val onlinePlayer: Player?
        get() {
            return LifeManager.getOnlinePlayer(uuid)
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
        set(value){
            val maxLives = LifeManager.getMaxLives()
            if(lives > maxLives)
                throw LifeException("You cannot have more than $maxLives lives")
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
    val isGhoul: Boolean
        get(){
            return (lives==0)
        }
    var isOldGhoul: Boolean
        set(oldGhoul){
            PlayersFile().setIsOldGhoul(uuid, oldGhoul)
        }
        get(){
            return PlayersFile().getIsOldGhoul(uuid)
        }
    var isShadow: Boolean
        set(shadow){
            PlayersFile().setIsShadow(uuid, shadow)
            update()
        }
        get(){
            return PlayersFile().getIsShadow(uuid)
        }
    var isOldShadow: Boolean
        set(oldShadow){
            PlayersFile().setIsOldShadow(uuid, oldShadow)
        }
        get(){
            return PlayersFile().getIsOldShadow(uuid)
        }

    private fun addRemoveLife(isAdd: Boolean){
        val maxLives = LifeManager.getMaxLives()
        if(isAdd && lives == maxLives)
            throw LifeException("You cannot have more than $maxLives lives")
        if(!isAdd && lives == -1)
            throw LifeException("You cannot have less than 0 lives")

        if(isOldGhoul && !isAdd && lives==1) // Lose red life when ghoul
            lives -= 2
        else
            lives += (if(isAdd) 1 else -1)

        // Drop items if die
        if (lives==-1 && onlinePlayer!=null) {
            val player = onlinePlayer!!

            val loc = player.location.clone()
            val inv = player.inventory
            player.inventory.clear()
            for (item in inv) {
                if (item!=null) {
                    loc.world.dropItemNaturally(loc, item.clone())
                }
            }
        }
    }
    fun addLife(){ addRemoveLife(true) }
    fun removeLife(){ addRemoveLife(false) }

    fun giveLife(target: LifePlayer){
        if(lives <=1) //Cannot sacrifice to dead/ghoul
            throw LifeException("You cannot give away your last life")
        val maxLives = LifeManager.getMaxLives()
        if(target.lives == maxLives)
            throw LifeException("Target already has $maxLives lives")
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
        if(allowedUse() == false) //Avoid type assertion
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
        val onlinePlayer = LifeManager.getOnlinePlayer(offlinePlayer)
        if (onlinePlayer==null) {
            //logger().severe("Player $name not online for permission check")
            // Just operate on trust :)
            return null
        }
        return onlinePlayer.hasPermission(permission)
    }




    constructor(uuid: UUID){
        this.uuid = uuid
        checkAllowedUse()
        init()
    }
    constructor(name: String){
        val newUUID = LifeManager.playerNameToUUID(name)
        if(newUUID==null) throw CacheException("Player $name not found")
        this.uuid = newUUID
        checkAllowedUse()
        init()
    }
    constructor(player: Player){
        this.uuid = player.uniqueId
        checkAllowedUse()
        init()
    }

    private fun init() {
        // Initialise default values
        if (PlayersFile().getName(uuid)==null) {
            PlayersFile().setName(uuid, onlinePlayer!!.name)
        }
        // Not initialising nick
        if (lives==null) {
            lives = LifeManager.getMaxLives()
        }
        if (isOldGhoul==null)
            isOldGhoul = false
        if (isShadow==null)
            isShadow = false
        if (isOldShadow==null)
            isOldShadow = false
    }

    fun update() {
        update(true)
    }
    fun update(sendMessage: Boolean){
        val onlinePlayer = LifeManager.getOnlinePlayer(offlinePlayer) ?: return
        if(!allowedUse()!!) return


        // Re-update nick
        onlinePlayer.displayName(nick)

        // Check if ghoul
        setHealth(!this.isGhoul)
        if (this.isGhoul) {
            PlayersFile().setIsOldGhoul(uuid, true)
        }

        if (allowedAdmin() != true) {
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
        }

        ScoreboardManager.updatePlayerBoards()
    }


    companion object {
        fun init() {
            Bukkit.getOnlinePlayers().forEach {
                LifePlayer(it)
            }
        }
    }


}
