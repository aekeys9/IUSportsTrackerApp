/*
GameDetailsActivity.kt
Displays detailed information about a selected game, including team stats and individual player stats.
Made By: Alex Keys
 */

package keysa.ist

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class GameDetailsActivity : AppCompatActivity() {

    private lateinit var detailTitleText: TextView
    private lateinit var detailDateText: TextView
    private lateinit var teamStatsText: TextView
    private lateinit var playerStatsText: TextView
    private lateinit var imageStadium: ImageView

    private val API_KEY = "Hidden for security"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_details)

        detailTitleText = findViewById(R.id.detailTitleText)
        detailDateText = findViewById(R.id.detailDateText)
        teamStatsText = findViewById(R.id.teamStatsText)
        playerStatsText = findViewById(R.id.playerStatsText) // Bind the view
        imageStadium = findViewById(R.id.imageDetailHeader)


        val game = intent.getSerializableExtra("GAME_DATA") as? Game
        val sport = intent.getStringExtra("SPORT") ?: "Football"


        if (sport.equals("Basketball", ignoreCase = true)) {
            imageStadium.setImageResource(R.drawable.ssah)
        } else {

            imageStadium.setImageResource(R.drawable.iustadium)
        }

        game?.let {
            detailTitleText.text = "${it.awayTeam} @ ${it.homeTeam}"
            detailDateText.text = "${it.dateTime?.take(10)} - Stadium ID: ${it.stadiumId}"
            teamStatsText.text = "Final Score: ${it.awayTeamScore} - ${it.homeTeamScore}"

            // Trigger the API call for stats
            fetchBoxScore(it.gameId, sport)
        }
    }

    private fun fetchBoxScore(gameId: Int, sport: String) {
        lifecycleScope.launch {
            try {
                val service = SportsApiClient.getService(sport)
                val boxScore: BoxScore?

                if(sport.equals("Basketball", ignoreCase = true)) {
                    boxScore = service.getBoxScoreBasketball(gameId, API_KEY)
                } else {
                    val boxScoreList = service.getBoxScoreFootball(gameId, API_KEY)
                    boxScore = if(boxScoreList.isNotEmpty()) boxScoreList[0] else null
                }

                if(boxScore != null) {
                    displayPlayerStats(boxScore, sport)
                } else {
                    playerStatsText.text = "No box score data available."
                }
            } catch (e: Exception) {
                playerStatsText.text = "Failed to load player stats."
                Log.i("GameDetailsActivity", "Error fetching box score: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun displayPlayerStats(boxScore: BoxScore, sport: String) {
        val players = boxScore.playerGames ?: emptyList()
        val statsBuilder = StringBuilder()
        Log.i("GameDetailsActivity", "Number of player stats retrieved: ${players.size}")

        if(players.isEmpty()){
            playerStatsText.text = "No individual player stats available."
            return
        }

        if (sport.equals("Basketball", ignoreCase = true)) {
            val playerStats : List<PlayerGame>
            val jsonString = Gson().toJson(players)
            val listType: Type = object : TypeToken<List<PlayerGame>>() {}.type
            playerStats =  Gson().fromJson(jsonString, listType);
            // BASKETBALL LOGIC: Find top scorer and rebounder
            val topScorer = playerStats.maxByOrNull { it.points ?: 0.0 }
            val topRebounder = playerStats.maxByOrNull { it.rebounds ?: 0.0 }

            if (topScorer != null) {
                statsBuilder.append("Top Scorer: ${topScorer.name} (${topScorer.team}) - ${topScorer.points?.toInt()} pts\n")
            }
            if (topRebounder != null) {
                statsBuilder.append("Top Rebounder: ${topRebounder.name} (${topRebounder.team}) - ${topRebounder.rebounds?.toInt()} rebs")
            }

        } else {
            // FOOTBALL LOGIC: Find passing and rushing leaders
            val passer = players.maxByOrNull { it.passingYards ?: 0.0 }
            val rusher = players.maxByOrNull { it.rushingYards ?: 0.0 }

            if (passer != null && (passer.passingYards ?: 0.0) > 0) {
                statsBuilder.append("Passing Leader: ${passer.name} (${passer.team})\n")
                statsBuilder.append("${passer.passingYards?.toInt()} Yds, ${passer.passingTouchdowns?.toInt()} TD\n\n")
            }

            if (rusher != null && (rusher.rushingYards ?: 0.0) > 0) {
                statsBuilder.append("Rushing Leader: ${rusher.name} (${rusher.team})\n")
                statsBuilder.append("${rusher.rushingYards?.toInt()} Yds, ${rusher.rushingTouchdowns?.toInt()} TD")
            }
        }

        if (statsBuilder.isEmpty()) {
            playerStatsText.text = "No individual player stats available."
        } else {
            playerStatsText.text = statsBuilder.toString()
        }
    }
}