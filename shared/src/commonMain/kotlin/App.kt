import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.ktor.util.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        var greetingText by remember { mutableStateOf("Hello, World!") }
        var showImage by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                greetingText = "Hello, ${getPlatformName()}"
                showImage = !showImage
            }) {
                Text(greetingText)
            }
            AnimatedVisibility(showImage) {
                Image(
                    painterResource("compose-multiplatform.xml"),
                    null
                );

            }
            getApiClientInstance().getRootAlbumPhotoIds()
        }
    }
}

fun getApiClientInstance(): ApiClient {
    return ApiClient();
}

class ApiClient {
    companion object {
        val rootPhotoMap = mutableMapOf(
            1L to "",
            2L to "",
            3L to "",
            4L to "",
            5L to ""
        )
        suspend fun getRootAlbumPhotoIds() : List<Long> {
            return listOf(1,2,3,4,5)
        }

        suspend fun getPhotoById(id: Long) : String {
            return rootPhotoMap[id]?:""
        }

        suspend fun postPhotoToRootAlbum(photoBlob: ByteArray) {
            val maxId = rootPhotoMap.keys.max();
            rootPhotoMap.put(maxId+1, photoBlob.encodeBase64())
        }
    }
}

expect fun getPlatformName(): String