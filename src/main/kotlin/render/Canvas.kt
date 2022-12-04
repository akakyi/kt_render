package render

import dto.RGBAColor
import dto.ScreenPoint
import dto.ScreenPointColored
import dto.Triangle

interface Canvas {

    fun drawLine(start: ScreenPoint, finish: ScreenPoint, color: RGBAColor)

    fun drawPoint(coord: ScreenPointColored, pointSize: Double = 1.0)

    fun canDrawTriangle(triangle: Triangle, color: RGBAColor): Boolean

    fun drawTriangle(triangle: Triangle, color: RGBAColor) {  }

}
