package se.kr4u.breathe

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var sessionDB: SessionDatabase
    private lateinit var sessionList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setSupportActionBar(findViewById(R.id.main_toolbar))
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionList = findViewById(R.id.session_list)
        val session_dao: SessionDao
        //session_list.adapter = SessionAdapter(session_dao.loadSessions())
        val add_session_button = findViewById<FloatingActionButton>(R.id.add_session)
        add_session_button.setOnClickListener {
            addSession()
        }
        sessionDB = Room.databaseBuilder(
            this.applicationContext,
            SessionDatabase::class.java,
            "sessions.db"
        ).build()
    }

    override fun onResume() {
        super.onResume()
        val context: Context = this
        sessionList.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch {
            val sessions = sessionDB.getSessionDao().loadAllSessions()
            sessionList.adapter = SessionAdapter(sessions)
        }
    }

    private fun addSession() {
        var intent = Intent()
        intent.component = ComponentName( this.applicationContext, CreateSessionActivity::class.java)
        intent.action = "se.kr4u.breathe.CREATE_SESSION"
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        startActivity(intent)
    }
}