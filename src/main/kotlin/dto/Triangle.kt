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

    fun shift(xShift: Double, yShift: Double) = Triangle(
        vertFirst = Point(vertFirst.x + xShift, vertFirst.y + yShift),
        vertSecond = Point(vertSecond.x + xShift, vertSecond.y + yShift),
        vertThird = Point(vertThird.x + xShift, vertThird.y + yShift)
    )

}
