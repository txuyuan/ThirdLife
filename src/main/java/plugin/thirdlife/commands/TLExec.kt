package plugin.thirdlife.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.io.BukkitObjectInputStream
import plugin.thirdlife.Main
import plugin.thirdlife.handlers.*
import plugin.thirdlife.logger
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

        return when (args.get(0).toLowerCase()){
            "get" -> getLives(sender, args)
            "add" -> addRemoveLives(sender, args, true)
            "remove" -> addRemoveLives(sender, args, false)
            "give" -> giveLife(sender, args)
            "reset" -> reset(sender)
            "endsession" -> endSession(sender, args)
            "newsession" -> newSession(sender)
            "nick" -> nick(sender, args)
            else -> {
                object: BukkitRunnable() {
                    override fun run() {
                        sender.sendHelp(helpMsg())
                    }
                }.runTaskLater(Main.getInstance(), 1)
                throw IllegalArgumentException("Unrecognised argument ${args[0]}")
            }
        }
    }


    private fun getLives(sender: CommandSender, args: Array<out String>): Component{
        val target: LifePlayer
        if(args.size < 2){
            if(sender is Player)
                target = LifePlayer(sender)
            else throw LifeException("Specify a player to get info for")
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
        val shadowStatus = Component.text(" - ")
            .append(Component.text(
                if (target.isShadow) "shadow"
                else "not shadow"
            )).color(
                if (target.isShadow) NamedTextColor.RED
                else NamedTextColor.WHITE
            )

        val msg = Component.text("> ")
            .append(target.nick!!)
            .append(componentWhite())
            .append(shadowStatus)

        return msg
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

    private fun endSession(sender: CommandSender, args: Array<out String>): Component{
        checkAdminPermission(sender)

        if (args.size < 2) {
            throw IllegalArgumentException("End session operation is required: [now | countdown | cancel]")
        }

        // Cancel existing countdown
        if (args[1].toLowerCase() == "cancel") {
            if (!countdownOngiong) {
                return Component.text("No countdown ongoing")
            } else {
                countdownOngiong = false
                countdownTasks.forEach {
                    it.cancel()
                }
                val broadcastMsg = Component.text("Session end countdown cancelled").color(NamedTextColor.RED)
                Bukkit.broadcast(broadcastMsg)
                return Component.text("Cancelled countdown")
            }
        }

        if (countdownOngiong)
            return Component.text("Session end countdown is already ongoing")
        countdownOngiong = true
        // People who become ghouls after countdown start stay alive
        val ghouls = GhoulManager.getGhouls()

        // Immediate session end
        if (args[1].toLowerCase() == "now") {
            initEndSession(ghouls)
            return Component.text("Session ended")
        }

        if (args[1].toLowerCase() == "countdown") {
            // Countdown to end of session
            val countdownMin = 10
            val scheduledRunnables = mutableListOf<BukkitTask>()
            for (i in countdownMin downTo 0) {
                val task = object : BukkitRunnable() {
                    override fun run() {
                        if (i == 0) {
                            initEndSession(ghouls)
                        } else {
                            // Broadcast countdown
                            val color = if (countdownMin > 5) NamedTextColor.RED else NamedTextColor.YELLOW
                            val msgContent = "Session ending in $i minutes"
                            val message = Component.text(msgContent).color(color)
                            val title = Component.text(msgContent).color(color)
                                .decorate(TextDecoration.BOLD)
                            Bukkit.broadcast(message)
                            for (player in Bukkit.getOnlinePlayers()) {
                                player.sendActionBar(title)
                            }
                            logger().info(msgContent)
                        }
                    }
                }.runTaskLater(Main.getInstance(), (10 - i.toLong()) * 1200)
                scheduledRunnables.add(task)
            }
            countdownTasks = scheduledRunnables
            return Component.text("Session ending in $countdownMin minutes")
        }

        throw IllegalArgumentException("Unrecognised argument ${args[1]}")
    }
    // The actual session end procedure
    private fun initEndSession(ghouls: List<LifePlayer>) {
        // Broadcast start
        val color = NamedTextColor.RED
        val message = Component.text("Session has ended!").color(color)
        val title = message.decorate(TextDecoration.BOLD)
        Bukkit.broadcast(message)
        for (player in Bukkit.getOnlinePlayers()) {
            player.sendActionBar(title)
        }
        // End session
        GhoulManager.endSession(ghouls)
        ShadowManager.endSession()
        countdownOngiong = false
    }

    private fun newSession(sender: CommandSender): Component {
        checkAdminPermission(sender)
        // Broadcast start
        val messsage = Component.text("Session has started!").color(NamedTextColor.GREEN)
        val title = messsage.decorate(TextDecoration.BOLD)
        Bukkit.broadcast(messsage)
        for (player in Bukkit.getOnlinePlayers()) {
            player.sendActionBar(title)
        }

        ShadowManager.newSession()

        return Component.text("Session started")
    }


    private fun nick(sender: CommandSender, args: Array<out String>): Component{
        checkAdminPermission(sender)
        val target: LifePlayer
        var nick: String
        if (args.size < 2)
            throw Exception("Nick or target player required")
        if(args.size < 3){
            if(sender !is Player) throw Exception("Target player required; you are not a valid target")
            target = LifePlayer(sender)
            nick = args.get(1)
        }else{
            target = LifePlayer(args.get(1))
            nick = args.get(2)
        }

        // For reset
        if (nick == "reset") {
            target.nick = null
            return Component.text("${target.name}'s nick reset")
        } else {
            val nickFormatted = LegacyComponentSerializer.legacyAmpersand().deserialize(nick)
            target.nick = nickFormatted
            return Component.text("${target.name}'s nick set: ").append(nickFormatted)
        }
    }






    // -------------- HELPERS ---------------

    fun helpMsg(): Component{
        val msg = """
            §b§lThirdLife v${Bukkit.getPluginManager().getPlugin("ThirdLife")!!.description.version}§f
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

    companion object {
        var countdownOngiong = false
        var countdownTasks = mutableListOf<BukkitTask>()
    }

}
