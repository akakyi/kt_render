package utils

import dto.ScreenPoint
import dto.Triangle
import utils.dto.TriangleBarycentric
import utils.dto.TriangleBarycentricDenominators

object BarycentricCalculator {

    fun calcTriangleBarycentric(basis: Triangle, point: ScreenPoint) = calcTriangleBarycentric(
        basis = basis,
        point = point,
        preCalcLambdaDenominators = calcLambdaDenominators(basis)
    )

    fun calcTriangleBarycentric(
        basis: Triangle,
        point: ScreenPoint,
        preCalcLambdaDenominators: TriangleBarycentricDenominators
    ): TriangleBarycentric {
        val (x0, y0) = basis.vertFirst
        val (x1, y1) = basis.vertSecond
        val (x2, y2) = basis.vertThird

        val pointX = point.x.toDouble()
        val pointY = point.y.toDouble()
        val firstLambda = ((pointY - y2) * (x1 - x2) - (pointX - x2) * (y1 - y2)) /
                preCalcLambdaDenominators.first
        val secondLambda = ((pointY - y0) * (x2 - x0) - (pointX - x0) * (y2 - y0)) /
                preCalcLambdaDenominators.second
        val thirdLambda = ((pointY - y1) * (x0 - x1) - (pointX - x1) * (y0 - y1)) /
                preCalcLambdaDenominators.third
        return TriangleBarycentric(
            lambdaFirst = firstLambda,
            lambdaSecond = secondLambda,
            lambdaThird = thirdLambda
        )
    }

    fun calcLambdaDenominators(basis: Triangle): TriangleBarycentricDenominators {
        val (x0, y0) = basis.vertFirst
        val (x1, y1) = basis.vertSecond
        val (x2, y2) = basis.vertThird

        return TriangleBarycentricDenominators(
            first = (y0 - y2) * (x1 - x2) - (x0 - x2) * (y1 - y2),
            second = (y1 - y0) * (x2 - x0) - (x1 - x0) * (y2 - y0),
            third = (y2 - y1) * (x0 - x1) - (x2 - x1) * (y0 - y1)
    }

}