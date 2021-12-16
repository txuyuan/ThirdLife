package plugin.ThirdLife.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GiveLifeComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {

        if(!(sender instanceof Player) || sender.hasPermission("thirdlife.bypass"))
            return Arrays.asList();

        if(args.length == 1)
            return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());

        return Arrays.asList();
    }

}
