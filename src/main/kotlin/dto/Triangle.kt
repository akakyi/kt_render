package dto

data class Triangle(
    val vertFirst: Point,
    val vertSecond: Point,
    val vertThird: Point
) {

    fun scale(xCoefficient: Double, yCoefficient: Double) = Triangle(
        vertFirst = Point(vertFirst.x * xCoefficient, vertFirst.y * yCoefficient),
        vertSecond = Point(vertSecond.x * xCoefficient, vertSecond.y * yCoefficient),
        vertThird = Point(vertThird.x * xCoefficient, vertThird.y * yCoefficient)
    )

}
