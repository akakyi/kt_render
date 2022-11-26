package render

import dto.*
import utils.BarycentricCalculator
import kotlin.random.Random
import kotlin.random.nextInt

class PolygonsRenderer(
    private val canvas: Canvas,
    private val xMult: Double = 1.0,
    private val yMult: Double = 1.0,
    private val xShift: Double = .0,
    private val yShift: Double = .0
) {

    companion object {

        const val STEP = 1

    }

    fun render(polygons: List<Polygon>) {
        polygons.forEach {
            drawPoligon(it)
        }
    }

    private fun drawPoligon(polygon: Polygon) {
        val triangleProection = polygon.triangle.proection2D.scale(xMult, yMult).shift(xShift, yShift)
        val color = getColor()
        console.log("drawing $triangleProection colored with $color")

        if (canvas.canDrawTriangle(triangleProection, color)) {
            canvas.drawTriangle(triangleProection, color)
            return
        }

        val focusArea = calcFocusArea(triangleProection)
        val leftBordX = focusArea.leftTop.x
        val rightBordX = focusArea.rightBottom.x
        val bottomBordY = focusArea.leftTop.y
        val topBordY = focusArea.rightBottom.y

        generateSequence(
            seed = leftBordX,
            nextFunction = { if (it < rightBordX) it + STEP else null }
        ).forEach { curX ->
            generateSequence(
                seed = bottomBordY,
                nextFunction = { if (it < topBordY) it + STEP else null }
            ).forEach { curY ->
                paintPixeInTriangle(
                    point = PointColored(curX, curY, color),
                    basis = triangleProection
                )
            }
        }
    }

    //TODO доработать. Слева снизу всегда точки вместо треугольников!
    private fun paintPixeInTriangle(point: PointColored, basis: Triangle) {
        val barycentric = BarycentricCalculator.calcTriangleBarycentric(basis, point.point)
//        console.log("Barycentric $barycentric for point $point")
        if (barycentric.lambdaFirst >= 0 && barycentric.lambdaSecond >= 0 && barycentric.lambdaThird >= 0) {
            canvas.drawPoint(
                coord = point
            )
        }
    }

    private fun calcFocusArea(triangle: Triangle): FocusArea {
        val leftTopX = minOf(triangle.vertFirst.x, triangle.vertSecond.x, triangle.vertThird.x)
        val leftTopY = minOf(triangle.vertFirst.y, triangle.vertSecond.y, triangle.vertThird.y)
        val rightTopX = maxOf(triangle.vertFirst.x, triangle.vertSecond.x, triangle.vertThird.x)
        val rightTopY = maxOf(triangle.vertFirst.x, triangle.vertSecond.x, triangle.vertThird.x)

        return FocusArea(
            leftTop = Point(leftTopX, leftTopY),
            rightBottom = Point(rightTopX, rightTopY)
        )
    }

    private fun getColor() = RGBAColor(
        redChannel = Random.nextInt(0..255).toShort(),
        greenChannel = Random.nextInt(0..255).toShort(),
        blueChannel = Random.nextInt(0..255).toShort(),
        alphaChannel = 0
    )

    private data class FocusArea(
        val leftTop: Point,
        val rightBottom: Point
    )

}