package se.kr4u.breathe

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(vararg sessions: Session)

    @Update
    suspend fun updateSessions(vararg sessions: Session)

    @Delete
    suspend fun deleteSessions(vararg sessions: Session)

    @Query("SELECT * FROM session WHERE id = :id")
    suspend fun loadSessionById(id: Int): Session

    @Query("SELECT * FROM session")
    suspend fun loadAllSessions(): List<Session>

    @Query("SELECT * FROM session")
    fun loadSessions(): Flow<List<Session>>
}