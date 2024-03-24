package se.kr4u.breathe

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SessionViewModel(private val repo: SessionRepository) : ViewModel() {
    val allSessions = repo.allSessions.asLiveData()

    fun insert(session: Session) = viewModelScope.launch {
        repo.insert(session)
    }

    fun delete(session: Session) = viewModelScope.launch {
        repo.delete(session)
    }

    suspend fun getSession(id: Int): Session {
        return repo.getById(id)
    }
}

class SessionViewModelFactory(private val repo: SessionRepository) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SessionViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}