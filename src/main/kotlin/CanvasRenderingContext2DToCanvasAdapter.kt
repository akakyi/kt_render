import dto.Point
import org.w3c.dom.CanvasRenderingContext2D
import render.Canvas

class CanvasRenderingContext2DToCanvasAdapter(
    private val canvas: CanvasRenderingContext2D
) : Canvas {

    override fun drawLine(start: Point, finish: Point) {
        canvas.moveTo(start.x, start.y)
        canvas.lineTo(finish.x, finish.y)
    }

    override fun drawPoint(coord: Point) {
        canvas.moveTo(coord.x, coord.y)
        canvas.lineTo(coord.x, coord.y)
    }

}
