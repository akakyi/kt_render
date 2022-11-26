package dto

data class PointColored(
    val point: Point,
    val color: RGBAColor
) {

    constructor(x: Double, y: Double, color: RGBAColor) : this(
        point = Point(x, y),
        color = color
    )

}
