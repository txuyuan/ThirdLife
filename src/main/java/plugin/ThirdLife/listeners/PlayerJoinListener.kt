package plugin.ThirdLife.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import plugin.ThirdLife.managers.LifeUpdate

class PlayerJoinListener: Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        LifeUpdate.loadPlayer(event.player)
    }

}