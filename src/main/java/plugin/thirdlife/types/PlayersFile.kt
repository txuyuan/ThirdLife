package plugin.thirdlife.types

import plugin.thirdlife.handlers.LifeManager
import java.util.*

class PlayersFile: FileConfig("players.yml") {

    val defaultLives = LifeManager.getMaxLives()
    fun setName(uuid: UUID, name: String) {
        this.set("$uuid.name", name)
    }
    fun getName(uuid: UUID): String? {
        return this.config.getString("$uuid.name")
    }

    fun setNick(uuid: UUID, nick: String) {
        this.set("$uuid.nick", nick)
    }
    fun getNick(uuid: UUID): String? {
        return this.config.getString("$uuid.nick")
    }

    fun setLives(uuid: UUID, lives: Int) {
        this.set("$uuid.lives", lives)
    }
    fun getLives(uuid: UUID): Int {
        val key = "$uuid.lives"
        if(!this.config.getKeys(true).contains(key))
            this.config.set(key, defaultLives)
        return this.config.getInt("$uuid.lives")
    }

    fun setIsGhoul(uuid: UUID, isGhoul: Boolean) {
        this.set("$uuid.isGhoul", isGhoul)
    }
    fun getIsGhoul(uuid: UUID): Boolean {
        // Default null value is correct
        return this.config.getBoolean("$uuid.isGhoul")
    }

    fun setIsOldGhoul(uuid: UUID, isOldGhoul: Boolean) {
        this.set("$uuid.isOldGhoul", isOldGhoul)
    }
    fun getIsOldGhoul(uuid: UUID): Boolean {
        // Default null value is correct
        return this.config.getBoolean("$uuid.isOldGhoul")
    }

    fun setIsShadow(uuid: UUID, isShadow: Boolean) {
        this.set("$uuid.isShadow", isShadow)
    }
    fun getIsShadow(uuid: UUID): Boolean {
        // Default null value is correct
        return this.config.getBoolean("$uuid.isShadow")
    }

    fun setIsOldShadow(uuid: UUID, isOldShadow: Boolean) {
        this.set("$uuid.isOldShadow", isOldShadow)
    }
    fun getIsOldShadow(uuid: UUID): Boolean {
        // Default null value is correct
        return this.config.getBoolean("$uuid.isOldShadow")
    }

}