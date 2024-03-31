package se.kr4u.breathe

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch

class CreateSessionActivity : AppCompatActivity(R.layout.create_session) {
    private lateinit var inhaleDuration: EditText
    private lateinit var exhaleDuration: EditText
    private lateinit var repetitions: EditText
    private val sessionViewModel: SessionViewModel by viewModels {
        SessionViewModelFactory((application as SessionApplication).repository)
    }

    companion object {
        val TAG = "CreateSessionActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: androidx.appcompat.widget.Toolbar = findViewById(R.id.add_session_toolbar)
        setSupportActionBar(actionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        inhaleDuration = findViewById(R.id.inhale_duration)
        exhaleDuration = findViewById(R.id.exhale_duration)
        repetitions = findViewById(R.id.repetitions)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_session, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Log.d(TAG, "Cancel")
                this.finish()
                return true
            }
            R.id.action_add_session -> {
                Log.d(TAG, "Add a session")
                val inhale = inhaleDuration.text.toString().toInt()
                val exhale = exhaleDuration.text.toString().toInt()
                val repetitions = repetitions.text.toString().toInt()
                if (repetitions > 0 && inhale > 0 && exhale > 0) {
                    sessionViewModel.insert(Session(0, inhale, exhale, repetitions, 0))
                } else {
                    Toast.makeText(this,
                        getString(R.string.durations_and_repetitions_cannot_be_0), Toast.LENGTH_SHORT).show()
                }
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}