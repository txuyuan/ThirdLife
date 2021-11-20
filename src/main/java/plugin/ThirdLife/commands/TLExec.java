package plugin.ThirdLife.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import plugin.ThirdLife.Main;
import plugin.ThirdLife.data.Data;
import plugin.ThirdLife.managers.GhoulManager;
import plugin.ThirdLife.managers.LifeUpdate;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class TLExec implements CommandExecutor {

    private static FileConfiguration config = Data.getLivesData();
    
    public static void load(){
        config = Data.getLivesData();
    }



    public static String reset(CommandSender sender) {
        config.getKeys(false).forEach(id -> config.set(id, 3));
        Data.saveLivesData(config, sender);
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(!sender.equals(player)) player.sendMessage("§b(Status)§f All lives have been reset");
            LifeUpdate.updateColour(player, 7);
            GhoulManager.setHasBeenGhoul(player, false);
        });
        Main.logInfo("§b(Status)§f All lives have been reset");
        return "§b(Status)§f All lives have been reset";
    }



    private static String add(CommandSender sender, String[] args, boolean isAdd) {
        if (args.length == 1) {
            if (!(sender instanceof Player))
                return "§c(Error)§f You must be a player to alter your own lives";
            return addPlayer(sender, (Player) sender, isAdd);
        }

        List<String> playerNames = Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());
        if (playerNames.contains(args[1]))
            return addPlayer(sender, Bukkit.getPlayer(args[1]), isAdd);

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target == null || !target.hasPlayedBefore())
            return "§c(Error)§f " + args[1] + " has not joined the server before";

        return LifeUpdate.addLife(target, isAdd);
    }



    private static String addPlayer(CommandSender sender, Player player, boolean isAdd) {
        String uuid = player.getUniqueId().toString();
        if (player.hasPermission("thirdlife.bypass"))
            return "§c(Error)§f They have the bypass node";

        return LifeUpdate.addLife(player, isAdd);
    }





    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§c(Error)§f No arguments specified");
            return true;
        }
        if ((sender instanceof Player) && !sender.hasPermission("thirdlife.admin")) {
            sender.sendMessage("§c(Error)§f You do not have permission to do this");
            return true;
        }

        sender.sendMessage( switch(args[0]){
            case "add" -> add(sender, args, true);
            case "remove" -> add(sender, args, false);
            case "reset" -> reset(sender);
            case "newsession" -> GhoulManager.newSession(sender);
            default -> "§c(Error)§f Unrecognised argument " + args[0];
        });
        return true;
    }

}
