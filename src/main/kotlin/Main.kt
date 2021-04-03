import kotlinx.browser.document
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

fun main() {
    console.log("Test ebtb!")
    val mainWindow = document.getElementById("main_window") as HTMLCanvasElement

    var ctx = mainWindow.getContext("2d") as CanvasRenderingContext2D
    ctx.fillStyle = "green"
    ctx.fillRect(10.0, 10.0, 100.0, 100.0)
}