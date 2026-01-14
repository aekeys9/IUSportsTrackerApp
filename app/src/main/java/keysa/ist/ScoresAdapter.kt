/*
ScoresAdapter.kt
Adapter for displaying a list of game scores in a RecyclerView.
Made By: Alex Keys
 */

package keysa.ist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adapter class for RecyclerView
class ScoresAdapter(
    private val games: List<Game>,
    private val onGameClicked: (Game) -> Unit
) : RecyclerView.Adapter<ScoresAdapter.ScoreViewHolder>() {

    // ViewHolder class for individual score items
    class ScoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gameDateText: TextView = view.findViewById(R.id.gameDateText)
        val gameStatusText: TextView = view.findViewById(R.id.gameStatusText)
        val homeTeamText: TextView = view.findViewById(R.id.homeTeamText)
        val awayTeamText: TextView = view.findViewById(R.id.awayTeamText)
        val gameInfoText: TextView = view.findViewById(R.id.gameInfoText)
        val scoreText: TextView = view.findViewById(R.id.scoreText)
        val homeTeamImage: ImageView = view.findViewById(R.id.imageHomeTeam)
    }

    // Inflate the item layout and create the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_scores, parent, false)
        return ScoreViewHolder(view)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val game = games[position]

        holder.gameDateText.text = game.dateTime?.take(10) ?: "TBD"
        holder.gameStatusText.text = game.status ?: "Scheduled"
        holder.homeTeamText.text = game.homeTeam
        holder.awayTeamText.text = game.awayTeam
        holder.gameInfoText.text = "Stadium ID: ${game.stadiumId ?: "Unknown"}"

        if (game.homeTeamScore != null && game.awayTeamScore != null) {
            holder.scoreText.text = "${game.homeTeamScore} - ${game.awayTeamScore}"
        } else {
            holder.scoreText.text = "vs"
        }

        // Example image loading with Glide (if you had URLs)
        // Glide.with(holder.itemView).load(game.LogoUrl).into(holder.homeTeamImage)

        holder.itemView.setOnClickListener {
            onGameClicked(game)
        }
    }

    // Return the total number of items
    override fun getItemCount() = games.size
}