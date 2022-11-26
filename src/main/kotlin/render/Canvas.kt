package render

import dto.Point
import dto.PointColored
import dto.RGBAColor
import dto.Triangle

interface Canvas {

    fun drawLine(start: Point, finish: Point, color: RGBAColor)

    fun drawPoint(coord: PointColored, pointSize: Double = 1.0)

    fun canDrawTriangle(triangle: Triangle, color: RGBAColor): Boolean

    fun drawTriangle(triangle: Triangle, color: RGBAColor) {  }

}
