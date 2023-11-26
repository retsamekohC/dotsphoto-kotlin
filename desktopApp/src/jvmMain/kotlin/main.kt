@file:OptIn(ExperimentalEncodingApi::class)

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlin.io.encoding.ExperimentalEncodingApi


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MainView()
    }
}


@Composable
fun MainView() = Test().AppPreview()

class Test {
    @Preview
    @Composable
    fun AppPreview() {

        var activeScreen by remember { mutableStateOf(ActiveScreen.REGISTRATION) }
        val changeScreen = {x:ActiveScreen ->
            activeScreen = x
        }
        val myCoroutineScope = rememberCoroutineScope()

        MaterialTheme {
            when (activeScreen) {
                ActiveScreen.LOGIN -> loginScreen(changeScreen)
                ActiveScreen.MAIN -> mainScreen(myCoroutineScope,changeScreen)
                ActiveScreen.REGISTRATION -> registrationScreen(changeScreen)
            }

        }
    }
}