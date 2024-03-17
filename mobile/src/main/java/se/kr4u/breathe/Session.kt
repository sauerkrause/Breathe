package se.kr4u.breathe

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val inhaleDuration: Int,
    val exhaleDuration: Int,
    val repetitions: Int,
    val timesUsed: Int,
)
