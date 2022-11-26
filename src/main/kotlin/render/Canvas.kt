package render

import dto.Point
import dto.PointColored
import dto.RGBAColor

interface Canvas {

    fun drawLine(start: Point, finish: Point, color: RGBAColor)

    fun drawPoint(coord: PointColored, pointSize: Double = 1.0)

}
