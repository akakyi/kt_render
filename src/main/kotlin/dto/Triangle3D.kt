package dto

data class Triangle3D(
    val vertFirst: Point3D,
    val vertSecond: Point3D,
    val vertThird: Point3D
) {

    val proection2D: Triangle
        get() = Triangle(
            vertFirst = Point(vertFirst.x, vertFirst.y),
            vertSecond = Point(vertSecond.x, vertSecond.y),
            vertThird = Point(vertThird.x, vertThird.y)
        )

    fun scale(xCoefficient: Double, yCoefficient: Double, zCoefficient: Double) = Triangle3D(
        vertFirst = Point3D(vertFirst.x * xCoefficient, vertFirst.y * yCoefficient, vertFirst.z * zCoefficient),
        vertSecond = Point3D(vertSecond.x * xCoefficient, vertSecond.y * yCoefficient, vertSecond.z * zCoefficient),
        vertThird = Point3D(vertThird.x * xCoefficient, vertThird.y * yCoefficient, vertThird.z * zCoefficient)
    )

}
