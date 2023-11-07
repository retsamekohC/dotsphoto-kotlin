import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

actual fun getPlatformName(): String = "Desktop"

@Composable
fun MainView() = AppPreview()

@OptIn(ExperimentalResourceApi::class)
@Preview
@Composable
fun AppPreview() {
    MaterialTheme() {
        Row() {
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
                Row (horizontalArrangement = Arrangement.SpaceEvenly){
                    val list = ApiClient.getRootAlbumPhotoIds()
                    for (i in list) {
                        Image(
                            painterResource("${i}.jpg"),
                            null,
                            modifier = Modifier.size(300.dp)
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

}

fun logOut() {

}