/*
    Models.kt
    This file contains data classes that map to the JSON responses from the Sports Data API.
    Each class corresponds to a specific part of the API response structure.
    Made by: Alex Keys
 */

package keysa.ist

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// 1. The Game Object (Base information)
data class Game(
    @SerializedName("GameID") val gameId: Int,
    @SerializedName("Season") val season: Int,
    @SerializedName("DateTime") val dateTime: String?,
    @SerializedName("Status") val status: String?,
    @SerializedName("AwayTeam") val awayTeam: String,
    @SerializedName("HomeTeam") val homeTeam: String,
    @SerializedName("AwayTeamScore") val awayTeamScore: Int?,
    @SerializedName("HomeTeamScore") val homeTeamScore: Int?,
    @SerializedName("StadiumID") val stadiumId: Int?,
    @SerializedName("Channel") val channel: String?
) : Serializable

// 2. The BoxScore Object (Root of the Stats response)
data class BoxScore(
    @SerializedName("Game") val game: Game,
    @SerializedName("PlayerGames") val playerGames: List<PlayerGame>?
)

// 3. The PlayerStats Object (Individual player statistics)
data class PlayerGame(
    @SerializedName("PlayerID") val playerId: Int,
    @SerializedName("Name") val name: String,
    @SerializedName("Team") val team: String,
    @SerializedName("Position") val position: String,

    // Football Specific
    @SerializedName("PassingYards") val passingYards: Double?,
    @SerializedName("PassingTouchdowns") val passingTouchdowns: Double?,
    @SerializedName("RushingYards") val rushingYards: Double?,
    @SerializedName("RushingTouchdowns") val rushingTouchdowns: Double?,

    // Basketball Specific
    @SerializedName("Points") val points: Double?,
    @SerializedName("Rebounds") val rebounds: Double?,
    @SerializedName("Assists") val assists: Double?
)