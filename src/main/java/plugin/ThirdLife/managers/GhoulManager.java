package plugin.ThirdLife.managers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import plugin.ThirdLife.Main;
import plugin.ThirdLife.data.Data;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GhoulManager {

    public static void newSession(CommandSender sender) {
        FileConfiguration fileC = Data.getLivesData();
        List<String> ghouls = fileC.getKeys(false).stream().filter(id -> fileC.getInt(id) == 0).toList();
        ghouls.forEach(id -> fileC.set(id, -1));
        Data.saveLivesData(fileC, sender);

        List<String> onlinePlayerIds = Bukkit.getOnlinePlayers().stream().map(player -> player.getUniqueId().toString()).toList();
        List<UUID> ghoulIds = onlinePlayerIds.stream().filter(id -> ghouls.contains(id)).map(id -> UUID.fromString(id)).toList();
        ghoulIds.forEach(id -> {
            LifeUpdate.updateColour(Bukkit.getPlayer(id), -1);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(id);
            if(offlinePlayer!=null && offlinePlayer.isOnline()){
                Player player = (Player)offlinePlayer;
                player.setHealth(0);
            }
        });
    }

    public static void checkGhoulKill(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player) || event.getEntity().getKiller() == null) return;

        Player player = (Player)event.getEntity();
        Player killer = event.getEntity().getKiller();
        String killerUuid = killer.getUniqueId().toString();

        FileConfiguration fileC = Data.getLivesData();
        if (fileC.getInt(killerUuid) != 0) return;
        if (fileC.getInt(player.getUniqueId().toString()) == 0) return;

        LifeUpdate.addLife(killer, true);
    }

    public static void resetGhouls() {
        FileConfiguration fileC = getGhoulData();

    }


    public static void setHasBeenGhoul(OfflinePlayer player, boolean isGhoul) {
        FileConfiguration fileC = getGhoulData();
        fileC.set(player.getUniqueId().toString(), isGhoul);
        saveGhoulData(fileC);
        Main.logTest(player + " ghoul " + isGhoul);
    }

    public static boolean getHasBeenGhoul(OfflinePlayer player) {
        FileConfiguration fileC = getGhoulData();

        String uuid = player.getUniqueId().toString();
        return fileC.getBoolean(uuid);
    }

    private static FileConfiguration getGhoulData() {
        return YamlConfiguration.loadConfiguration(new File(Bukkit.getPluginManager().getPlugin("ThirdLife").getDataFolder(), "ghouls.yml"));
    }

    private static void saveGhoulData(FileConfiguration fileC) {
        File file = new File(Bukkit.getPluginManager().getPlugin("ThirdLife").getDataFolder(), "ghouls.yml");
        try {
            fileC.save(file);
        } catch (IOException e) {
            Main.logDiskError(e);
        }
    }

}
