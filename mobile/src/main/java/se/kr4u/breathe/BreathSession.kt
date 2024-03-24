package se.kr4u.breathe

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class BreathSession : AppCompatActivity(R.layout.activity_breath_session) {
    enum class Direction {
        IN, OUT
    }

    companion object {
        val TAG = "BreathSession"
    }

    private val sessionViewModel: SessionViewModel by viewModels {
        SessionViewModelFactory((application as SessionApplication).repository)
    }
    lateinit var session: Session
    private lateinit var directionTextView: TextView
    private lateinit var countdownTextView: TextView
    private lateinit var timesTextView: TextView
    private lateinit var actionBar: Toolbar
    private lateinit var job: Deferred<Unit>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        directionTextView = findViewById(R.id.direction)
        countdownTextView = findViewById(R.id.countdown)
        timesTextView = findViewById(R.id.times)
        actionBar = findViewById(R.id.breath_session_toolbar)
        actionBar.setTitle(R.string.breath_session)
        setSupportActionBar(actionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onResume() {
        super.onResume()

        val id = this.intent.extras!!.getInt("Session ID")
        this.lifecycleScope.launch {
            session = sessionViewModel.getSession(id)
            beginInhaleCountdown(session.inhaleDuration, session.repetitions)
        }
    }

    override fun onPause() {
        super.onPause()
        CoroutineScope(Dispatchers.Main).launch {
            job.cancelAndJoin()
        }
    }

    private fun beginInhaleCountdown(duration: Int, repetitions: Int) {
        directionTextView.text = resources.getText(R.string.direction_in)
        countdownTextView.text = duration.toString()
        timesTextView.text = repetitions.toString()
        var count = duration
        var times = repetitions
        var direction = Direction.IN
        job = CoroutineScope(Dispatchers.Main).launchPeriodicAsync(1000) {
            when (count) {
                0 -> {
                    if (direction == Direction.IN) {
                        direction = Direction.OUT
                        changeDirection(direction)
                        count = session.exhaleDuration
                    } else {
                        if (times > 0) {
                            direction = Direction.IN
                            changeDirection(direction)
                            count = session.inhaleDuration
                            times--
                        }
                    }
                }
                else -> {
                }
            }
            count--
            if (times > 0) {
                countdown(direction, count, times)
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    job.cancelAndJoin()
                    this@BreathSession.finish()
                }
            }
        }
    }

    fun CoroutineScope.launchPeriodicAsync(repeatMillis: Long, action: () -> Unit) =
        this.async {
            if (repeatMillis > 0) {
                while (isActive) {
                    action()
                    Log.d(TAG, "Delaying for " + repeatMillis + " ms")
                    delay(repeatMillis)
                }
            } else {
                action()
            }
        }

    private fun countdown(direction: Direction, duration: Int, repetitions: Int) {
        var count = duration + 1
        Log.d(TAG, count.toString())
        countdownTextView.text = count.toString()
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator()
        fadeOut.duration = 1000
        countdownTextView.animation = fadeOut

        directionTextView.text = when (direction) {
            Direction.IN -> {
                resources.getText(R.string.direction_in)
            }

            Direction.OUT -> {
                resources.getText(R.string.direction_out)
            }
        }
        timesTextView.text = repetitions.toString()
    }

    private fun changeDirection(direction: Direction) {
        // Add sounds and/or vibration here
    }
}