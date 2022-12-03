package dto

data class ScreenPointColored(
    val point: ScreenPoint,
    val color: RGBAColor
) {

    constructor(x: Int, y: Int, color: RGBAColor) : this(
        point = ScreenPoint(x, y),
        color = color
    )

}
