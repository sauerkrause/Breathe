package se.kr4u.breathe

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class SessionRepository(private val sessionDao: SessionDao) {
    val allSessions: Flow<List<Session>> = sessionDao.loadSessions()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(session: Session) {
        sessionDao.insertSessions(session)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getById(id: Int): Session {
        return sessionDao.loadSessionById(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(session: Session) {
        sessionDao.deleteSessions(session)
    }

    @WorkerThread
    suspend fun deleteAll() {
        sessionDao.deleteAllSessions()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(session: Session) {
        sessionDao.updateSessions(session)
    }
}