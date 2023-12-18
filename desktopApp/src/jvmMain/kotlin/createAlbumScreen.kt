import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import io.ktor.client.engine.apache5.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun createAlbumScreen(goToMainScreen: () -> Unit) {
    val scope = rememberCoroutineScope()
    val apiClient = ApiClientLocal.current
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(200, 255, 255))
    ) {
        Column(
            Modifier.width(145.dp)
                .background(Color(75, 255, 255)).fillMaxHeight()
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = { goToMainScreen() }, modifier = Modifier.height(40.dp).width(135.dp)
                ) {
                    Text("Home", fontSize = TextUnit(1f, TextUnitType.Em))
                }
            }
        }

        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight().fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            var name by remember { mutableStateOf("") }
            TextField(
                name, onValueChange = { text -> name = text },
                modifier = Modifier.background(Color.Cyan).width(300.dp),
                placeholder = { Text("Enter album name") },
            )
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.width(300.dp)) {
                Button(onClick = {
                    createAlbum(apiClient, name, scope)
                    goToMainScreen()
                }, modifier = Modifier.height(40.dp).width(135.dp)) {
                    Text("Create album", fontSize = TextUnit(1f, TextUnitType.Em))
                }
            }
        }
    }
}

fun createAlbum(apiClient: ApiClient<Apache5EngineConfig>, name: String, scope: CoroutineScope) {
    scope.launch { apiClient.createAlbum(name) }
}