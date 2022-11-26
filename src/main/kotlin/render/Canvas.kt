package render

import dto.Point

interface Canvas {

    fun drawLine(start: Point, finish: Point)

    fun drawPoint(coord: Point)

}
