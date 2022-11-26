import kotlinx.browser.document
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import reader.obj.ObjParser
import reader.obj.ObjStubLinesProvider
import render.PolygonsRenderer

//Basis: https://habr.com/ru/post/249467/; Goshin, digital graphics lecturer from SSAU
fun main() {
    console.log("Test ebtb!")
    val mainWindow = document.getElementById("main_window") as HTMLCanvasElement

    var ctx = mainWindow.getContext("2d") as CanvasRenderingContext2D
    ctx.fillStyle = "black"
    ctx.fillRect(10.0, 10.0, 200.0, 200.0)

    ctx.beginPath()
    val polygonsRenderer = PolygonsRenderer(
        canvas = CanvasRenderingContext2DToCanvasAdapter(ctx),
        xMult = 100.0,
        yMult = 100.0,
        xShift = 100.0,
        yShift = 100.0
    )

    val objParser = ObjParser(ObjStubLinesProvider())
    val polygons = objParser.parse()
    polygonsRenderer.render(polygons)
    ctx.stroke()

}