import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun createAlbumScreen() {
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
            var name by remember { mutableStateOf("") }
            TextField(
                name, onValueChange = { text -> name = text },
                modifier = Modifier.background(Color.Cyan).width(300.dp),
                placeholder = { Text("Enter album name") },
            )
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.width(300.dp)) {
                Button(onClick = { createAlbum() }) {
                    Text("Create album")
                }
            }
        }
    }
}

fun createAlbum() {

}