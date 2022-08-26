package plugin.thirdlife.handlers

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player
import plugin.thirdlife.types.LifePlayer
import plugin.thirdlife.types.PlayersFile

object NickManager {

    fun getNick(player: LifePlayer): Component{
        val rawNick = PlayersFile().getNick(player.uuid)
        if (rawNick==null) {
            return Component.text(player.name)
                .color(LifeManager.getLifeColours(player.lives))
                .append(componentWhite())
        }
        return LegacyComponentSerializer.legacySection().deserialize(rawNick).append(Component.text().color(NamedTextColor.WHITE))
    }

    fun setNick(player: LifePlayer, nick: Component?){
        val rawNick =
            if(nick==null) null
            else LegacyComponentSerializer.legacySection().serialize(nick)
        PlayersFile().setNick(player.uuid, rawNick)
        player.offlinePlayer.player?.displayName(getNick(player))
    }
}