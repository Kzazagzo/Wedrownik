package pl.put.szlaki.ui.screens

import android.os.Parcelable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize
import pl.put.szlaki.R
import pl.put.szlaki.ui.models.DataStatus
import pl.put.szlaki.ui.models.SzlakiScreenModel

@Parcelize
object LoadingScreen : Screen, Parcelable {
    private fun readResolve(): Any = LoadingScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val contentLoaded by getScreenModel<SzlakiScreenModel>().szlakiInCategories.collectAsState(
            initial = DataStatus.Loading,
        )

        val superAnimacja = remember { Animatable(0f) }
        val size by animateDpAsState(targetValue = 300.dp * superAnimacja.value)
        val rotation by animateFloatAsState(targetValue = -70f + (70f) * superAnimacja.value)
        val pidEffect by animateFloatAsState(
            targetValue = if (superAnimacja.value >= 1f) 360f else 0f,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        )

        // To może być złe, ale po co tworzyć drugą coroutine
        LaunchedEffect(Unit) {
            superAnimacja.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 3000),
            )
        }

        LaunchedEffect(contentLoaded) {
            delay(1000L)
            when (contentLoaded) {
                is DataStatus.Error -> DataStatus.Error((contentLoaded as DataStatus.Error).msg)
                DataStatus.Loading -> DataStatus.Loading
                is DataStatus.Success -> {
                    navigator?.replace(MainSingleScreen)
                }
            }
        }

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.onPrimary),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = null,
                    modifier =
                        Modifier.size(size)
                            .rotate(rotation + pidEffect),
                )
                Spacer(modifier = Modifier.height(20.dp))
                CircularProgressIndicator()
            }
        }
    }
}
