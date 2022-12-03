package utils

import dto.ScreenPoint
import dto.Triangle
import utils.dto.TriangleBarycentric

object BarycentricCalculator {

    fun calcTriangleBarycentric(basis: Triangle, point: ScreenPoint): TriangleBarycentric {
        val x0 = basis.vertFirst.x
        val x1 = basis.vertSecond.x
        val x2 = basis.vertThird.x
        val y0 = basis.vertFirst.y
        val y1 = basis.vertSecond.y
        val y2 = basis.vertThird.y

        val pointX = point.x.toDouble()
        val pointY = point.y.toDouble()
        val firstLambda = ((pointY - y2) * (x1 - x2) - (pointX - x2) * (y1 - y2)) /
                ((y0 - y2) * (x1 - x2) - (x0 - x2) * (y1 - y2))
        val secondLambda = ((pointY - y0) * (x2 - x0) - (pointX - x0) * (y2 - y0)) /
                ((y1 - y0) * (x2 - x0) - (x1 - x0) * (y2 - y0))
        val thirdLambda = ((pointY - y1) * (x0 - x1) - (pointX - x1) * (y0 - y1)) /
                ((y2 - y1) * (x0 - x1) - (x2 - x1) * (y0 - y1))
        return TriangleBarycentric(
            lambdaFirst = firstLambda,
            lambdaSecond = secondLambda,
            lambdaThird = thirdLambda
        )
    }

}