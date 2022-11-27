package render

import dto.*
import utils.BarycentricCalculator
import kotlin.math.pow
import kotlin.math.sqrt

// Матрицы моё слабое место. Я не понял ни-хре-на
// (кроме того, что одно это матрица поворота и другое матрица смещения) и тупо переписал с хабра тот кусок -_-
// для норм понимания почитать НЕ уставшим https://habr.com/ru/post/248611/
class PolygonsRenderer(
    private val canvas: Canvas,
    private val xMult: Double = 1.0,
    private val yMult: Double = 1.0,
    private val zMult: Double = 1.0,
    private val xShift: Double = .0,
    private val yShift: Double = .0,
    private val zShift: Double = .0,
    private val cameraVector: ThreeVector = ThreeVector(.0, .0, 1.0 * zMult),
) {

    //TODO разобраться с координатами реальными и эрканными. Даблы везде не есть хорошо
    private val zBuffer = mutableMapOf<Pair<Int, Int>, Double>()

    companion object {

        const val STEP = 1

        val WHITE_RGB = RGBAColor(255, 255, 255, 0)

    }

    fun render(polygons: List<Polygon>) {
        polygons.forEach {
            drawPoligon(it)
        }
    }

    private fun drawPoligon(polygon: Polygon) {
        val triangleScaled = polygon.triangle.scale(xMult, yMult, zMult).shift(xShift, yShift, zShift)
        val normalCos = getNormalCosinus(triangleScaled, cameraVector)
        if (normalCos < 0) {
            return
        }

        val color = getColor(normalCos)
        console.log("drawing $triangleScaled colored with $color")

        val focusArea = calcFocusArea(triangleScaled)
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
                paintPixelInTriangle(
                    pointCol = PointColored(curX, curY, color),
                    basis = triangleScaled
                )
            }
        }
    }

    private fun paintPixelInTriangle(pointCol: PointColored, basis: Triangle3D) {
        val barycentric = BarycentricCalculator.calcTriangleBarycentric(basis.proection2D, pointCol.point)
//        console.log("Barycentric $barycentric for point $point")
        if (barycentric.lambdaFirst >= 0 && barycentric.lambdaSecond >= 0 && barycentric.lambdaThird >= 0) {
            val zBufferKey = pointCol.point.x.toInt() to pointCol.point.y.toInt()
            val currZBuffer = zBuffer[zBufferKey] ?: Double.POSITIVE_INFINITY
            val currZ = barycentric.lambdaFirst * basis.vertFirst.z + barycentric.lambdaSecond * basis.vertSecond.z +
                    barycentric.lambdaThird * basis.vertThird.z
            if (currZ < currZBuffer) {
                zBuffer[zBufferKey] = currZ
                canvas.drawPoint(
                    coord = pointCol
                )
            }
        }
    }

    private fun calcFocusArea(triangle: Triangle3D): FocusArea {
        val leftTopX = minOf(triangle.vertFirst.x, triangle.vertSecond.x, triangle.vertThird.x)
        val leftTopY = minOf(triangle.vertFirst.y, triangle.vertSecond.y, triangle.vertThird.y)
        val rightDownX = maxOf(triangle.vertFirst.x, triangle.vertSecond.x, triangle.vertThird.x)
        val rightDownY = maxOf(triangle.vertFirst.y, triangle.vertSecond.y, triangle.vertThird.y)

        return FocusArea(
            leftTop = Point(leftTopX, leftTopY),
            rightBottom = Point(rightDownX, rightDownY)
        )
    }

    private fun getColor(colorIntense: Double) = WHITE_RGB.scale(colorIntense)

    private fun getNormalCosinus(triangle: Triangle3D, cameraVector: ThreeVector): Double {
        val (x0, y0, z0) = triangle.vertFirst
        val (x1, y1, z1) = triangle.vertSecond
        val (x2, y2, z2) = triangle.vertThird

        val n0 = (y2 - y0) * (z1 - z0) - (z2 - z0) * (y1 - y0)
        val n1 = -((x2 - x0) * (z1 - z0) - (z2 - z0) * (x1 - x0))
        val n2 = (x2 - x0) * (y1 - y0) - (y2 - y0) * (x1 - x0)

        val nScalarWithCamera = n0 * cameraVector.x + n1 * cameraVector.y + n2 * cameraVector.z
        val normal = sqrt(n0.pow(2) + n1.pow(2) + n2.pow(2)) *
                sqrt(cameraVector.x.pow(2) + cameraVector.y.pow(2) + cameraVector.z.pow(2))
        //TODO убрать костыль!
        return -nScalarWithCamera / normal
    }

    private data class FocusArea(
        val leftTop: Point,
        val rightBottom: Point
    )

}
