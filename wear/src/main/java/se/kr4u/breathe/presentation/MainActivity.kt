/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package se.kr4u.breathe.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.wear.ambient.AmbientLifecycleObserver
import androidx.wear.widget.CurvingLayoutCallback
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import se.kr4u.breathe.R
import se.kr4u.breathe.Session
import se.kr4u.breathe.SessionAdapter
import se.kr4u.breathe.SessionApplication
import se.kr4u.breathe.SessionViewModel
import se.kr4u.breathe.SessionViewModelFactory

class MainActivity() : AppCompatActivity(R.layout.main), AmbientLifecycleObserver {
    override val isAmbient = true
    private val sessionViewModel: SessionViewModel by viewModels {
        SessionViewModelFactory((application as SessionApplication).repository)
    }

    private lateinit var sessionList: WearableRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)

        sessionList = findViewById(R.id.session_list)
        val sessionAdapter = SessionAdapter()
        sessionAdapter.onItemClick = { it: Session, view: View ->
            beginSession(it, view)
        }
        sessionList.adapter = sessionAdapter
        sessionList.layoutManager = WearableLinearLayoutManager(this, CurvingLayoutCallback(this))
        sessionList.isEdgeItemsCenteringEnabled = true
        sessionList.setHasFixedSize(true)
        val decorator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        sessionList.addItemDecoration(decorator)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        sessionViewModel.allSessions.observe(this) { sessions ->
            sessions?.let { sessionAdapter.submitList(it) }
        }
    }

    fun beginSession(session: Session, view: View) {
        val intent = Intent(this.applicationContext, BeginSession::class.java)
        intent.action = "se.kr4u.breathe.START_SESSION"
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.putExtra("SESSION_ID", session.id)

        startActivity(intent)
    }
}

