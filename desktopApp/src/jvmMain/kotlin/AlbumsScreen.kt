import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import dto.AlbumApiDto
import dto.UserApiDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun albumsScreen(goToMain: () -> Unit) {
    val apiClient = ApiClientLocal.current
    val albumsList by produceState(listOf<AlbumApiDto>()) {
        this.value = apiClient.getMyAlbums()
    }
    var isDialogOpen by remember { mutableStateOf(false) }
    val closeDialog = { isDialogOpen = false }
    val openDialog = { isDialogOpen = true }
    var dialogAlbum by remember { mutableStateOf(0L) }
    Row {
        Column(Modifier) {
            Row(modifier = Modifier.weight(1f)) {
                Button(onClick = { goToMain() }, Modifier.width(100.dp)) {
                    Text("Home")
                }
            }
        }
        BoxWithConstraints(modifier = Modifier.fillMaxHeight().fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxHeight().fillMaxWidth().background(Color.Cyan)
            ) {
                for (album in albumsList) {
                    Row(modifier = Modifier.weight(1f)) {
                        Button(onClick = {
                            dialogAlbum = album.id
                            openDialog()
                        }, Modifier.width(150.dp).height(150.dp).padding(10.dp)) {
                            Text(album.albumName)
                        }
                    }
                }
            }
        }
        DialogWindow(onCloseRequest = closeDialog, visible = isDialogOpen, content = @Composable {
            val options by produceState<List<UserApiDto>>(listOf()) {
                value = apiClient.getUsers()
            }
            var exp by remember { mutableStateOf(false) }
            var selectedOption by remember { mutableStateOf<UserApiDto?>(null) }
            Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                Box(modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.TopStart)) {
                    Text(selectedOption?.nickname ?: "",modifier = Modifier.fillMaxWidth().clickable(onClick = { exp = true }).background(
                        Color.Gray))
                    DropdownMenu(expanded = exp, onDismissRequest = { exp = false }) {
                        options.forEach { s ->
                            DropdownMenuItem(onClick = {
                                selectedOption = s
                                exp = false
                            }) {
                                Text(text = s.nickname)
                            }
                        }
                    }
                }
                Button(
                    onClick = {
                        val scope = CoroutineScope(Dispatchers.Default)
                        scope.launch {
                            val user = selectedOption
                            if (user != null && apiClient.shareAlbum(dialogAlbum, user.id )) {
                                closeDialog()
                            }
                        }
                    }
                ) {
                    Text(text = "Submit")
                }
            }
        })
    }
}