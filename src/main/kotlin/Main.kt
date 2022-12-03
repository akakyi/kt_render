
import dto.Point3D
import dto.Polygon
import dto.ThreeVector
import dto.Triangle3D
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import reader.obj.ObjParser
import reader.obj.ObjStubLinesProvider
import render.PolygonsRenderer
import utils.ConsoleLogsProvider

//Basis: https://habr.com/ru/post/249467/ & Goshin, digital graphics lecturer from SSAU
fun main() {
    console.log("Test ebtb!")
    val mainWindow = document.getElementById("main_window") as HTMLCanvasElement

    val ctx = mainWindow.getContext("2d") as CanvasRenderingContext2D

    val polygonsRenderer = PolygonsRenderer(
        canvas = CanvasRenderingContext2DToCanvasAdapter(ctx),
        logsProvider = ConsoleLogsProvider(),
        xShift = 300.0,
        yShift = 600.0,
        zShift = 300.0,
        distanceToScreen = 1000.0
    )

    val objParser = ObjParser(ObjStubLinesProvider())
    val polygons = objParser.parse()
        .map { makeUpsideDown(it) }
        .map { it.triangle.scale(xCoefficient = 300.0, yCoefficient = 300.0, zCoefficient = 300.0) }
        .map { Polygon(it) }
    rerender(polygons, ThreeVector(.0, .0, 2000.0), polygonsRenderer, ctx)

    var lastWheelPosition = 10000.0
    window.onwheel = {
        lastWheelPosition += it.deltaY
        console.log("scrolling, y = ${lastWheelPosition / 5}")
        rerender(polygons, ThreeVector(.0, .0, lastWheelPosition / 5), polygonsRenderer, ctx)
    }
}

private fun rerender(
    polygons: List<Polygon>,
    cameraVector: ThreeVector,
    polygonsRenderer: PolygonsRenderer,
    context: CanvasRenderingContext2D
) {
    context.clearRect(.0, .0, 800.0, 800.0)
    polygonsRenderer.render(polygons, cameraVector)
}

private fun makeUpsideDown(polygon: Polygon): Polygon {
    console.log("get rotated, idiot")
    val triangle = polygon.triangle
    val result = Triangle3D(
        vertFirst = Point3D(
            x = -triangle.vertFirst.x,
            y = -triangle.vertFirst.y,
            z = triangle.vertFirst.z
        ),
        vertSecond = Point3D(
            x = -triangle.vertSecond.x,
            y = -triangle.vertSecond.y,
            z = triangle.vertSecond.z
        ),
        vertThird = Point3D(
            x = -triangle.vertThird.x,
            y = -triangle.vertThird.y,
            z = triangle.vertThird.z
        )
    )
    return Polygon(result)
}
