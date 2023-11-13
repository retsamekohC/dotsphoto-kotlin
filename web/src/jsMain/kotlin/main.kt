import kotlinx.browser.document
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.css.properties.borderBottom
import kotlinx.css.properties.scale
import kotlinx.css.properties.transform
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.dom.HTMLInputElement
import org.w3c.files.File
import org.w3c.files.FileReader
import org.w3c.files.get
import react.*
import react.dom.render
import styled.*

external interface PhotoBoxProps : RProps {
    var photos: List<String>
}

object ComponentStyles : StyleSheet("ComponentStyles", isStatic = true) {
    val containerRowCenterSelfFill by css {
        width = LinearDimension("100%")
        display = Display.flex
        flex(1.0, 0.0, FlexBasis.auto)
/*
        gap = Gap("25px")
        flexDirection = FlexDirection.column
        minHeight = LinearDimension("100%")
        height = LinearDimension("100%")
        margin(LinearDimension("0"))
        padding(LinearDimension("0"))
*/
    }

    val sidebar by css {
        position = Position.sticky
        top = LinearDimension("25")
        display = Display.flex
        flexDirection = FlexDirection.column
        width = LinearDimension("100")
        height = LinearDimension("30")
        minWidth = LinearDimension("100")
        gap = Gap("10")
        backgroundColor = Color.white
        borderTopRightRadius = LinearDimension("15")
        borderBottomRightRadius = LinearDimension("15")
        padding = "25"
    }

    val sidebarItem by css {
        color = Color.darkMagenta
        display = Display.block
        fontSize = LinearDimension("24")
    }

    val sidebarItemInput by css {
        alignSelf = Align.start
        display = Display.block
        border = "block"
        background = "none"
        borderRadius = 5.px
        padding = "0"
        margin = "0"
        color = Color.darkMagenta
        cursor = Cursor.pointer
    }

    val sidebarItemLogoutButton by css {
        color = Color.white
        backgroundColor = Color.lightGreen
        border = "none"
        borderRadius = LinearDimension("5")
        padding(LinearDimension("7"))
    }

    val cardGallery by css {
        display = Display.flex
        flexGrow = 1.0
        flexWrap = FlexWrap.wrap
        justifyContent = JustifyContent.spaceBetween
        gap = Gap("10")
        marginRight = LinearDimension("25")
    }

    val image by css {
        height = LinearDimension("250")
        objectFit = ObjectFit.cover
        hover {
            transform { scale(1.05) }
        }
    }

    val header by css {
        display = Display.flex
        backgroundColor = Color.white
        boxSizing = BoxSizing.borderBox
        alignItems = Align.center
        justifyContent = JustifyContent.spaceAround
        margin = "0 0 10 0"
        borderBottom(LinearDimension.borderBox, BorderStyle.solid, Color.azure)
    }

    val headerName by css {
        fontWeight = FontWeight.w500
    }

    val headerNameEmphasis by css {
        color = Color.darkMagenta
    }

    val footer by css {

    }

    val contacts by css {

    }

    val contact by css {

    }

    val html by css {
        margin(LinearDimension("0"))
        padding(LinearDimension("0"))
        height = LinearDimension("100%")
        fontFamily = "Roboto Slab"
        fontWeight = FontWeight.w300
        backgroundColor = Color.aquamarine
        opacity = 1
        put("background-image", "radial-gradient(var(--light-mint) 2px, transparent 2px), radial-gradient(var(--light-mint) 2px, var(--lightest-mint) 2px)")
        backgroundSize = "80px 80px"
        backgroundPosition = "0 0 40px 40px"
        backgroundAttachment = BackgroundAttachment.fixed
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun main(): Unit = run {
    val app = functionalComponent<RProps> {
        val (photosState, setPhotoState) = useState<ArrayList<String>>(ArrayList())
        useEffect(emptyList()) {
            GlobalScope.launch {
                setPhotoState(ArrayList(ApiClient.getRootAlbumPhotoIds().map { id ->
                    ApiClient.getPhotoById(id)
                }))
            }
        }

        val processImage = { imageData: ByteArray ->
            GlobalScope.launch {
                ApiClient.postPhotoToRootAlbum(imageData)
                ApiClient.getRootAlbumPhotoIds().forEach { id ->
                    val copy = ArrayList(photosState)
                    copy.add(ApiClient.getPhotoById(id))
                    setPhotoState(copy)
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
            styledDiv {
                for (photo in it.photos) {
                    styledImg {
                        attrs {
                            src = "data:image/jpeg;base64,${photo}"
                        }
                        css { +ComponentStyles.image }
                    }
                }
                css { +ComponentStyles.cardGallery }
            }
        }

        styledDiv {
            styledDiv {
                styledH1 {
                    css { +ComponentStyles.headerName }
                    +"МНОГОТОЧИЕ "
                    styledSpan {
                        css { +ComponentStyles.headerNameEmphasis }
                        +"ФОТО"
                    }
                }
                css { +ComponentStyles.header }
            }

            styledDiv {
                styledDiv {
                    styledA(href = "") {

                        css { +ComponentStyles.sidebarItem }
                        +"Home"
                    }
                    styledA(href = "404") {
                        css { +ComponentStyles.sidebarItem }
                        +"404"
                    }

                    styledInput(type = InputType.file) {
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
                        css { +ComponentStyles.sidebarItemInput }
                    }

                    styledA(href = "") {
                        css { +ComponentStyles.sidebarItemLogoutButton }
                        +"Log out"
                    }

                    css { +ComponentStyles.sidebar }
                }

                child(photoBox) {
                    attrs.photos = photosState
                }

                css { +ComponentStyles.containerRowCenterSelfFill }
            }

            styledDiv {
                styledDiv {
                    css { +ComponentStyles.contacts }
                }
                css { +ComponentStyles.footer }
            }
            css {
                +ComponentStyles.html
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