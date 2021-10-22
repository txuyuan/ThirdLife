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

import java.util.List;
import java.util.stream.Collectors;

public class TLExec implements CommandExecutor {

    private static void reset(CommandSender sender, FileConfiguration fileC) {
        fileC.getKeys(false).forEach(id -> fileC.set(id, 3));
        Data.saveLivesData(fileC, sender);
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage("§b(Status)§f All lives have been reset to 3");
            LifeUpdate.updateColour(player, 3);
            GhoulManager.setHasBeenGhoul(player, false);
        });
        Main.logInfo("§b(Status)§f All lives have been reset");
    }

    private static String add(CommandSender sender, FileConfiguration fileC, String[] args, boolean isAdd) {
        if (args.length == 1) {
            if (!(sender instanceof Player))
                return "§c(Error)§f You must be a player to alter your own lives";
            return addPlayer(sender, (Player) sender, fileC, isAdd);
        }

        List<String> playerNames = Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());
        if (playerNames.contains(args[1]))
            return addPlayer(sender, Bukkit.getPlayer(args[1]), fileC, isAdd);

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target == null || !target.hasPlayedBefore())
            return "§c(Error)§f " + args[1] + " has not joined the server before";

        String uuid = target.getUniqueId().toString();
        if (isAdd && fileC.getInt(uuid) > 2) {
            fileC.set(uuid, 3);
            Data.saveLivesData(fileC, sender);
            return "§b(Status)§f " + target.getName() + " already has 3 or more lives";
        }
        if (!isAdd && fileC.getInt(uuid) < 0) {
            fileC.set(uuid, 0);
            Data.saveLivesData(fileC, sender);
            return "§b(Status)§f " + target.getName() + " is already dead";
        }

        int lives = fileC.getInt(uuid) + (isAdd ? 1 : -1);
        fileC.set(uuid, lives);
        Data.saveLivesData(fileC, sender);

        return "§b(Status)§f " + args[1] + " now has " + lives + " lives";
    }

    private static String addPlayer(CommandSender sender, Player player, FileConfiguration fileC, boolean isAdd) {
        String uuid = player.getUniqueId().toString();
        if (player.hasPermission("thirdlife.bypass"))
            return "§c(Error)§f They have the bypass node";

        if (isAdd && fileC.getInt(uuid) > 2) {
            fileC.set(uuid, 3);
            Data.saveLivesData(fileC, sender);
            return "§b(Status)§f " + player.getName() + " already has 3 or more lives";
        }
        if (!isAdd && fileC.getInt(uuid) < 0) {
            fileC.set(uuid, 0);
            Data.saveLivesData(fileC, sender);
            return "§b(Status)§f " + player.getName() + " is already dead";
        }

        int lives = fileC.getInt(uuid) + (isAdd ? 1 : -1);
        fileC.set(uuid, lives);
        Data.saveLivesData(fileC, sender);
        LifeUpdate.updateColour(player, lives);
        return "§b(Status)§f They now have " + lives + " lives";
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

        FileConfiguration data = Data.getLivesData();
        switch (args[0]) {
            case "reset":
                reset(sender, data);
                break;
            case "remove":
                sender.sendMessage(add(sender, data, args, false));
                break;
            case "add":
                sender.sendMessage(add(sender, data, args, true));
                break;
            case "newsession":
                sender.sendMessage(GhoulManager.newSession(sender));
            default:
                sender.sendMessage("§c(Error)§f " + args[0] + " is an unrecognised argument");
                break;
        }
        return true;
    }

}
