package plugin.ThirdLife.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TLComplete implements TabCompleter {

    public List<String> onTabComplete(final CommandSender s, final Command c, final String al, final String[] a) {
        if ((c.getName().equals("thirdlife") || c.getName().equalsIgnoreCase("tl")) && a.length <= 1 && s instanceof Player) {
            List<String> defList = new ArrayList<>();
            List<String> rList = new ArrayList<>();
            if(!s.hasPermission("thirdlife.admin")) {
                rList.add("§cYou are not allowed to use this command");
                return rList;
            }
            defList.add("reset");
            defList.add("remove");
            defList.add("add");
            for (final String str : defList)
                if (str.indexOf(a[0]) == 0)
                    rList.add(str);
            return rList;
        }
        return null;
    }

}
