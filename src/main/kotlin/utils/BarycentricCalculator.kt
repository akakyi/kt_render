package utils

import dto.Point
import dto.Triangle
import utils.dto.TriangleBarycentric

object BarycentricCalculator {

    fun calcTriangleBarycentric(basis: Triangle, point: Point): TriangleBarycentric {
        val x0 = basis.vertFirst.x
        val x1 = basis.vertSecond.x
        val x2 = basis.vertThird.x
        val y0 = basis.vertFirst.y
        val y1 = basis.vertSecond.y
        val y2 = basis.vertThird.y

        val firstLambda = ((point.y - y2) * (x1 - x2) - (point.x - x2) * (y1 - y2)) /
                ((y0 - y2) * (x1 - x2) - (x0 - x2) * (y1 - y2))
        val secondLambda = ((point.y - y0) * (x2 - x0) - (point.x - x0) * (y2 - y0)) /
                ((y1 - y0) * (x2 - x0) - (x1 - x0) * (y2 - y0))
        val thirdLambda = ((point.y - y1) * (x0 - x1) - (point.x - x1) * (y0 - y1)) /
                ((y2 - y1) * (x0 - x1) - (x2 - x1) * (y0 - y1))
        return TriangleBarycentric(
            lambdaFirst = firstLambda,
            lambdaSecond = secondLambda,
            lambdaThird = thirdLambda
        )
    }

}