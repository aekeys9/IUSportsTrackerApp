/*
    SportsApiService.kt
    API service interface and client setup for accessing Sports Data API.
    Made By: Alex Keys
 */

package keysa.ist

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Base URLs for different sports
private const val BASE_URL_CFB = "https://api.sportsdata.io/v3/cfb/"
private const val BASE_URL_CBB = "https://api.sportsdata.io/v3/cbb/"

// Retrofit service interface
interface SportsApiService {
    // Fetch games for a given season
    @GET("scores/json/Games/{season}")
    suspend fun getGames(
        @Path("season") season: String,
        @Query("key") apiKey: String
    ): List<Game>

    // Fetch box score for a specific football game
    @GET("stats/json/BoxScore/{gameId}")
    suspend fun getBoxScoreFootball(
        @Path("gameId") gameId: Int,
        @Query("key") apiKey: String
    ): List<BoxScore>

    // Fetch box score for a specific basketball game
    @GET("stats/json/BoxScore/{gameId}")
    suspend fun getBoxScoreBasketball(
        @Path("gameId") gameId: Int,
        @Query("key") apiKey: String
    ): BoxScore
}

object SportsApiClient {
    // Create a specific Gson instance
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    // Function to get the appropriate service based on sport
    fun getService(sport: String): SportsApiService {
        val baseUrl = if (sport.equals("Basketball", ignoreCase = true)) BASE_URL_CBB else BASE_URL_CFB

        // We reuse the client logic for both, just switching the URL
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            // THIS LINE IS KEY: It plugs Gson into the networking layer
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(SportsApiService::class.java)
    }
}
