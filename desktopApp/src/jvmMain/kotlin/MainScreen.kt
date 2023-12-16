import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.ktor.client.engine.apache5.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import javax.swing.JFileChooser
import kotlin.math.round

@Composable
fun mainScreen(toggleLogout: (ActiveScreen) -> Unit) {
    val apiClient = ApiClientLocal.current
    var trigger by remember { mutableStateOf(false) }
    val list by produceState(listOf<Long>(), trigger) {
        this.value = apiClient.getRootAlbumPhotoIds()
        listOf(-1L)
    }

    Row {
        Column(Modifier) {
            Row(modifier = Modifier.weight(1f)) {
                Button(onClick = { homeButton() }, Modifier.width(100.dp)) {
                    Text("Home")
                }
            }
            Row(modifier = Modifier.weight(1f)) {
                Button(onClick = {
                    uploadButton { bytes: ByteArray, name: String ->
                        apiClient.postPhotoToRootAlbum(bytes, name)
                        trigger = !trigger
                    }
                }, Modifier.width(100.dp)) {
                    Text("Upload")
                }
            }
            Row {
                Button(onClick = {
                    toggleLogout(ActiveScreen.CREATE_ALBUM)
                }, Modifier.width(100.dp)) {
                    Text("Create album")
                }
            }
            Row(modifier = Modifier.weight(10f), verticalAlignment = Alignment.Bottom) {
                Button(onClick = { logOut(toggleLogout) }, Modifier.width(100.dp)) {
                    Text("Log out")
                }
            }
        }
        BoxWithConstraints(modifier = Modifier.fillMaxHeight().fillMaxWidth()) {
            val width = maxWidth
            val step = roundFloor(width / 200.dp)
            val needScroll = 200.dp.times(roundCeil(list.size.toFloat() / step)) > maxHeight
            Column(
                modifier = Modifier.fillMaxHeight().fillMaxWidth().background(Color.Cyan).verticalScroll(
                    rememberScrollState(), needScroll
                )
            ) {

                for (i in list.indices step step) {
                    makePhotoCardRow(list, i, step, width)
                }
            }
        }
    }
}


@Composable
fun makePhotoCardRow(list: List<Long>, offset: Int, number: Int, width: Dp) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.width(width)) {
        list.subList(offset, offset + minOf(number, list.size - offset)).map { id ->
            PhotoCard(id)
        }
    }
}

fun roundCeil(a: Float): Int {
    val x = round(a).toInt()
    if (x < a)
        return x + 1
    return x
}

fun roundFloor(a: Float): Int {
    val x = round(a).toInt()
    if (x > a)
        return x - 1
    return x
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoCard(id: Long) {
    val apiClient = ApiClientLocal.current
    val painter by produceState(ImageBitmap(1000, 1000), id) {
        val bytes = apiClient.getPhotoById(id).content
        this.value = org.jetbrains.skia.Image.makeFromEncoded(bytes).toComposeImageBitmap()
    }
    val visible by remember { mutableStateOf(true) }
    var context by remember { mutableStateOf(false) }
    var pressOffset by remember { mutableStateOf(DpOffset.Zero) }
    var height by remember { mutableStateOf(0.dp) }
    var width by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    Box(modifier = Modifier.onSizeChanged {
        height = with(density) { it.height.toDp() }
        width = with(density) { it.width.toDp() }
    }.pointerInput(true) {

        detectTapGestures(onTap = {
            pressOffset = DpOffset(it.x.dp, it.y.dp)
        })

    }) {
        if (visible) {
            DropdownMenu(
                expanded = context,
                onDismissRequest = { context = false },
                offset = pressOffset.copy(y = pressOffset.y / 2 - height, x = pressOffset.x / 2 - width)
            ) {
                Text("Загрузить фотографию", modifier = Modifier.clickable { downloadImage(id, apiClient) })
                Text("Удалить фотографию", modifier = Modifier.clickable { deletePhoto() })
                Text("Переместить фотографию в другой альбом", modifier = Modifier.clickable { movePhoto() })
            }
            Image(
                BitmapPainter(painter),
                null,
                modifier = Modifier.background(Color.Black).size(200.dp).onClick(
                    matcher = PointerMatcher.mouse(
                        PointerButton.Secondary
                    )
                ) {
                    context = true
                },
                contentScale = ContentScale.FillBounds,
            )
        } else
            Text("$id")
    }
}

fun logOut(toggleLogout: (ActiveScreen) -> Unit) {
    toggleLogout(ActiveScreen.LOGIN)
}

fun homeButton() {

}

fun downloadImage(id: Long, apiClient: ApiClient<Apache5EngineConfig>) {
    val fileChooser = JFileChooser()
    fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    val value = fileChooser.showSaveDialog(null)
    val scope = CoroutineScope(Dispatchers.Default)
    if (value == JFileChooser.APPROVE_OPTION) {
        scope.launch {
            val bytes = apiClient.getPhotoById(id)
            val stream = FileOutputStream("${fileChooser.selectedFile}/${bytes.fileName}")
            stream.write(bytes.content)
            stream.flush()
            stream.close()
        }
    }
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

fun movePhoto() {

}

fun deletePhoto() {

}