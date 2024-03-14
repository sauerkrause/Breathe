package se.kr4u.breathe

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [Session::class])
abstract class SessionDatabase : RoomDatabase() {
    abstract fun getSessionDao(): SessionDao
}