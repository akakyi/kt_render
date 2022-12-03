
import dto.*
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import reader.obj.ObjParser
import reader.obj.ObjStubLinesProvider
import render.PolygonsRenderer
import utils.EmptyLogsProvider
import utils.EmptyMeasureProvider

//Basis: https://habr.com/ru/post/249467/ & Goshin, digital graphics lecturer from SSAU
fun main() {
    console.log("Test ebtb!")
    val mainWindow = document.getElementById("main_window") as HTMLCanvasElement

    val ctx = mainWindow.getContext("2d") as CanvasRenderingContext2D

    val polygonsRenderer = PolygonsRenderer(
        canvas = CanvasRenderingContext2DToCanvasAdapter(ctx),
        logsProvider = EmptyLogsProvider(),
        measureProvider = EmptyMeasureProvider(),
        screenWidth = 800,
        screenHeight = 400,
        xShift = 300.0,
        yShift = 600.0,
        zShift = 300.0,
        distanceToScreen = 1000.0
    )

    var lastKnownAngle = Angle(0, 0, 0)
    var lastWheelPosition = 10000.0

    val objParser = ObjParser(ObjStubLinesProvider())
    val polygons = objParser.parse()
        .map { makeUpsideDown(it) }
        .map { it.triangle.scale(xCoefficient = 300.0, yCoefficient = 300.0, zCoefficient = 300.0) }
        .map { Polygon(it) }
    rerender(
        polygons = polygons,
        cameraVector = ThreeVector(.0, .0, lastWheelPosition / 5),
        angle = lastKnownAngle,
        polygonsRenderer = polygonsRenderer,
        context = ctx
    )

    window.onwheel = {
        lastWheelPosition += it.deltaY
        rerender(
            polygons = polygons,
            cameraVector = ThreeVector(.0, .0, lastWheelPosition / 5),
            angle = lastKnownAngle,
            polygonsRenderer = polygonsRenderer,
            context = ctx
        )
    }
    var lastKnownScreenX = 0
    var lastKnownScreenY = 0
    val leftButtonState: Short = 0
    mainWindow.onmousemove = {
        val screenX = it.screenX
        val screenY = it.screenY
        val buttonState = it.button
        val buttonsCount = it.buttons
        var pointChanged = false
        if (buttonsCount > 0 && buttonState == leftButtonState && (screenX % 5 == 0 || screenY % 5 == 0)) {
            console.log(it)
            if (screenX > lastKnownScreenX) {
                lastKnownAngle = Angle(
                    vertical = lastKnownAngle.vertical,
                    horizontal = lastKnownAngle.horizontal + 5,
                    depth = lastKnownAngle.depth
                )
                pointChanged = true
            }
            if (screenX < lastKnownScreenX) {
                lastKnownAngle = Angle(
                    vertical = lastKnownAngle.vertical,
                    horizontal = lastKnownAngle.horizontal - 5,
                    depth = lastKnownAngle.depth
                )
                pointChanged = true
            }
            lastKnownScreenX = screenX

            if (screenY > lastKnownScreenY) {
                lastKnownAngle = Angle(
                    vertical = lastKnownAngle.vertical,
                    horizontal = lastKnownAngle.horizontal,
                    depth = lastKnownAngle.depth + 5
                )
                pointChanged = true
            }
            if (screenY < lastKnownScreenX) {
                lastKnownAngle = Angle(
                    vertical = lastKnownAngle.vertical,
                    horizontal = lastKnownAngle.horizontal,
                    depth = lastKnownAngle.depth - 5
                )
                pointChanged = true
            }
            lastKnownScreenY = screenY

            if (pointChanged) {
                rerender(
                    polygons = polygons,
                    cameraVector = ThreeVector(.0, .0, lastWheelPosition / 5),
                    angle = lastKnownAngle,
                    polygonsRenderer = polygonsRenderer,
                    context = ctx
                )
            }
        }
    }
}

private fun rerender(
    polygons: List<Polygon>,
    cameraVector: ThreeVector,
    angle: Angle,
    polygonsRenderer: PolygonsRenderer,
    context: CanvasRenderingContext2D
) {
    context.clearRect(.0, .0, 800.0, 400.0)
    polygonsRenderer.render(polygons, cameraVector, angle)
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
