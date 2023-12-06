import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dto.AlbumApiDto
import io.ktor.client.engine.apache5.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import javax.swing.JFileChooser
import kotlin.math.round

@Composable
fun mainScreen(logout: () -> Unit, goToAlbums: () -> Unit) {
    val apiClient = ApiClientLocal.current
    val scope = rememberCoroutineScope()
    var trigger by remember { mutableStateOf(false) }
    var currentAlbum by remember { mutableStateOf<AlbumApiDto?>(null, neverEqualPolicy()) }
    val setCurrentAlbum = { newAlbum: AlbumApiDto ->
        currentAlbum = newAlbum
    }

    LaunchedEffect(null) {
        currentAlbum = apiClient.getRootAlbum()
    }

    val logoutButtonOnClick: () -> Unit = {
        scope.launch {
            apiClient.logout()
        }
        logout()
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
            Row(modifier = Modifier.weight(1f)) {
                Button(
                    onClick = goToAlbums,
                    Modifier.width(100.dp)
                ) {
                    Text("Albums")
                }
            }
            Row(modifier = Modifier.weight(10f), verticalAlignment = Alignment.Bottom) {
                Button(onClick = { logoutButtonOnClick() }, Modifier.width(100.dp)) {
                    Text("Log out")
                }
            }
        }
        albumDisplay(currentAlbum, setCurrentAlbum, trigger)
    }
}

@Composable
fun albumDisplay(albumProp: AlbumApiDto?, setCurrentAlbum: (AlbumApiDto) -> Unit, trigger:Boolean) {
    val apiClient = ApiClientLocal.current
    var list by remember(albumProp) { mutableStateOf(listOf<Long>()) }

    LaunchedEffect(albumProp, trigger) {
        if (albumProp != null) {
            apiClient.getPhotoIdsByAlbum(albumProp.id).run {
                list = this
            }
        }
    }
    BoxWithConstraints(modifier = Modifier.fillMaxHeight().fillMaxWidth()) {
        val width = maxWidth
        val step = roundFloor(width / 200.dp)
        val needScroll = 200.dp.times(roundCeil(list.size.toFloat() / step)) > maxHeight;
        Column(
            modifier = Modifier.fillMaxHeight().fillMaxWidth().background(Color.Cyan).verticalScroll(
                rememberScrollState(), needScroll
            )
        ) {
            if (albumProp != null) {
                albumSelect(albumProp, setCurrentAlbum)
                for (i in list.indices step step) {
                    makePhotoCardRow(list, i, step, width)
                }
            }
        }
    }
}

@Composable
fun albumSelect(defaultAlbum: AlbumApiDto, setCurrentAlbum: (AlbumApiDto) -> Unit) {
    val apiClient = ApiClientLocal.current
    val options by produceState<List<AlbumApiDto>>(listOf()) {
        value = apiClient.getAccessibleAlbums()
    }
    var exp by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(defaultAlbum) }

    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
        Box(modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.TopStart)) {
            Text(
                selectedOption.albumName, modifier = Modifier.fillMaxWidth().clickable(onClick = { exp = true }).background(
                    Color.Gray
                )
            )
            DropdownMenu(expanded = exp, onDismissRequest = { exp = false }) {
                options.forEach { s ->
                    DropdownMenuItem(onClick = {
                        setCurrentAlbum(selectedOption)
                        selectedOption = s
                        exp = false
                    }) {
                        Text(text = s.albumName)
                    }
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

@Composable
fun PhotoCard(id: Long) {
    val apiClient = ApiClientLocal.current
    val painter by produceState(ImageBitmap(1000, 1000), id) {
        val bytes = apiClient.getPhotoById(id, true).content
        this.value = org.jetbrains.skia.Image.makeFromEncoded(bytes).toComposeImageBitmap()
    }
    val visible by remember { mutableStateOf(true) }
    Box(modifier = Modifier.size(200.dp)) {
        if (visible)
            Image(
                BitmapPainter(painter),
                null,
                modifier = Modifier.background(Color.Black).size(200.dp).clickable {
                    loadImage(id, apiClient)
                },
                contentScale = ContentScale.FillBounds,
            )
        else
            Text("$id")
    }
}

fun logOut(toggleLogout: (ActiveScreen) -> Unit) {
    toggleLogout(ActiveScreen.LOGIN)
}

fun homeButton() {

}

fun loadImage(id: Long, apiClient: ApiClient<Apache5EngineConfig>) {
    val fileChooser = JFileChooser()
    val value = fileChooser.showSaveDialog(null)
    val scope = CoroutineScope(Dispatchers.Default)
    if (value == JFileChooser.APPROVE_OPTION) {
        scope.launch {
            val bytes = apiClient.getPhotoById(id, false).content
            val stream = FileOutputStream(fileChooser.selectedFile)
            stream.write(bytes)
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
