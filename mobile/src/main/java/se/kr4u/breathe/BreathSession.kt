package se.kr4u.breathe

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.TextView
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
    enum class State {
        IN, OUT, DONE
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
    private lateinit var pulser: View
    private lateinit var actionBar: Toolbar
    private lateinit var job: Deferred<Unit>

    private var count: Int? = null
    private var times: Int? = null
    private var state: State = State.IN

/*
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("count", count!!)
        outState.putInt("times", times!!)
        outState.putInt("direction", when (state) {
            State.OUT -> 0
            State.IN -> 1
            State.DONE -> 2
        })
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        when(savedInstanceState.getInt("state", 1)) {
            0 -> state = State.OUT
            1 -> state = State.IN
            2 -> state = State.DONE
        }
        count = savedInstanceState.getInt("count")
        times = savedInstanceState.getInt("times")
        super.onRestoreInstanceState(savedInstanceState)
    }
 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        directionTextView = findViewById(R.id.direction)
        countdownTextView = findViewById(R.id.countdown)
        timesTextView = findViewById(R.id.times)
        pulser = findViewById(R.id.pulser)
        actionBar = findViewById(R.id.breath_session_toolbar)
        actionBar.setTitle(R.string.breath_session)
        setSupportActionBar(actionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onResume() {
        super.onResume()

        val id = this.intent.extras!!.getInt("Session ID")
        this.lifecycleScope.launch {
            session = sessionViewModel.getSession(id)
            if (times == null) {
                Log.d(TAG, "Beginning countdown")
                state = State.IN
                Log.d(TAG, "State = $state")
                beginCountdown(state, session.inhaleDuration, session.repetitions)
            } else {
                Log.d(TAG, "Resuming countdown")
                beginCountdown(state, count!!, times!!)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        CoroutineScope(Dispatchers.Main).launch {
            job.cancelAndJoin()
        }
    }

    private fun beginCountdown(initialDirection: State, duration: Int, repetitions: Int) {
        directionTextView.text = resources.getText(R.string.breathe_in)
        countdownTextView.text = duration.toString()
        timesTextView.text = resources.getQuantityString(R.plurals.number_of_repetitions, repetitions, repetitions)
        count = duration
        times = repetitions
        state = initialDirection
        when (state) {
            State.IN -> if (count == session.inhaleDuration) {
                changeDirection(state)
            }
            State.OUT -> if (count == session.exhaleDuration) {
                changeDirection(state)
            }
            State.DONE -> {
                changeDirection(state)
            }
        }
        job = CoroutineScope(Dispatchers.Main).launchPeriodicAsync(1000) {
            when (count) {
                0 -> {
                    if (state == State.IN) {
                        state = State.OUT
                        changeDirection(state)
                        count = session.exhaleDuration
                    } else if (state == State.OUT) {
                        if (times!! > 0) {
                            state = State.IN
                            changeDirection(state)
                            count = session.inhaleDuration
                            times = times!! - 1
                        }
                    }
                }
            }
            count = count!! - 1
            if (times!! > 0) {
                countdown(state, count!!, times!!)
            } else if (times!! == 0) {
                Log.d(TAG, "Technically done")
                state = State.DONE
                changeDirection(state)
                count = 0
                times = -1
                countdown(state, count!!, times!!)
            } else if (times!! < 0){
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

    private fun countdown(state: State, duration: Int, repetitions: Int) {
        Log.d(TAG, "State = $state")
        directionTextView.text = when (state) {
            State.IN -> {
                resources.getText(R.string.breathe_in)
            }
            State.OUT -> {
                resources.getText(R.string.breathe_out)
            }
            State.DONE -> {
                resources.getText(R.string.done)
            }
        }
        val count = duration + 1
        Log.d(TAG, count.toString())
        countdownTextView.text = count.toString()
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator()
        fadeOut.duration = 1000
        countdownTextView.animation = fadeOut
        if (state != State.DONE) {
            timesTextView.text = resources.getQuantityString(
                R.plurals.number_of_repetitions,
                repetitions,
                repetitions
            )
        } else {
            timesTextView.text = resources.getText(R.string.done)
        }
    }

    private fun changeDirection(state: State) {
        // Vibration waveform:
        val waveform = when (state) {
            State.IN -> {
                longArrayOf(0L, 500L, 100L, 100L)
            }
            State.OUT -> {
                longArrayOf(0L, 100L, 100L, 500L)
            }
            State.DONE -> {
                longArrayOf(0L, 50L, 100L, 50L)
            }
        }
        // Vibrate using the waveform depending on android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            val effect = VibrationEffect.createWaveform(waveform, -1)
            vibrator.vibrate(effect)
        } else {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(waveform, -1)
        }

        // Animate the pulser
        val pulse = when (state) {
            State.IN -> ScaleAnimation(1f, 2f, 1f, 2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            State.OUT -> ScaleAnimation(2f, 1f, 2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            State.DONE -> ScaleAnimation(1f, 1f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 1f)
        }
        pulse.duration = when (state) {
            State.OUT -> session.exhaleDuration * 1000L
            State.IN -> session.inhaleDuration * 1000L
            State.DONE -> 1000L
        }
        pulse.fillAfter = true
        pulser.startAnimation(pulse)
    }
}