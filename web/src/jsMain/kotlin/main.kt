
import kotlinx.browser.document
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.button
import react.dom.div
import react.dom.p
import react.dom.render

fun main(): Unit = run {
    render(document.getElementById("root")) {
        child(App::class) {  }
    }
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class App : RComponent<RProps, RState>() {
    override fun RBuilder.render() {
        div {
            child(functionalComponent<RProps> {
                val (count, setCount) = useState(0)
                button {
                    attrs.onClickFunction = {setCount(count+1)}
                    +"press me!"
                    +"$count"
                }
            })
            child(StringList::class) {
                attrs.lines = listOf("a", "b", "c")
            }
        }
    }
}

external interface StringListProps : RProps {
    var lines: List<String>
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class StringList : RComponent<StringListProps, RState>() {
    override fun RBuilder.render() {
        for (line in props.lines) {
            p {
                +line
            }
        }
    }
}