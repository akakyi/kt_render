package render

import dto.*
import utils.BarycentricCalculator
import kotlin.math.pow
import kotlin.math.sqrt

// Матрицы моё слабое место. Я не понял ни-хре-на
// (кроме того, что одно это матрица поворота и другое матрица смещения) и тупо переписал с хабра тот кусок -_-
// для норм понимания почитать НЕ уставшим https://habr.com/ru/post/248611/
// P.S. немного к статье выше: то, что чел называет камерой, по факту фонарик. А то, на что проецирует фонарик,
// по факту и есть матрица камеры, но при этом фонарик как бы просвечивает модель насквозь. Сложно визуализировать -_-
class PolygonsRenderer(
    private val canvas: Canvas,
    private val xShift: Double = .0,
    private val yShift: Double = .0,
    private val zShift: Double = .0,
    private val distanceToScreen: Double = 1000.0
) {


    companion object {

        const val STEP = 1

        val WHITE_RGB = RGBAColor(255, 255, 255, 0)

    }

    fun render(
        polygons: List<Polygon>,
        cameraVector: ThreeVector = ThreeVector(.0, .0, 1.0)
    ) {
        //TODO разобраться с координатами реальными и эрканными. Даблы везде не есть хорошо
        val zBuffer = mutableMapOf<Pair<Int, Int>, Double>()
        polygons.forEach {
            drawPolygon(it, cameraVector, zBuffer)
        }
    }

    private fun drawPolygon(
        polygon: Polygon,
        cameraVector: ThreeVector,
        zBuffer: MutableMap<Pair<Int, Int>, Double>
    ) {
        val triangleScaled = polygon.triangle.shift(xShift, yShift, zShift)
        console.log("drawing $triangleScaled")

        val distortedTriangle = getCameraPositionDistortion(triangleScaled, cameraVector)

        val normalCos = getNormalCosinus(distortedTriangle, cameraVector)
        console.log("distorted before drawing: $distortedTriangle, normal: $normalCos")
        if (normalCos < 0) {
            return
        }

        val color = getColor(normalCos)
        console.log("Will draw, colored with $color")

        val focusArea = calcFocusArea(distortedTriangle)
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
                    basis = distortedTriangle,
                    zBuffer = zBuffer
                )
            }
        }
    }

    private fun paintPixelInTriangle(
        pointCol: PointColored,
        basis: Triangle3D,
        zBuffer: MutableMap<Pair<Int, Int>, Double>
    ) {
        val barycentric = BarycentricCalculator.calcTriangleBarycentric(basis.proection2D, pointCol.point)
//        console.log("Barycentric $barycentric for point ${pointCol.point}")
        if (barycentric.lambdaFirst < 0 || barycentric.lambdaSecond < 0 || barycentric.lambdaThird < 0) {
            return
        }

        val zBufferKey = pointCol.point.x.toInt() to pointCol.point.y.toInt()
        val currZBuffer = zBuffer[zBufferKey] ?: Double.NEGATIVE_INFINITY
        val currZ = barycentric.lambdaFirst * basis.vertFirst.z + barycentric.lambdaSecond * basis.vertSecond.z +
                barycentric.lambdaThird * basis.vertThird.z
        if (currZ > currZBuffer) {
            zBuffer[zBufferKey] = currZ
            canvas.drawPoint(
                coord = pointCol
            )
        }
    }

    private fun getCameraPositionDistortion(
        triangle: Triangle3D,
        cameraVector: ThreeVector
    ): Triangle3D {
//        return Triangle3D(
//            vertFirst = Point3D(
//                x = triangle.vertFirst.x / (1 - triangle.vertFirst.z / cameraVector.z),
//                y = triangle.vertFirst.y / (1 - triangle.vertFirst.z / cameraVector.z),
//                z = triangle.vertFirst.z / (1 - triangle.vertFirst.z / cameraVector.z)
//            ),
//            vertSecond = Point3D(
//                x = triangle.vertSecond.x / (1 - triangle.vertSecond.z / cameraVector.z),
//                y = triangle.vertSecond.y / (1 - triangle.vertSecond.z / cameraVector.z),
//                z = triangle.vertSecond.z / (1 - triangle.vertSecond.z / cameraVector.z)
//            ),
//            vertThird = Point3D(
//                x = triangle.vertThird.x / (1 - triangle.vertThird.z / cameraVector.z),
//                y = triangle.vertThird.y / (1 - triangle.vertThird.z / cameraVector.z),
//                z = triangle.vertThird.z / (1 - triangle.vertThird.z / cameraVector.z)
//            )
//        )
        return Triangle3D(
            vertFirst = Point3D(
                x = triangle.vertFirst.x * distanceToScreen / (cameraVector.z - triangle.vertFirst.z),
                y = triangle.vertFirst.y * distanceToScreen / (cameraVector.z - triangle.vertFirst.z),
                z = triangle.vertFirst.z * distanceToScreen / (cameraVector.z - triangle.vertFirst.z)
            ),
            vertSecond = Point3D(
                x = triangle.vertSecond.x * distanceToScreen / (cameraVector.z - triangle.vertSecond.z),
                y = triangle.vertSecond.y * distanceToScreen / (cameraVector.z - triangle.vertSecond.z),
                z = triangle.vertSecond.z * distanceToScreen / (cameraVector.z - triangle.vertSecond.z)
            ),
            vertThird = Point3D(
                x = triangle.vertThird.x * distanceToScreen / (cameraVector.z - triangle.vertThird.z),
                y = triangle.vertThird.y * distanceToScreen / (cameraVector.z - triangle.vertThird.z),
                z = triangle.vertThird.z * distanceToScreen / (cameraVector.z - triangle.vertThird.z)
            )
        )
    }
//    private fun getCameraPositionDistortion(triangle: Triangle3D) = triangle

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
        //TODO КОСТЫЛЬ!
        return -nScalarWithCamera / normal
    }

    private data class FocusArea(
        val leftTop: Point,
        val rightBottom: Point
    )

}
