package plugin.ThirdLife.Commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import plugin.ThirdLife.Data.Config;
import plugin.ThirdLife.Listeners.OnPlayerJoin;

public class TLExec implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length <1){
            sender.sendMessage("§c(Error)§f No arguments specified");
            return true;
        }

        Boolean hasPerm = false;
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(player.hasPermission("thirdlife.admin"))
                hasPerm = true;
        }else hasPerm = true;
        if(!hasPerm) {
            sender.sendMessage("§c(Error)§f You do not have permission to administrate ThirdLife");
            return true;
        }

        FileConfiguration data = Config.getData();
        switch (args[0]){
            case "reset":
                for(String id: data.getKeys(true))
                    data.set(id, 3);
                Config.saveDataS(data, sender);
                for(Player player: Bukkit.getOnlinePlayers()){
                    player.setDisplayName("§a" + player.getName());
                    OnPlayerJoin.updateColour(3, player);
                    player.sendMessage("§b(Status)§f All lives have been reset");
                    if(!player.hasPermission("thirdlife.bypass"))
                        player.setGameMode(GameMode.SURVIVAL);
                }
                System.out.println("§b(ThirdLife | Status)§f All lives have been reset");
                break;

            case "remove":
                if(args.length == 1){
                    if (!(sender instanceof Player)){
                        sender.sendMessage("§c(Error)§f You must be a player to remove your own lives");
                        return true;
                    }
                    String uuid = ((Player)sender).getUniqueId().toString();
                    Player player = (Player)sender;
                    if(data.getInt(uuid) == 0){
                        player.sendMessage("§c(Error)§f You have no lives left to remove");
                        return true;
                    }
                    data.set(uuid, data.getInt(uuid)-1); Config.saveDataS(data, sender);
                    if(data.getInt(uuid) == 0)
                        player.sendMessage("§b(Status)§f You have removed your last life");
                    else
                        player.sendMessage("§b(Status)§f You have removed one of your lives, you now have " + data.getInt(uuid) + " lives");
                    OnPlayerJoin.updateColour(data.getInt(uuid), player);
                    return true;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if(target==null || !target.hasPlayedBefore()){
                    sender.sendMessage("§c(Error)§f Player " + args[1] + " has not joined the server before");
                    return true;
                }
                String tUuid = target.getUniqueId().toString();
                if(!data.getKeys(true).contains(tUuid)){
                    data.set(tUuid, 2); Config.saveDataS(data, sender);
                    sender.sendMessage("§b(Status)§f You have removed one of Player " + args[1] + "'s lives, he/she now has 2 lives");
                    return true;
                }
                data.set(tUuid, data.getInt(tUuid)-1); Config.saveDataS(data, sender);
                if(data.getInt(tUuid) == 0)
                    sender.sendMessage("§b(Status)§f You have removed Player " + args[1] + "'s last life");
                else
                    sender.sendMessage("§b(Status)§f You have removed one of Player " + args[1] + "'s lives, he/she now has " + data.getInt(tUuid) + " lives");
                if(Bukkit.getPlayer(args[1])!=null)
                    OnPlayerJoin.updateColour(data.getInt(tUuid), Bukkit.getPlayer(args[1]));
                return true;

            case "add":
                if(args.length == 1){
                    if (!(sender instanceof Player)){
                        sender.sendMessage("§c(Error)§f You must be a player to give a life to yourself");
                        return true;
                    }
                    String uuid = ((Player)sender).getUniqueId().toString();
                    Player player = (Player)sender;
                    if(!data.getKeys(true).contains(uuid) || data.getInt(uuid)==3){
                        player.sendMessage("§c(Error)§f You already have 3 lives");
                        return true;
                    }
                    data.set(uuid, data.getInt(uuid)+1); Config.saveDataS(data, sender);
                    player.sendMessage("§b(Status)§f You have given yourself a life, you now have " + data.getInt(uuid) + " lives");
                    OnPlayerJoin.updateColour(data.getInt(uuid), player);
                    return true;
                }

                OfflinePlayer targetA = Bukkit.getOfflinePlayer(args[1]);
                if(targetA==null || !targetA.hasPlayedBefore()){
                    sender.sendMessage("§c(Error)§f Player " + args[1] + " has not joined the server before");
                    return true;
                }
                String tUuidA = targetA.getUniqueId().toString();
                data.set(tUuidA, data.getInt(tUuidA)+1); Config.saveDataS(data, sender);
                sender.sendMessage("§b(Status)§f You have given Player " + args[1] + " 1 life, he/she now has " + data.getInt(tUuidA) + " lives");
                if(Bukkit.getPlayer(args[1])!=null)
                    OnPlayerJoin.updateColour(data.getInt(tUuidA), Bukkit.getPlayer(args[1]));
                return true;
            default:
                sender.sendMessage("§c(Error)§f " + args[0] + " is an unrecognised argument");
                return true;
        }
        return true;
    }

}
