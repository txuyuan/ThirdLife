package plugin.ThirdLife.commands

import org.bukkit.Bukkit
import org.bukkit.EntityEffect
import org.bukkit.Particle
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import plugin.ThirdLife.managers.LifeUpdate

class GiveLifeExec : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage(giveLife(sender, args))
        return true
    }

    fun getAllowed(player: Player): Boolean{
        return player.hasPermission("thirdlife.bypass")
    }

    private fun giveLife(sender: CommandSender, args: Array<out String>): String{
        if(!(sender is Player))
            return "§c(Error)§f You must be a player to give a life"
        val player = sender as Player
        if(getAllowed(player))
            return "§c(Error)§f You are exempt from the life system"

        if (args.size < 1)
            return "§c(Error)§f Target player required"


        val targetName = args.get(0)
        var target = Bukkit.getPlayer(targetName)

        if(target==null || !target.hasPlayedBefore()){
            val offlineTarget = Bukkit.getOfflinePlayer(targetName)
            if(offlineTarget==null || !offlineTarget.hasPlayedBefore()) return "§c(Error)§f Player $targetName has not played before"
        }
        target!!

        if(player.name==target.name)
            return "§c(Error)§f You cannot give a life to yourself"

        val lives = LifeUpdate.getLives(player)
        val targetLives = LifeUpdate.getLives(target)
        if(targetLives < 0) //Can to ghoul
            return "§c(Error)§f You cannot give lives to a dead person"
        if(targetLives < 2)
            return "§c(Error)§f You must have at least 2 lives to give one"

        LifeUpdate.addLife(target, true)
        LifeUpdate.removeLife(player)

        sendNotifs(player, target)
        return "§b(Status)§f You have given ${target.displayName} a life"
    }

    private fun sendNotifs(giver: Player, receiver: Player){
        giver.playEffect(EntityEffect.TOTEM_RESURRECT)

        receiver.sendMessage("§b(Status)§f ${giver.displayName} has given you a life")
        receiver.sendTitle("§b§lYou have been given a life", "§bBy ${giver.displayName}", 10, 30, 10)
    }


    fun helpMsg(): String{
        val msg = """
§e(Help)§f §b§lThirdLife v${Bukkit.getPluginManager().getPlugin("ThirdLife")!!.description.version}§f
> §egivelife <target>§f    """.trimIndent()
        return msg
    }




}