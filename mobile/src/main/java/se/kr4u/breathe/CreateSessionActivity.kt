package se.kr4u.breathe

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch

class CreateSessionActivity : AppCompatActivity(R.layout.create_session) {
    private lateinit var inhaleDuration: EditText
    private lateinit var exhaleDuration: EditText
    private lateinit var repetitions: EditText
    private lateinit var sessionDB: SessionDatabase

    companion object {
        val TAG = "CreateSessionActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: androidx.appcompat.widget.Toolbar = findViewById(R.id.add_session_toolbar)
        setSupportActionBar(actionBar)
        inhaleDuration = findViewById(R.id.inhale_duration)
        exhaleDuration = findViewById(R.id.exhale_duration)
        repetitions = findViewById(R.id.repetitions)
        sessionDB = Room.databaseBuilder(
            this.applicationContext,
            SessionDatabase::class.java,
            "sessions.db"
        ).build()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_session, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cancel -> {
                Log.d(TAG, "Cancel")
                this.finish()
                return true
            }
            R.id.action_add_session -> {
                Log.d(TAG, "Add a session")
                lifecycleScope.launch {
                    val inhale = inhaleDuration.text.toString().toInt()
                    val exhale = exhaleDuration.text.toString().toInt()
                    val repetitions = repetitions.text.toString().toInt()
                    sessionDB.getSessionDao()
                        .insertSessions(Session(0, exhale, inhale, repetitions, 0))
                }
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}