package plugin.thirdlife.handlers

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import java.util.logging.Logger

fun CommandSender.sendStatus(message: String){
    sendStatus(Component.text(message))
}
fun CommandSender.sendError(message: String){
    sendError(Component.text(message))
}
fun CommandSender.sendHelp(message: String) {
    sendHelp(Component.text(message))
}

fun CommandSender.sendStatus(message: Component){
    sendMessage(
        Component.text("(Status) ").color(NamedTextColor.AQUA)
        .append(message.color(NamedTextColor.WHITE))
    )
}
fun CommandSender.sendError(message: Component){
    sendMessage(
        Component.text("(Error) ").color(NamedTextColor.RED)
        .append(message.color(NamedTextColor.WHITE))
    )
}
fun CommandSender.sendHelp(message: Component){
    sendMessage(
        Component.text("(Help) ").color(NamedTextColor.YELLOW)
        .append(message.color(NamedTextColor.WHITE))
    )
}


fun Logger.info(msg: Component){
    val serMsg = LegacyComponentSerializer.legacySection().serialize(msg)
    info(serMsg)
}
fun componentWhite(): Component {
    return LegacyComponentSerializer.legacyAmpersand().deserialize("§r")
}

fun Component.reset(): Component {
    decoration(TextDecoration.ITALIC, false)
    decoration(TextDecoration.BOLD, false)
    decoration(TextDecoration.OBFUSCATED, false)
    decoration(TextDecoration.STRIKETHROUGH, false)
    decoration(TextDecoration.UNDERLINED, false)
    color(NamedTextColor.WHITE)
    return this
}