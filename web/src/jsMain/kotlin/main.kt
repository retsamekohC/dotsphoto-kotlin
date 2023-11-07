import kotlinx.browser.document
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.dom.HTMLInputElement
import org.w3c.files.File
import org.w3c.files.FileReader
import org.w3c.files.get
import react.*
import react.dom.div
import react.dom.img
import react.dom.input
import react.dom.render

external interface PhotoBoxProps: RProps {
    var photos: List<String>
}

@OptIn(DelicateCoroutinesApi::class)
fun main(): Unit = run {
    val app = functionalComponent<RProps> {
        val (photosState, setPhotoState) = useState<ArrayList<String>>(ArrayList())
        useEffect(emptyList()) {
            GlobalScope.launch {
                setPhotoState(ArrayList(ApiClient.getRootAlbumPhotoIds().map {id ->
                    ApiClient.getPhotoById(id)
                }))
            }
        }
        val processImage = {imageData:ByteArray ->
            GlobalScope.launch {
                ApiClient.postPhotoToRootAlbum(imageData)
                ApiClient.getRootAlbumPhotoIds().forEach {id ->
                    val copy = ArrayList(photosState)
                    copy.add(ApiClient.getPhotoById(id))
                    setPhotoState (copy)
                }
            }
        }

        val handleImageUpload = { fileList: List<File> ->
            fileList.forEach { file ->
                GlobalScope.launch {
                    val reader = FileReader()
                    reader.onload = {
                        GlobalScope.launch {
                            val res = reader.result as ArrayBuffer
                            processImage(res.asByteArray())
                        }
                    }
                    reader.readAsArrayBuffer(file)
                }
            }
        }
        val photoBox = functionalComponent<PhotoBoxProps> {
            div {
                for (photo in it.photos) {
                    img {
                        attrs {
                            src = "data:image/jpeg;base64,${photo}"
                        }
                    }
                }
            }
        }

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
            child(photoBox) {
                attrs.photos = photosState
            }
        }
    }
    render(document.getElementById("root")) {
        child(app)
    }
}

@Suppress("CAST_NEVER_SUCCEEDS")
fun ArrayBuffer?.asByteArray(): ByteArray? = this?.run { Int8Array(this) as ByteArray }

@Suppress("CAST_NEVER_SUCCEEDS")
fun ArrayBuffer.asByteArray(): ByteArray = this.run { Int8Array(this) as ByteArray }