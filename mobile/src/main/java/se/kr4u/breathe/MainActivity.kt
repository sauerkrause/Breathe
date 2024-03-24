package se.kr4u.breathe

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var sessionList: RecyclerView

    private val sessionViewModel: SessionViewModel by viewModels {
        SessionViewModelFactory((application as SessionApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.main_toolbar))
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionList = findViewById(R.id.session_list)
        val adapter = SessionAdapter()
        adapter.onItemClick = {
            beginSession(it)
        }
        sessionList.layoutManager = LinearLayoutManager(this)
        sessionList.adapter = adapter
        val decorator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        sessionList.addItemDecoration(decorator)
        val swipeHelper = SwipeToDeleteCallback(this)
        swipeHelper.onSwipe = { viewHolder: SessionAdapter.ViewHolder, _: Int ->
            sessionViewModel.delete(viewHolder.sessionEntity)
        }
        val itemTouchHelper = ItemTouchHelper(swipeHelper)
        itemTouchHelper.attachToRecyclerView(sessionList)

        sessionViewModel.allSessions.observe(this, Observer { sessions ->
            sessions?.let { adapter.submitList(it) }

        })
        val addSessionButton = findViewById<FloatingActionButton>(R.id.add_session)
        addSessionButton.setOnClickListener {
            addSession()
        }

    }

    private fun addSession() {
        val intent = Intent()
        intent.component = ComponentName( this.applicationContext, CreateSessionActivity::class.java)
        intent.action = "se.kr4u.breathe.CREATE_SESSION"
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        startActivity(intent)
    }

    private fun beginSession(session: Session) {
        val intent = Intent()
        intent.component = ComponentName(this.applicationContext, BreathSession::class.java)
        intent.action = "se.kr4u.breathe.BEGIN_SESSION"
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.putExtra("Session ID", session.id)
        startActivity(intent)
    }
}