import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun sharingScreen(goToMain: () -> Unit) {
    val apiClient = ApiClientLocal.current
    val users by produceState(listOf()) {
        this.value = apiClient.getUsers().filter { apiClient.getMe().id != it.id }
    }
    Row {
        Column(Modifier) {
            Row(modifier = Modifier.weight(1f)) {
                Button(onClick = { goToMain() }, Modifier.width(100.dp)) {
                    Text("Home")
                }
            }
        }
        BoxWithConstraints(modifier = Modifier.fillMaxHeight().fillMaxWidth()) {
            val width = maxWidth
            Column(
                modifier = Modifier.fillMaxHeight().fillMaxWidth().background(Color.Cyan)
            ) {
                for (user in users) {
                    Row(modifier = Modifier.weight(1f)) {
                        Text(text = user.nickname, Modifier.weight(10f).padding(vertical = 0.dp, horizontal = 10.dp))
                        Button(onClick = {}, Modifier.width(150.dp).padding(vertical = 0.dp, horizontal = 10.dp)) {
                            Text("Поделиться")
                        }
                        Button(onClick = {}, Modifier.width(200.dp).padding(vertical = 0.dp, horizontal = 10.dp)) {
                            Text("Закрыть")
                        }
                    }
                }
            }
        }
    }
}