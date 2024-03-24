package se.kr4u.breathe

import android.app.Application

class SessionApplication: Application() {
    val database by lazy { SessionDatabase.getDatabase(this) }
    val repository by lazy { SessionRepository(database.getSessionDao()) }
}