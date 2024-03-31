package se.kr4u.breathe.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import se.kr4u.breathe.SessionApplication
import se.kr4u.breathe.SessionViewModel
import se.kr4u.breathe.SessionViewModelFactory

class BeginSession: ComponentActivity() {
    private val sessionViewModel: SessionViewModel by viewModels {
        SessionViewModelFactory((application as SessionApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}