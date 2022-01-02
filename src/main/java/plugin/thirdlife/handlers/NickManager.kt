package plugin.thirdlife.handlers

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import plugin.thirdlife.types.LifePlayer
import plugin.thirdlife.types.NicksFile
import plugin.thirdlife.types.componentWhite

object NickManager {

    fun getNick(player: LifePlayer): Component{
        val rawNick = NicksFile().config.getString(player.uuid.toString())
            ?: return Component.text(player.name)
                .color(LifeManager.getLifeColours(player.lives))
                .append(componentWhite())
        return LegacyComponentSerializer.legacySection().deserialize(rawNick)
    }

    fun setNick(player: LifePlayer, nick: Component?){
        val rawNick =
            if(nick!=null) LegacyComponentSerializer.legacySection().serialize(nick)
            else null
        NicksFile().set(player.uuid.toString(), rawNick)
    }


}