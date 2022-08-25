package plugin.thirdlife.scoreboards

import org.bukkit.Bukkit
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Score
import org.bukkit.scoreboard.Scoreboard
import plugin.thirdlife.handlers.LifeManager
import plugin.thirdlife.types.LifePlayer
import java.util.StringJoiner

object ScoreboardManager {

    val divider = getScoreboardDivider()

    fun updatePlayerBoards() {
        val onlinePlayers = LifeManager.getAllPlayers()
            .filter { it.onlinePlayer!=null }
        onlinePlayers.forEach {
            it.onlinePlayer?.scoreboard = getPlayerBoard(it)
        }
    }
    fun updatePlayer(player: LifePlayer){
        player.onlinePlayer?.scoreboard = getPlayerBoard(player)
    }

    private fun getPlayerBoard(player: LifePlayer): Scoreboard {
        val scoreboard = Bukkit.getScoreboardManager().newScoreboard
        val objectiveName = "§l§dThirdlife-${player.uuid}§f"
        val thirdlifeObj = scoreboard.registerNewObjective(objectiveName, "dummy", "§d" + getGameName())
        thirdlifeObj.displaySlot = DisplaySlot.SIDEBAR

        // Get score lines for each player
        val lines = mutableListOf<String>()

        lines.add(divider)

        val lives = player.lives
        lines.add("§aLives§f: $lives")

        val isShadow = player.isShadow
        if (isShadow) {
            lines.add("§cYou are the SHADOW§f")
        }

        lines.add("§7" + divider)

        queueScores(lines, thirdlifeObj)
        return scoreboard
    }
    private fun queueScores(lines: List<String>, objective: Objective): List<Score> {
        val scores = lines.map { objective.getScore(it) }
        var i  = scores.size
        for (score in scores) {
            score.score = i
            i--
        }
        return scores
    }

}

fun getScoreboardDivider(): String {
    return "§7-------------------§f"
}
fun getGameName(): String {
    return "GOTFB"
}