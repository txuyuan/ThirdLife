package plugin.ThirdLife.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TLComplete implements TabCompleter {

    public List<String> onTabComplete(final CommandSender s, final Command c, final String al, final String[] a) {
        if ((c.getName().equals("thirdlife") || c.getName().equalsIgnoreCase("tl")) && a.length <= 1 && s instanceof Player) {
            List<String> defList = Arrays.asList("reset", "remove", "add", "get", "newsession");
            List<String> rList = new ArrayList<>();
            if (!s.hasPermission("thirdlife.admin")) {
                rList.add("§cYou do not have permission to do this");
                return rList;
            }

            rList = defList.stream().filter(str -> str.indexOf(a[0]) == 0).collect(Collectors.toList());
            Collections.sort(rList, String.CASE_INSENSITIVE_ORDER);
            return rList;
        }
        return null;
    }

}
