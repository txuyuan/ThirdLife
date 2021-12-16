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
import plugin.ThirdLife.managers.BlueManager;
import plugin.ThirdLife.managers.GhoulManager;
import plugin.ThirdLife.managers.LifeUpdate;

import java.util.List;
import java.util.stream.Collectors;

public class TLExec implements CommandExecutor {

    private static FileConfiguration config = Data.getLivesData();
    
    public static void load(){
        config = Data.getLivesData();
    }



    public static String reset(CommandSender sender) {
        config = LifeUpdate.data;
        config.getKeys(false).forEach(id -> config.set(id, 7));
        Data.saveLivesData(config, sender);
        LifeUpdate.load();
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


    private static String get(CommandSender sender, String[] args){
        OfflinePlayer target = null;
        if (args.length < 2) {
            if(sender instanceof Player) target = (Player)sender;
            else return "§c(Erorr)§f Player name required";
        }
        else{
            target = Bukkit.getOfflinePlayer(args[1]);
            if (target == null || !target.hasPlayedBefore())
                return "§c(Error)§f " + args[1] + " has not joined the server before";
        }


        int lives = LifeUpdate.getLives(target);
        Main.logInfo("Lives: " + lives);

        String msg = "§b(Status)§f " + target.getName();
        return switch(lives){
            case -1 -> msg + " is dead";
            case 0 -> msg + " is a ghoul";
            default -> msg + " has " + lives + " " +  (lives!=1 ? "lives" : "life");
        };
    }

    private static String newSession(CommandSender sender){
        BlueManager.INSTANCE.newSession();
        GhoulManager.newSession(sender);
        return "§b(Status)§f New session started";
    }

    private static String nick(CommandSender sender, String[] args){
        if(!(sender instanceof Player))
            return "§c(Error)§f You must be a player to give yourself a nick";
        Player player = (Player)sender;

        if(args.length < 2)
            return "§c(Error)§f Nickname required";
        String name = args[1].replace("&", "§");

        player.setDisplayName(name);
        return "§b(Status)§f Nick set: " + name;
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
            case "get" -> get(sender, args);
            case "add" -> add(sender, args, true);
            case "remove" -> add(sender, args, false);
            case "reset" -> reset(sender);
            case "newsession" -> newSession(sender);
            case "nick" -> nick(sender, args);
            default -> "§c(Error)§f Unrecognised argument " + args[0] +
                    "\n" + helpMsg();
        });
        return true;
    }

    public static String helpMsg(){
        String msg = "§e(Help)§f §b§lThirdLife v" + Bukkit.getPluginManager().getPlugin("ThirdLife").getDescription().getVersion() +
                "\n> §eget <target>§f" +
                "\n> §eadd <target>§f" +
                "\n> §eremove <target>§f" +
                "\n> §ereset" +
                "\n> §enewsession";
        return msg;
    }

}
