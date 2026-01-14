/*
ScoresActivity.kt
Displays the list of Indiana University sports scores for a selected season and sport.
Made By: Alex Keys
 */

package keysa.ist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ScoresActivity : AppCompatActivity() {
    private var sport: String = "Football"

    // Declare views
    private lateinit var scoresTitleText: TextView
    private lateinit var filterSpinner: Spinner
    private lateinit var progressBarAPI: ProgressBar
    private lateinit var scoresRecycler: RecyclerView

    private val API_KEY = "Hidden for Security"

    //onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scores)

        // Initialize views
        scoresTitleText = findViewById(R.id.scoresTitleText)
        filterSpinner = findViewById(R.id.filterSpinner)
        progressBarAPI = findViewById(R.id.progressBarAPI)
        scoresRecycler = findViewById(R.id.scoresRecycler)

        val season = intent.getStringExtra("SEASON") ?: "2025"
        sport = intent.getStringExtra("SPORT") ?: "Football"

        scoresTitleText.text = "IU $sport Scores ($season)"

        setupFilterSpinner()
        fetchScores(season, sport)
    }

    // Setup filter spinner
    private fun setupFilterSpinner() {
        val filters = listOf("All Games", "Final", "Scheduled")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filters)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinner.adapter = adapter
    }

    // Fetch scores from API
    private fun fetchScores(season: String, sport: String) {
        progressBarAPI.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // In ScoresActivity.kt
                val response = SportsApiClient.getService(sport).getGames(season, API_KEY)
                // Filter for "IND" (Indiana) games
                val iuGames = response.filter { it.homeTeam == "IND" || it.awayTeam == "IND" }

                setupRecyclerView(iuGames)
                progressBarAPI.visibility = View.GONE
            } catch (e: Exception) {
                progressBarAPI.visibility = View.GONE
                Log.i("ScoresActivity", "Error fetching scores: ${e.message}")
                Toast.makeText(this@ScoresActivity, "Error fetching scores: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Setup RecyclerView with scores
    private fun setupRecyclerView(games: List<Game>) {
        val adapter = ScoresAdapter(games) { selectedGame ->
            val intent = Intent(this, GameDetailsActivity::class.java)
            intent.putExtra("GAME_DATA", selectedGame)


            intent.putExtra("SPORT", sport)
            startActivity(intent)
        }
        scoresRecycler.layoutManager = LinearLayoutManager(this)
        scoresRecycler.adapter = adapter
    }
}