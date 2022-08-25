package plugin.thirdlife.commands.exec

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.title.TitlePart
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import plugin.thirdlife.Main
import plugin.thirdlife.handlers.*
import plugin.thirdlife.types.LifeException
import plugin.thirdlife.types.LifePlayer
import plugin.thirdlife.types.PermissionException

class TLExec : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        try{
            sender.sendStatus(exec(sender, args))
        }catch(exception: Exception){
            sender.sendError(exception.message ?: "An internal error occured")
        }
        return true
    }
    private fun exec(sender: CommandSender, args: Array<out String>): Component{
        if(args.size < 1)
            throw IllegalArgumentException("No arguments specified")

        return when (args.get(0)){
            "get" -> getLives(sender, args)
            "add" -> addRemoveLives(sender, args, true)
            "remove" -> addRemoveLives(sender, args, false)
            "give" -> giveLife(sender, args)
            "reset" -> reset(sender)
            "endSession" -> endSession(sender)
            "newSession" -> newSession(sender)
            "nick" -> nick(sender, args)
            else -> Component.text("Unrecognised argument").append(Component.newline())
                .append(helpMsg())
        }
    }


    private fun getLives(sender: CommandSender, args: Array<out String>): Component{
        val target: LifePlayer
        if(args.size < 2){
            if(sender is Player)
                target = LifePlayer(sender)
            else throw LifeException("You must be a player to alter your lives")
        }else{
            checkAdminPermission(sender)

            if(args.get(1).equals("all", true)){ //List all lives
                var msg = Component.text("")
                val players = LifeManager.getAllPlayers().filter { it.allowedUse()!=false }
                if(players.size < 1) return Component.text("No players found")
                players.forEach { msg = msg.append { Component.newline() }.append{ getLivesStatus(it) } }
                return msg
            }

            target = LifePlayer(args.get(1))
        }
        return getLivesStatus(target)
    }
    private fun getLivesStatus(target: LifePlayer): Component{
        return target.nick!!.append(
            Component.text(
                when(target.lives){
                    -1 -> " is dead"
                    0 -> " is a ghoul"
                    else -> " has ${target.lives} lives"
                }
            ).reset()
        )
    }

    private fun addRemoveLives(sender: CommandSender, args: Array<out String>, isAdd: Boolean): Component{
        checkAdminPermission(sender)
        val target: LifePlayer
        if(args.size < 2){
            if(sender is Player)
                target = LifePlayer(sender)
            else throw LifeException("You must be a player to alter your lives")
        }else
            target = LifePlayer(args.get(1))

        if(isAdd) target.addLife()
        else target.removeLife()

        return Component.text("1 life ${if(isAdd) "added to" else "removed from"} ${target.name}")
    }

    private fun reset(sender: CommandSender): Component{
        checkAdminPermission(sender)
        LifeManager.resetAll()
        return Component.text("All lives reset")
    }

    private fun giveLife(sender: CommandSender, args: Array<out String>): Component{
        if(sender !is Player)
            throw LifeException("You must be a player to give lives")
        val player = LifePlayer(sender)
        if(player.allowedUse() == false)
            throw PermissionException()

        if(args.size < 2)
            throw IllegalArgumentException("Target player required")
        val target = LifePlayer(args.get(1))

        if(player.name==target.name)
            throw IllegalArgumentException("You cannot give yourself lives")
        if(target.lives < 0)
            throw IllegalArgumentException("$target is already dead")
        if(player.lives < 1) //Cannot give as ghoul
            throw IllegalArgumentException("You must have at least one life")

        player.giveLife(target)

        return Component.text("You have given a life to ").append(target.nick!!)
    }

    private fun endSession(sender: CommandSender): Component{
        checkAdminPermission(sender)

        // Countdown to end of session
        val countdownMin = 10
        for (i in countdownMin downTo 0) {
            object :  BukkitRunnable() {
                override fun run() {
                    if (countdownMin==0) {
                        // Broadcast start
                        val color = NamedTextColor.RED
                        val message = Component.text("Session has ended!").color(color)
                        val title = Component.text("SESSION END").color(color)
                        Bukkit.broadcast(message)
                        for (player in Bukkit.getOnlinePlayers()) {
                            player.sendTitlePart(TitlePart.TITLE, title)
                        }
                        // End session
                        GhoulManager.endSession()
                        ShadowManager.endSession()
                    } else {
                        // Broadcast countdown
                        val color = if (countdownMin > 5) NamedTextColor.GREEN else NamedTextColor.GOLD
                        val message = Component.text("Session ending in $countdownMin minutes").color(color)
                        val title = Component.text("${countdownMin}min").color(color)
                        Bukkit.broadcast(message)
                        for (player in Bukkit.getOnlinePlayers()) {
                            player.sendTitlePart(TitlePart.TITLE, title)
                        }
                    }
                }
            }.runTaskLater(Main.getInstance(), ((10-i) * 1000) as Long)
        }

        return Component.text("Session ending in $countdownMin minutes")
    }

    private fun newSession(sender: CommandSender): Component {
        // Broadcast start
        val color = NamedTextColor.GREEN
        val message = Component.text("").color(color)
        val title = Component.text("SESSION START").color(color)

        return Component.text("Session started")
    }


    private fun nick(sender: CommandSender, args: Array<out String>): Component{
        checkAdminPermission(sender)
        val target: LifePlayer
        var nick: String
        if(args.size < 3){
            if(sender !is Player) throw IllegalArgumentException("Target player required")
            target = LifePlayer(sender)
            nick = args.get(1)
        }else{
            target = LifePlayer(args.get(1))
            nick = args.get(2)
        }

        // For reset
        if (nick.length == 0)
            nick = target.name

        val nickFormatted = LegacyComponentSerializer.legacyAmpersand().deserialize(nick)
        target.nick = nickFormatted
        return Component.text("${target.name} nick set ").append(nickFormatted)
    }






    // -------------- HELPERS ---------------

    fun helpMsg(): Component{
        val msg = """
            §e(Help)§f §b§lThirdLife v${Bukkit.getPluginManager().getPlugin("ThirdLife")!!.description.version}§f
            > §eget <target>§f
            > §eadd <target>§f
            > §eremove <target>§f
            > §ereset§f
            > §eendSession§f
            """.trimIndent()
        return LegacyComponentSerializer.legacySection().deserialize(msg)
    }

    private fun checkAdminPermission(sender: CommandSender){
        if(sender is Player){
            if(!LifePlayer(sender).allowedAdmin()!!)
                throw PermissionException()
        }
    }

}
