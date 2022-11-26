import dto.Point
import dto.PointColored
import dto.RGBAColor
import org.w3c.dom.CanvasRenderingContext2D
import render.Canvas

class CanvasRenderingContext2DToCanvasAdapter(
    private val context: CanvasRenderingContext2D
) : Canvas {

    override fun drawLine(start: Point, finish: Point, color: RGBAColor) {
        context.fillStyle = "rgb(${color.redChannel}, ${color.greenChannel}, ${color.blueChannel})"
        context.moveTo(start.x, start.y)
        context.lineTo(finish.x, finish.y)
    }

    override fun drawPoint(coord: PointColored, pointSize: Double) {
        val color = coord.color
        context.fillStyle = "rgb(${color.redChannel}, ${color.greenChannel}, ${color.blueChannel})"
        val point = coord.point
        context.fillRect(point.x, point.y, pointSize, pointSize)
    }

}
