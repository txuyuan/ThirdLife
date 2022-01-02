package plugin.thirdlife.commands.exec

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import plugin.thirdlife.handlers.BlueManager
import plugin.thirdlife.handlers.GhoulManager
import plugin.thirdlife.handlers.LifeManager
import plugin.thirdlife.types.*

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
            "newsession" -> newSession(sender)
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
        LifeManager.reset()
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

    private fun newSession(sender: CommandSender): Component{
        checkAdminPermission(sender)
        BlueManager.newSession()
        GhoulManager.newSession()
        return Component.text("New session started")
    }

    private fun nick(sender: CommandSender, args: Array<out String>): Component{
        checkAdminPermission(sender)
        var target: LifePlayer
        var nick: String
        if(args.size < 3){
            if(sender !is Player) throw IllegalArgumentException("Target player required")
            target = LifePlayer(sender)
            nick = args.get(1)
        }else{
            target = LifePlayer(args.get(1))
            nick = args.get(2)
        }
        val nickFormatted = LegacyComponentSerializer.legacyAmpersand().deserialize(nick)
        target.nick = nickFormatted
        return Component.text("${target.name} nick set ").append(nickFormatted)
    }






    // -------------- HELPERS ---------------

    fun helpMsg(): Component{
        val msg = """
            §e(Help)§f §b§lThirdLife v${Bukkit.getPluginManager().getPlugin("ThirdLife")!!.description.version}
            > §eget <target>§f
            > §eadd <target>§f
            > §eremove <target>§f
            > §ereset
            > §enewsession
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