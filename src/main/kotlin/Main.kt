import kotlinx.browser.document
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

fun main() {
    console.log("Test ebtb!")
    val mainWindow = document.getElementById("main_window") as HTMLCanvasElement

    var ctx = mainWindow.getContext("2d") as CanvasRenderingContext2D
    ctx.fillStyle = "green"
    ctx.fillRect(10.0, 10.0, 100.0, 100.0)
    ctx.fillStyle = "rgb(255, 255, 255)"
    ctx.fillRect(60.0, 60.0, 1.0, 1.0)
    ctx.moveTo(50.0, 50.0)
    ctx.lineTo(10.0, 10.0)

//    val test = listOf("a", "b", "c")
//        .map { it + "1" }
//        .onEach { console.log(it) }

}