package render

import dto.*
import utils.BarycentricCalculator
import utils.LogsProvider
import utils.MeasureProvider
import utils.dto.TriangleBarycentricDenominators
import utils.times
import kotlin.math.*

// Матрицы моё слабое место. Я не понял ни-хре-на
// (кроме того, что одно это матрица поворота и другое матрица смещения) и тупо переписал с хабра тот кусок -_-
// для норм понимания почитать НЕ уставшим https://habr.com/ru/post/248611/
// P.S. немного к статье выше: то, что чел называет камерой, по факту фонарик. А то, на что проецирует фонарик,
// по факту и есть матрица камеры, но при этом фонарик как бы просвечивает модель насквозь. Сложно визуализировать -_-
// P.P.S. так, сделал слегка по-своему (крыво). Считаю исходя из того, что у меня есть не камера,
// а виртуальный экран и его фокус. Из параметров у меня теперь фокусное расстояние и расстояние от фокуса до "экрана"
// так гораздо проще понимать как по мне, да и выглядит результат поприкольнее. Но время рендера просто ппц
class PolygonsRenderer(
    private val canvas: Canvas,
    private val logsProvider: LogsProvider,
    private val measureProvider: MeasureProvider,
    private val screenHeight: Int,
    private val screenWidth: Int,
    private val xShift: Double = .0,
    private val yShift: Double = .0,
    private val zShift: Double = .0,
    private val distanceToScreen: Double = 1000.0
) {


    companion object {

        val WHITE_RGB = RGBAColor(255, 255, 255, 0)

    }

    fun render(
        polygons: List<Polygon>,
        cameraVector: ThreeVector = ThreeVector(.0, .0, 1.0),
        angle: Angle = Angle(0, 0, 0)
    ) {
        val startMillis = measureProvider.currentMillis()

        val zBuffer = Array(screenWidth) {
            Array(screenHeight) {
                Double.NEGATIVE_INFINITY
            }
        }
        polygons.forEach {
            drawPolygon(it, cameraVector, angle, zBuffer)
        }

        val endMillis = measureProvider.currentMillis()
        logsProvider.debug("Millis taken for render: ${endMillis - startMillis}")
    }

    private fun drawPolygon(
        polygon: Polygon,
        cameraVector: ThreeVector,
        angle: Angle,
        zBuffer: Array<Array<Double>>
    ) {
        val startMillis = measureProvider.currentMillis()

        val triangleScaled = polygon.triangle.shift(xShift, yShift, zShift)
        logsProvider.debug("drawing $triangleScaled")

        val rotationCoeffMatrix = prepareRotationMatrix(angle.vertical, angle.horizontal, angle.depth)
        val distortedTriangle = getCameraPositionDistortion(triangleScaled, rotationCoeffMatrix, cameraVector)

        val normalCos = getNormalCosinus(distortedTriangle, cameraVector)
        logsProvider.debug("distorted before drawing: $distortedTriangle, normal: $normalCos")
        if (normalCos < 0) {
            return
        }

        val color = getColor(normalCos)
        logsProvider.debug("Will draw, colored with $color")

        val focusArea = calcFocusArea(distortedTriangle)
        val leftBordX = focusArea.leftTop.x.let { max(it, 0) }
        val rightBordX = focusArea.rightBottom.x.let { min(it, screenWidth) }
        val bottomBordY = focusArea.leftTop.y.let { max(it, 0) }
        val topBordY = focusArea.rightBottom.y.let { min(it, screenHeight) }

        val startMillisPainting = measureProvider.currentMillis()
        val lambdaDenominators = BarycentricCalculator.calcLambdaDenominators(distortedTriangle.proection2D)
        for (i in leftBordX..rightBordX) {
            for (j in bottomBordY..topBordY) {
                paintPixelInTriangle(
                    pointCol = ScreenPointColored(i, j, color),
                    basis = distortedTriangle,
                    triangleBarycentricDenominators = lambdaDenominators,
                    zBuffer = zBuffer
                )
            }
        }
        val endMillisPainting = measureProvider.currentMillis()
        logsProvider.debug("Millis taken for painting: ${endMillisPainting - startMillisPainting}")

        val endMillis = measureProvider.currentMillis()
        logsProvider.debug("Millis taken for draw polygon: ${endMillis - startMillis}")
    }

    private fun paintPixelInTriangle(
        pointCol: ScreenPointColored,
        basis: Triangle3D,
        triangleBarycentricDenominators: TriangleBarycentricDenominators,
        zBuffer: Array<Array<Double>>
    ) {
        val screenPoint = pointCol.point

        val startMillis = measureProvider.currentMillis()

        val barycentric = BarycentricCalculator.calcTriangleBarycentric(
            basis = basis.proection2D,
            point = screenPoint,
            preCalcLambdaDenominators = triangleBarycentricDenominators
        )
        if (barycentric.lambdaFirst < 0 || barycentric.lambdaSecond < 0 || barycentric.lambdaThird < 0) {
            return
        }

        val currZBuffer = zBuffer[screenPoint.x][screenPoint.y]
        val currZ = barycentric.lambdaFirst * basis.vertFirst.z + barycentric.lambdaSecond * basis.vertSecond.z +
                barycentric.lambdaThird * basis.vertThird.z
        if (currZ > currZBuffer) {
            zBuffer[screenPoint.x][screenPoint.y] = currZ
            canvas.drawPoint(
                coord = pointCol
            )
        }

        val endMillis = measureProvider.currentMillis()
        logsProvider.debug("Millis taken for draw pixel: ${endMillis - startMillis}")
    }

    private fun getCameraPositionDistortion(
        triangle: Triangle3D,
        rotationCoeffMatrix: Array<Array<Double>>,
        cameraVector: ThreeVector
    ): Triangle3D {
        val startMillis = measureProvider.currentMillis()

        val firstPerspectiveCoeff = preparePerspectiveCoeffMatrix(triangle.vertFirst, cameraVector)
        val first = preparePerspectivePointMatrix(triangle.vertFirst)
        val vertFirstMatrix = rotationCoeffMatrix * firstPerspectiveCoeff * first
        logsProvider.debugObj(vertFirstMatrix)

        val secondPerspectiveCoeff = preparePerspectiveCoeffMatrix(triangle.vertSecond, cameraVector)
        val second = preparePerspectivePointMatrix(triangle.vertSecond)
        val vertSecondMatrix = rotationCoeffMatrix * secondPerspectiveCoeff * second
        logsProvider.debugObj(vertSecondMatrix)

        val thirdPerspectiveCoeff = preparePerspectiveCoeffMatrix(triangle.vertThird, cameraVector)
        val third = preparePerspectivePointMatrix(triangle.vertThird)
        val vertThirdMatrix = rotationCoeffMatrix * thirdPerspectiveCoeff * third
        logsProvider.debugObj(vertThirdMatrix)

        return Triangle3D(
            vertFirst = Point3D(
                x = vertFirstMatrix[0][0] / vertFirstMatrix[3][0],
                y = vertFirstMatrix[1][0] / vertFirstMatrix[3][0],
                z = vertFirstMatrix[2][0] / vertFirstMatrix[3][0]
            ),
            vertSecond = Point3D(
                x = vertSecondMatrix[0][0] / vertSecondMatrix[3][0],
                y = vertSecondMatrix[1][0] / vertSecondMatrix[3][0],
                z = vertSecondMatrix[2][0] / vertSecondMatrix[3][0]
            ),
            vertThird = Point3D(
                x = vertThirdMatrix[0][0] / vertThirdMatrix[3][0],
                y = vertThirdMatrix[1][0] / vertThirdMatrix[3][0],
                z = vertThirdMatrix[2][0] / vertThirdMatrix[3][0]
            )
        ).also {
            val endMillis = measureProvider.currentMillis()
            logsProvider.debug("Millis taken for distortion triangle: ${endMillis - startMillis}")
        }
    }
//    private fun getCameraPositionDistortion(triangle: Triangle3D) = triangle

    //Я сам не совсем понимаю, что конкретно я тут делаю. Нужно будет на бумаге расписать, ибо проекция понятна
    //вращение не оч понятно, сдвиг аналогично. Матрицы, сложна
    //TODO Вращает вокруг хз чего
    private fun prepareRotationMatrix(
        angleVertical: Int,
        angleHorizontal: Int,
        angleDepth: Int
    ): Array<Array<Double>> {
        val radVertical = angleVertical.toDouble() * PI / 180
        val radHorizontal = angleHorizontal.toDouble() * PI / 180
        val radDepth = angleDepth.toDouble() * PI / 180

        val vertical = arrayOf(
            arrayOf( cos(radVertical), sin(radVertical), .0,  .0),
            arrayOf(-sin(radVertical), cos(radVertical), .0,  .0),
            arrayOf(.0,            .0,                  1.0,  .0),
            arrayOf(.0,            .0,                   .0, 1.0)
        )
        val horizontal = arrayOf(
            arrayOf( cos(radHorizontal), 0.0, sin(radHorizontal),  .0),
            arrayOf(                 .0, 1.0,                 .0,  .0),
            arrayOf(-sin(radHorizontal), 0.0, cos(radHorizontal),  .0),
            arrayOf(                 .0, 0.0,                 .0, 1.0)
        )
        val depth = arrayOf(
            arrayOf(1.0,             .0,            .0,   .0),
            arrayOf( .0,  cos(radDepth),  sin(radDepth),  .0),
            arrayOf( .0, -sin(radDepth),  cos(radDepth),  .0),
            arrayOf( .0, .0,             .0,             1.0)
        )

//        val centrShift = prepareShiftMatrix(-xShift, -yShift, -zShift)
//        val backShift = prepareShiftMatrix(xShift, yShift, zShift)
        return (vertical * horizontal * depth)
            .also {
                logsProvider.debug("Rotation matrix:")
                logsProvider.debugObj(it)
            }
    }

    private fun prepareShiftMatrix(
        xShift: Double,
        yShift: Double,
        zShift: Double
    ) = arrayOf(
        arrayOf(1.0, .0, .0, xShift),
        arrayOf(.0, 1.0, .0, yShift),
        arrayOf(.0, .0, 1.0, zShift),
        arrayOf(.0, .0, .0, 1.0)
    )

    private fun preparePerspectivePointMatrix(vertex: Point3D) = arrayOf(
        arrayOf(vertex.x),
        arrayOf(vertex.y),
        arrayOf(vertex.z),
        arrayOf(1.0)
    )

    private fun preparePerspectiveCoeffMatrix(vertex: Point3D, cameraVector: ThreeVector) = arrayOf(
        arrayOf(1.0, 0.0, 0.0, 0.0),
        arrayOf(0.0, 1.0, 0.0, 0.0),
        arrayOf(0.0, 0.0, 1.0, 0.0),
        arrayOf(0.0, 0.0, 0.0, (cameraVector.z - vertex.z) / distanceToScreen)
    )

    private fun calcFocusArea(triangle: Triangle3D): FocusArea {
        val leftTopX = minOf(triangle.vertFirst.x, triangle.vertSecond.x, triangle.vertThird.x)
        val leftTopY = minOf(triangle.vertFirst.y, triangle.vertSecond.y, triangle.vertThird.y)
        val rightDownX = maxOf(triangle.vertFirst.x, triangle.vertSecond.x, triangle.vertThird.x)
        val rightDownY = maxOf(triangle.vertFirst.y, triangle.vertSecond.y, triangle.vertThird.y)

        return FocusArea(
            leftTop = ScreenPoint(leftTopX.toInt(), leftTopY.toInt()),
            rightBottom = ScreenPoint(rightDownX.toInt(), rightDownY.toInt())
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
        val leftTop: ScreenPoint,
        val rightBottom: ScreenPoint
    )

}
