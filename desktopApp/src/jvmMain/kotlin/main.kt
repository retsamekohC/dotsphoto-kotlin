@file:OptIn(ExperimentalEncodingApi::class)

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.ktor.client.*
import io.ktor.client.engine.apache5.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.skia.Image.Companion.makeFromEncoded
import javax.swing.JFileChooser
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MainView()
    }
}


@Composable
fun MainView() = AppPreview()

@OptIn(ExperimentalEncodingApi::class)
@Preview
@Composable
fun AppPreview() {
    val myCoroutineScope = rememberCoroutineScope()
    MaterialTheme {
        Row {
            Column(Modifier) {
                Row(modifier = Modifier.weight(1f)) {
                    Button(onClick = { homeButton() }, Modifier.width(100.dp)) {
                        Text("Home")
                    }
                }
                Row(modifier = Modifier.weight(1f)) {
                    Button(onClick = { uploadButton() }, Modifier.width(100.dp)) {
                        Text("Upload")
                    }
                }
                Row(modifier = Modifier.weight(10f), verticalAlignment = Alignment.Bottom) {
                    Button(onClick = { logOut() }, Modifier.width(100.dp)) {
                        Text("Log out")
                    }
                }
                //getApiClientInstance().getRootAlbumPhotoIds();
            }
            Column(modifier = Modifier.fillMaxHeight().fillMaxWidth().background(Color.Cyan)) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    val list = ApiClient.getRootAlbumPhotoIds()

                    for (i in list) {
                        var painter by remember {
                            mutableStateOf(ImageBitmap(1000, 1000))
                        }
                        myCoroutineScope.launch {
                            val pic = loadPicture(i)
                            painter = pic
                        }
//                        LaunchedEffect(Unit) {
//                            val pic = loadPicture("", i)
//                            painter = pic
//                        }
                        Image(
                            BitmapPainter(painter),
                            null,
                            modifier = Modifier.background(Color.Black).size(300.dp),
                            contentScale = ContentScale.Fit,
                        )
                    }
                }
            }
        }
    }
}


fun homeButton() {

}

fun uploadButton() {
    val fileChooser = JFileChooser()
    val value = fileChooser.showOpenDialog(null)
    val scope = CoroutineScope(Dispatchers.Default)
    if (value == JFileChooser.APPROVE_OPTION) {
        scope.launch {
            ApiClient.postPhotoToRootAlbum(fileChooser.selectedFile.readBytes())
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
suspend fun loadPicture(id: Long): ImageBitmap {
    val ans = ApiClient.getPhotoById(id)
    val bytes = Base64.decode(ans)

    @Suppress("UNUSED_VARIABLE") val httpClient = HttpClient(Apache5) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                }
            )
        }
    }

    return makeFromEncoded(bytes).toComposeImageBitmap()
}

fun logOut() {

}
