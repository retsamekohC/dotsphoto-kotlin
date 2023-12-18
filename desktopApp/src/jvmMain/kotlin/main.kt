@file:OptIn(ExperimentalEncodingApi::class)

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.ktor.client.engine.apache5.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        val goToLogin = { activeScreen = ActiveScreen.LOGIN }
        val goToMain = { activeScreen = ActiveScreen.MAIN }
        val goToRegistration = { activeScreen = ActiveScreen.REGISTRATION }
        val goToAlbumsScreen = { activeScreen = ActiveScreen.ALBUMS }
        val goToCreateAlbumScreen = {activeScreen = ActiveScreen.CREATE_ALBUM}
        CompositionLocalProvider(ApiClientLocal provides ApiClient(Apache5)) {
            CompositionLocalProvider(CoroutineScopeLocal provides rememberCoroutineScope()) {
                MaterialTheme {
                    when (activeScreen) {
                        ActiveScreen.LOGIN -> loginScreen(goToMain, goToRegistration)
                        ActiveScreen.MAIN -> mainScreen(goToLogin, goToAlbumsScreen, goToCreateAlbumScreen)
                        ActiveScreen.REGISTRATION -> registrationScreen(goToLogin)
                        ActiveScreen.ALBUMS -> albumsScreen(goToMain)
                        ActiveScreen.CREATE_ALBUM -> createAlbumScreen(goToMain)
                    }
                }
            }
        }
    }
}