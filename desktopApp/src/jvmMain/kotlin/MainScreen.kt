import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.swing.JFileChooser

@Composable
fun mainScreen(toggleLogout:(ActiveScreen) -> Unit) {
    val apiClient = ApiClientLocal.current
    var trigger by remember { mutableStateOf(false) }
    val list by produceState(listOf<Long>(), trigger) {
        this.value = apiClient.getRootAlbumPhotoIds()
    }

    Row {
        Column(Modifier) {
            Row(modifier = Modifier.weight(1f)) {
                Button(onClick = { homeButton() }, Modifier.width(100.dp)) {
                    Text("Home")
                }
            }
            Row(modifier = Modifier.weight(1f)) {
                Button(onClick = { uploadButton { bytes: ByteArray, name: String ->
                    apiClient.postPhotoToRootAlbum(bytes, name)
                    trigger = !trigger
                } }, Modifier.width(100.dp)) {
                    Text("Upload")
                }
            }
            Row(modifier = Modifier.weight(10f), verticalAlignment = Alignment.Bottom) {
                Button(onClick = { logOut(toggleLogout) }, Modifier.width(100.dp)) {
                    Text("Log out")
                }
            }
        }
        Column(modifier = Modifier.fillMaxHeight().fillMaxWidth().background(Color.Cyan)) {
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                list.map {id ->
                    PhotoCard(id)
                }
            }
        }
    }
}

@Composable
fun PhotoCard(id: Long) {
    val apiClient = ApiClientLocal.current
    val painter by produceState(ImageBitmap(1000, 1000), id) {
        val bytes = apiClient.getPhotoById(id).content
        this.value = org.jetbrains.skia.Image.makeFromEncoded(bytes).toComposeImageBitmap()
    }

    Image(
        BitmapPainter(painter),
        null,
        modifier = Modifier.background(Color.Black).size(300.dp),
        contentScale = ContentScale.Fit,
    )
}

fun logOut(toggleLogout:(ActiveScreen) -> Unit) {
    toggleLogout(ActiveScreen.LOGIN)
}

fun homeButton() {

}

fun uploadButton(uploadPhoto: suspend (ByteArray, String) -> Unit) {
    val fileChooser = JFileChooser()
    val value = fileChooser.showOpenDialog(null)
    val scope = CoroutineScope(Dispatchers.Default)
    if (value == JFileChooser.APPROVE_OPTION) {
        scope.launch {
            uploadPhoto(fileChooser.selectedFile.readBytes(), fileChooser.selectedFile.name)
        }
    }
}

