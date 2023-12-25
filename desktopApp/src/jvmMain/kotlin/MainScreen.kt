import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import dto.AlbumApiDto
import dto.PhotoApiDto
import io.ktor.client.engine.apache5.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import javax.swing.JFileChooser
import kotlin.math.round

@Composable
fun mainScreen(logout: () -> Unit, goToAlbums: () -> Unit, goToCreateAlbums: () -> Unit) {
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
        Column(
            Modifier.width(145.dp).background(Color(75, 255, 255)).fillMaxHeight().fillMaxWidth()
        ) {
            Row(modifier = Modifier.height(60.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = { homeButton() }, Modifier.width(135.dp).height(40.dp)) {
                    Text("Home", fontSize = TextUnit(1f, TextUnitType.Em))
                }
            }
            Row(modifier = Modifier.height(60.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = {
                    uploadButton { bytes: ByteArray, name: String ->
                        apiClient.postPhotoToRootAlbum(bytes, name)
                        trigger = !trigger
                    }
                }, Modifier.width(135.dp).height(40.dp)) {
                    Text("Upload", fontSize = TextUnit(1f, TextUnitType.Em))
                }
            }
            Row(modifier = Modifier.height(80.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = goToAlbums,
                    Modifier.width(135.dp).height(60.dp)
                ) {
                    Text("Share album", fontSize = TextUnit(1f, TextUnitType.Em), textAlign = TextAlign.Center)
                }
            }
            Row(Modifier.height(60.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = {
                    goToCreateAlbums()
                }, Modifier.width(135.dp).height(80.dp)) {
                    Text("Create album", fontSize = TextUnit(1f, TextUnitType.Em), textAlign = TextAlign.Center)
                }
            }
            Row(
                modifier = Modifier.height(60.dp).fillMaxWidth().weight(1.1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Button(onClick = { logoutButtonOnClick() }, Modifier.width(135.dp).height(40.dp)) {
                    Text("Log out", fontSize = TextUnit(1f, TextUnitType.Em))
                }
            }
        }
        albumDisplay(currentAlbum, setCurrentAlbum, trigger)
    }
}

@Composable
fun albumDisplay(albumProp: AlbumApiDto?, setCurrentAlbum: (AlbumApiDto) -> Unit, trigger: Boolean) {
    val apiClient = ApiClientLocal.current
    var list by remember(albumProp) { mutableStateOf(listOf<Long>()) }

    LaunchedEffect(albumProp, trigger) {
        if (albumProp != null) apiClient.getPhotoIdsByAlbum(albumProp.id).run {
            list = this
        }
    }
    val scope = rememberCoroutineScope()

    val needReload = {
        scope.launch {
            if (albumProp != null) {
                apiClient.getPhotoIdsByAlbum(albumProp.id).run {
                    list = this
                }
            }
        }
    }
    BoxWithConstraints(modifier = Modifier.fillMaxHeight().fillMaxWidth()) {
        val width = maxWidth
        val step = roundFloor(width / 200.dp)
        val needScroll = 200.dp.times(roundCeil(list.size.toFloat() / step)) > maxHeight
        Column(
            modifier = Modifier.fillMaxHeight().fillMaxWidth().background(Color(200, 255, 255)).verticalScroll(
                rememberScrollState(), needScroll
            )
        ) {
            if (albumProp != null) {
                albumSelect(albumProp, setCurrentAlbum)
                for (i in list.indices step step) {
                    makePhotoCardRow(list, i, step, width, needReload)
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
    LaunchedEffect(selectedOption) {
        setCurrentAlbum(selectedOption)
    }

    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Box {
                Text(
                    selectedOption.albumName,
                    fontSize = TextUnit(1.5f, TextUnitType.Em),
                    color = Color(110, 20, 239),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.clickable(onClick = { exp = true })
                )
                DropdownMenu(expanded = exp, onDismissRequest = { exp = false }) {
                    options.forEach { s ->
                        DropdownMenuItem(onClick = {
                            setCurrentAlbum(selectedOption)
                            selectedOption = s
                            exp = false
                        }) {
                            Text(
                                text = s.albumName,
                                fontSize = TextUnit(1f, TextUnitType.Em),
                                color = Color(110, 20, 239)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun makePhotoCardRow(list: List<Long>, offset: Int, number: Int, width: Dp, needReload: () -> Job) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.width(width)) {
        list.subList(offset, offset + minOf(number, list.size - offset)).map { id ->
            PhotoCard(id, needReload)
        }
    }
}

fun roundCeil(a: Float): Int {
    val x = round(a).toInt()
    if (x < a) return x + 1
    return x
}

fun roundFloor(a: Float): Int {
    val x = round(a).toInt()
    if (x > a) return x - 1
    return x
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoCard(id: Long, needReload: () -> Job) {
    val apiClient = ApiClientLocal.current
    val scope = rememberCoroutineScope()
    val photo by produceState<PhotoApiDto?>(null, id) {
        this.value = apiClient.getPhotoById(id, true)
    }
    val painter by produceState(ImageBitmap(1000, 1000), photo) {
        if (photo != null) {
            val bytes = photo!!.content
            this.value = org.jetbrains.skia.Image.makeFromEncoded(bytes).toComposeImageBitmap()
        }
    }
    var copyVisible by remember { mutableStateOf(false) }
    var moveVisible by remember { mutableStateOf(false) }
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
            Box {
                Image(
                    BitmapPainter(painter),
                    null,
                    modifier = Modifier.background(Color.Transparent)
                        .size(200.dp)
                        .onClick(
                            matcher = PointerMatcher.mouse(
                                PointerButton.Secondary
                            )
                        ) {
                            context = true
                        },
                    contentScale = ContentScale.FillBounds,
                )
                DropdownMenu(
                    expanded = context,
                    onDismissRequest = { context = false },

                    ) {
                    Text(
                        "Download photo",
                        modifier = Modifier.clickable {
                            downloadImage(id, apiClient)
                            context = false
                        },
                        fontSize = TextUnit(1f, TextUnitType.Em),
                        color = Color(110, 20, 239)
                    )
                    Text("Delete photo", modifier = Modifier.clickable {
                        scope.launch {
                            deletePhoto(
                                apiClient, photoId = id, albumId = photo!!.albumId
                            )
                            needReload()
                            context = false
                        }
                    }, fontSize = TextUnit(1f, TextUnitType.Em), color = Color(110, 20, 239))
                    MyButton(
                        moveVisible,
                        onButtonClick = { moveVisible = true },
                        close = { moveVisible = false },
                        "Move photo to album",
                        id
                    ) { photoId: Long, albumId: Long ->
                        scope.launch {
                            apiClient.movePhotoToAlbum(photoId, albumId)
                            needReload()
                            moveVisible = false
                            context = false
                        }
                    }
                    MyButton(
                        copyVisible,
                        onButtonClick = { copyVisible = true },
                        close = { copyVisible = false },
                        "Copy photo to album",
                        id
                    ) { photoId: Long, albumId: Long ->
                        scope.launch {
                            apiClient.copyPhotoToAlbum(photoId, albumId)
                            needReload()
                            copyVisible = false
                            context = false
                        }
                    }
                }
            }

        } else Text("$id", fontSize = TextUnit(1f, TextUnitType.Em), color = Color(110, 20, 239))
    }
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
            val bytes = apiClient.getPhotoById(id, false)
            val stream = FileOutputStream("${fileChooser.selectedFile}\\${bytes.fileName}")
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

suspend fun deletePhoto(apiClient: ApiClient<Apache5EngineConfig>, photoId: Long, albumId: Long) {
    apiClient.removePhotoFromAlbum(photoId, albumId)
}

@Composable
fun PhotoMenu(isOpen: Boolean, photoId: Long, close: () -> Unit, onClick: (photoId: Long, albumId: Long) -> Unit) {
    val apiClient = ApiClientLocal.current
    val albums by produceState<List<AlbumApiDto>>(emptyList(), isOpen) {
        this.value = apiClient.getMyAlbums()
    }
    DropdownMenu(expanded = isOpen, onDismissRequest = close) {
        for (i in albums) DropdownMenuItem(onClick = { onClick(photoId, i.id) }) {
            Text(i.albumName, fontSize = TextUnit(1f, TextUnitType.Em), color = Color(110, 20, 239))
        }
    }
}

@Composable
fun MyButton(
    copyVisible: Boolean,
    onButtonClick: () -> Unit,
    close: () -> Unit,
    text: String,
    id: Long,
    onClick: (photoId: Long, albumId: Long) -> Unit
) {
    Text(
        text,
        modifier = Modifier.clickable { onButtonClick() },
        fontSize = TextUnit(1f, TextUnitType.Em),
        color = Color(110, 20, 239)
    )
    PhotoMenu(copyVisible, id, close, onClick)
}