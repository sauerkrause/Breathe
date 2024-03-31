/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package se.kr4u.breathe.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import kotlinx.coroutines.flow.Flow
import se.kr4u.breathe.Direction
import se.kr4u.breathe.R
import se.kr4u.breathe.Session
import se.kr4u.breathe.SessionApplication
import se.kr4u.breathe.SessionViewModel
import se.kr4u.breathe.SessionViewModelFactory
import se.kr4u.breathe.presentation.theme.BreatheTheme

class MainActivity : ComponentActivity() {
    private val sessionViewModel: SessionViewModel by viewModels {
        SessionViewModelFactory((application as SessionApplication).repository)
    }
    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            val sessions: List<Session> by sessionViewModel.flowAllSessions.collectAsState(emptyList<Session>())
            WearApp(sessions) {
                beginSession(it)
            }
        }
    }

    fun beginSession(session: Session) {
        val intent = Intent(this.applicationContext, BeginSession::class.java)
        intent.action = "se.kr4u.breathe.START_SESSION"
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.putExtra("SESSION_ID", session.id)

        startActivity(intent)
    }
}

val contentModifier = Modifier
    .fillMaxWidth()
    .padding(bottom = 8.dp)

@Composable
fun WearApp(sessions: List<Session>, onClick: (Session) -> Unit) {
    BreatheTheme {
        val listState = rememberScalingLazyListState()
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            autoCentering = AutoCenteringParams(itemIndex = 0),
            flingBehavior = ScalingLazyColumnDefaults.snapFlingBehavior(
                state = listState,
                snapOffset = 0.dp
            ),
            state = listState
        ) {
            for (session in sessions) {
                item {SessionRow(contentModifier, session, onClick) }
            }
        }
    }
}

@Composable
fun SessionRow(modifier: Modifier = Modifier, session: Session, click: (Session) -> Unit) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                click(session)
            },
            content = {
                Image(
                    painterResource(R.drawable.baseline_arrow_right_24),
                    contentDescription = "",
                    contentScale = ContentScale.Inside,
                    modifier = Modifier.fillMaxSize()
                )

            },
            modifier = Modifier.height(20.dp).wrapContentWidth()
        )
        Duration(session, Direction.IN)
        Duration(session, Direction.OUT)
        Repetition(session)
    }
}

@Composable
fun Duration(session: Session, direction: Direction) {
    Text(
        color = MaterialTheme.colors.primary,
        textAlign = TextAlign.Center,
        text = when(direction) {
            Direction.IN -> session.inhaleDuration.toString()
            Direction.OUT -> session.exhaleDuration.toString()
            else -> {""}
        },
        modifier = Modifier.padding(start = 4.dp)
    )
    Second()
    Direction(direction)
}

@Composable
fun Second() {
    Text(
        color = MaterialTheme.colors.primary,
        modifier = Modifier.padding(start = 0.dp, end = 4.dp),
        text = stringResource(id = R.string.second)
    )
}

@Composable
fun Direction(direction: Direction) {
    Text (
        color = MaterialTheme.colors.secondary,
        modifier = Modifier.padding(end = 4.dp),
        text = stringResource(id = when(direction) {
            Direction.IN -> R.string.direction_in
            Direction.OUT -> R.string.direction_out
            Direction.DONE -> R.string.done
        })
    )
}

@Composable
fun Repetition(session: Session) {
    Text (
        color = MaterialTheme.colors.primary,
        textAlign = TextAlign.Center,
        text = session.repetitions.toString(),
    )
    Text (
        color = MaterialTheme.colors.secondary,
        modifier = Modifier.padding(all = 4.dp),
        text = stringResource(id = R.string.repetitions)
    )
}


@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    val previewList = listOf(Session(0, 4, 8, 5, 0),
        Session(0, 3, 6, 5, 0),
        Session(0, 5, 10, 5, 0),
        Session(0, 2, 4, 5, 0))
    WearApp(previewList, {})
}
