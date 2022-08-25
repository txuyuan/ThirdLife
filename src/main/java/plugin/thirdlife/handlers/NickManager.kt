package plugin.thirdlife.handlers

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
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
        return LegacyComponentSerializer.legacySection().deserialize(rawNick)
    }

    fun setNick(player: LifePlayer, nick: Component?){
        val rawNick =
            if(nick!=null) LegacyComponentSerializer.legacySection().serialize(nick)
            else ""
        PlayersFile().setNick(player.uuid, rawNick)
        player.offlinePlayer.player?.displayName(nick)
    }
}