package plugin.thirdlife.commands.completers

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import plugin.thirdlife.getOnlinePlayerNames
import plugin.thirdlife.types.LifePlayer

class TLComplete : TabCompleter {

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String> ): MutableList<String>? {

        if(sender is Player && LifePlayer(sender).allowedAdmin()!=true)
            return when(args.size){
                1 -> mutableListOf("get", "give")
                2 -> when(args.get(0)){
                    "get", "give" -> getOnlinePlayerNames()
                    else -> mutableListOf()
                }
                else -> mutableListOf()
            }

        return when(args.size){
            1 -> mutableListOf("add", "remove", "get", "give", "reset", "newsession", "nick")
            2 -> when(args.get(0)){
                "add","remove","nick" -> getOnlinePlayerNames()
                "get" -> {
                    getOnlinePlayerNames()
                    val rList = getOnlinePlayerNames()
                    rList.add("all")
                    rList
                }
                else -> mutableListOf()
            }
            else -> mutableListOf()
        }
    }

}

