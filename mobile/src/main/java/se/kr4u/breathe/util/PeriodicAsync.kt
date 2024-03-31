package se.kr4u.breathe.util

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class PeriodicAsync {
    companion object {
        private const val TAG = "PeriodicAsync"
        fun CoroutineScope.launchPeriodicAsync(repeatMillis: Long, action: () -> Unit) =
            this.async {
                if (repeatMillis > 0) {
                    while (isActive) {
                        action()
                        Log.d(TAG, "Delaying for $repeatMillis ms")
                        delay(repeatMillis)
                    }
                } else {
                    action()
                }
            }
    }
}