/*
MainActivity.kt
Home screen for selecting season and sport, with options to remember preferences.
Made By: Alex Keys
 */

package keysa.ist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import keysa.ist.ScoresActivity

class MainActivity : AppCompatActivity() {
    // Declare views
    private lateinit var seasonSpinner: Spinner
    private lateinit var footballButton: RadioButton
    private lateinit var basketballButton: RadioButton
    private lateinit var rememberPreferences: CheckBox
    private lateinit var viewScoresButton: Button


    //onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: MaterialToolbar = findViewById(R.id.menuBar)
        setSupportActionBar(toolbar)

        // Initialize views
        seasonSpinner = findViewById(R.id.seasonSpinner)
        footballButton = findViewById(R.id.footballButton)
        basketballButton = findViewById(R.id.basketballButton)
        rememberPreferences = findViewById(R.id.rememberPreferences)
        viewScoresButton = findViewById(R.id.viewScoresButton)

        setupSeasonSpinner()
        loadPreferences()

        viewScoresButton.setOnClickListener {
            savePreferences()
            navigateToScores()
        }
    }

    // Menu setup
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // Menu item selection handling
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                recreate()
                true
            }

            R.id.action_about -> {
                val view = layoutInflater.inflate(R.layout.activity_about, null)
                AlertDialog.Builder(this)
                    .setView(view)
                    .setPositiveButton("Close", null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Setup season spinner with options
    private fun setupSeasonSpinner() {
        val seasons = listOf("2025", "2024")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, seasons)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        seasonSpinner.adapter = adapter
    }

    // Navigate to ScoresActivity with selected options
    private fun navigateToScores() {
        val selectedSeason = seasonSpinner.selectedItem.toString()
        val selectedSport = if (footballButton.isChecked) "Football" else "Basketball"

        val intent = Intent(this, ScoresActivity::class.java).apply {
            putExtra("SEASON", selectedSeason)
            putExtra("SPORT", selectedSport)
        }
        startActivity(intent)
    }

    // Save user preferences if checkbox is checked
    private fun savePreferences() {
        if (rememberPreferences.isChecked) {
            val sharedPref = getPreferences(MODE_PRIVATE) ?: return
            with(sharedPref.edit()) {
                putString("SAVED_SEASON", seasonSpinner.selectedItem.toString())
                putBoolean("IS_FOOTBALL", footballButton.isChecked)
                apply()
            }
        }
    }

    // Load saved preferences if available
    private fun loadPreferences() {
        val sharedPref = getPreferences(MODE_PRIVATE)
        val savedSeason = sharedPref.getString("SAVED_SEASON", "2025")
        val isFootball = sharedPref.getBoolean("IS_FOOTBALL", true)

        if (isFootball) footballButton.isChecked = true else basketballButton.isChecked = true

        val seasons = listOf("2025", "2024", "2023", "2022")
        val spinnerPosition = seasons.indexOf(savedSeason)
        if (spinnerPosition >= 0) {
            seasonSpinner.setSelection(spinnerPosition)
        }
    }
}