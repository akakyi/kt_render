import dto.Point3D
import dto.Polygon
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
        xShift = 210.0,
        yShift = 210.0,
        zShift = 210.0
    )

    val objParser = ObjParser(ObjStubLinesProvider())
    console.log("get rotated, idiot")
    val polygons = objParser.parse().map { makeUpsideDown(it, Point3D(1.0, 1.0, 1.0)) }
    polygonsRenderer.render(polygons)

}

private fun makeUpsideDown(polygon: Polygon, rightBottomBorder: Point3D): Polygon {
    val triangle = polygon.triangle
    val result = Triangle3D(
        vertFirst = Point3D(
            x = rightBottomBorder.x - triangle.vertFirst.x,
            y = rightBottomBorder.y - triangle.vertFirst.y,
            z = rightBottomBorder.z - triangle.vertFirst.z
        ),
        vertSecond = Point3D(
            x = rightBottomBorder.x - triangle.vertSecond.x,
            y = rightBottomBorder.y - triangle.vertSecond.y,
            z = rightBottomBorder.z - triangle.vertSecond.z
        ),
        vertThird = Point3D(
            x = rightBottomBorder.x - triangle.vertThird.x,
            y = rightBottomBorder.y - triangle.vertThird.y,
            z = rightBottomBorder.z - triangle.vertThird.z
        )
    )
    return Polygon(result)
}
