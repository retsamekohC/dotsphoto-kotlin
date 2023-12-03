@file:OptIn(ExperimentalEncodingApi::class)

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import io.ktor.client.*
import io.ktor.client.engine.apache5.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.io.encoding.ExperimentalEncodingApi


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MainView()
    }
}

@Composable
fun MainView() = Test().AppPreview()

val ApiClientLocal = compositionLocalOf { ApiClient(Apache5) }
val CoroutineScopeLocal = compositionLocalOf { CoroutineScope(Dispatchers.Default) }

class Test {
    @Preview
    @Composable
    fun AppPreview() {
        var activeScreen by remember { mutableStateOf(ActiveScreen.LOGIN) }
        val changeScreen = { x: ActiveScreen ->
            activeScreen = x
        }

        CompositionLocalProvider(ApiClientLocal provides ApiClient(Apache5)) {
            CompositionLocalProvider(CoroutineScopeLocal provides rememberCoroutineScope()) {
                MaterialTheme {
                    when (activeScreen) {
                        ActiveScreen.LOGIN -> loginScreen(changeScreen)
                        ActiveScreen.MAIN -> mainScreen(changeScreen)
                        ActiveScreen.REGISTRATION -> registrationScreen(changeScreen)
                    }
                }
            }
        }
    }
}

@Composable
fun TestScreen() {
    BoxWithConstraints(){
        val m = maxHeight
        Row {
            //Text("width = $maxWidth")
            Text("height = $m")
        }
    }
}