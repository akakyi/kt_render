import dto.Point3D
import dto.Polygon
import dto.ThreeVector
import dto.Triangle3D
import kotlinx.browser.document
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import reader.obj.ObjParser
import reader.obj.ObjStubLinesProvider
import render.PolygonsRenderer

//Basis: https://habr.com/ru/post/249467/ & Goshin, digital graphics lecturer from SSAU
fun main() {
    console.log("Test ebtb!")
    val mainWindow = document.getElementById("main_window") as HTMLCanvasElement

    val ctx = mainWindow.getContext("2d") as CanvasRenderingContext2D

    val polygonsRenderer = PolygonsRenderer(
        canvas = CanvasRenderingContext2DToCanvasAdapter(ctx),
        xMult = 300.0,
        yMult = 300.0,
        zMult = 300.0,
        xShift = 300.0,
        yShift = 300.0,
        zShift = 300.0,
        cameraVector = ThreeVector(.0, .0, -800.0)
    )

    val objParser = ObjParser(ObjStubLinesProvider())
    val polygons = objParser.parse().map { makeUpsideDown(it) }
    polygonsRenderer.render(polygons)
}

private fun makeUpsideDown(polygon: Polygon): Polygon {
    console.log("get rotated, idiot")
    val triangle = polygon.triangle
    val result = Triangle3D(
        vertFirst = Point3D(
            x = -triangle.vertFirst.x,
            y = -triangle.vertFirst.y,
            z = -triangle.vertFirst.z
        ),
        vertSecond = Point3D(
            x = -triangle.vertSecond.x,
            y = -triangle.vertSecond.y,
            z = -triangle.vertSecond.z
        ),
        vertThird = Point3D(
            x = -triangle.vertThird.x,
            y = -triangle.vertThird.y,
            z = -triangle.vertThird.z
        )
    )
    return Polygon(result)
}
