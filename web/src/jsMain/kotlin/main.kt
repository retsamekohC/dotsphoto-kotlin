import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.asList
import org.w3c.files.*
import org.w3c.xhr.ProgressEvent
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.img
import react.dom.input
import react.dom.render
import kotlin.coroutines.suspendCoroutine

fun main(): Unit = run {
    render(document.getElementById("root")) {
        child(App::class) {  }
    }
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class App : RComponent<RProps, RState>() {

    private val scope = MainScope()
    private var photos = emptyList<String>()

    private suspend fun processImage(imageData: ByteArray) {
        ApiClient.postPhotoToRootAlbum(imageData)
        reloadPage()
    }

    private fun handleImageUpload(fileList: List<File>) {
        fileList.forEach {file ->
            scope.launch {
                val reader = FileReader()
                reader.onload = {
                    scope.launch {
                        val res = reader.result as ArrayBuffer
                        processImage(res.asByteArray())
                    }
                }
                reader.readAsArrayBuffer(file)
            }
        }
    }

    private fun reloadPage() {
        window.location.reload()
    }

    private suspend fun loadPhotos() {
        val photoIds = ApiClient.getRootAlbumPhotoIds()
        val loadedPhotos = mutableListOf<String>()

        for (photoId in photoIds) {
            val photoData = ApiClient.getPhotoById(photoId)
            loadedPhotos.add(photoData)
        }

        photos = loadedPhotos
    }

    override fun RBuilder.render() {
        div {
            input(type = InputType.file) {
                attrs {
                    onChangeFunction = { event ->
                        val files = (event.target as HTMLInputElement).files!!
                        val fileList = ArrayList<File>()
                        for (i in 0..<files.length) {
                            fileList.add(files[i]!!)
                        }
                        handleImageUpload(fileList)
                    }
                    accept = "image/*"
                }
            }

            div {
                for (photoData in photos) {
                    img {
                        attrs {
                            src = "data:image/jpeg;base64,${photoData}"
                        }
                    }
                }
            }
        }
    }

    init {
        scope.launch {
            loadPhotos()
        }
    }
}

@Suppress("CAST_NEVER_SUCCEEDS")
fun ArrayBuffer?.asByteArray(): ByteArray? = this?.run { Int8Array(this) as ByteArray }

@Suppress("CAST_NEVER_SUCCEEDS")
fun ArrayBuffer.asByteArray(): ByteArray = this.run { Int8Array(this) as ByteArray }