
import dto.RGBAColor
import dto.ScreenPoint
import dto.ScreenPointColored
import dto.Triangle
import org.w3c.dom.CanvasRenderingContext2D
import render.Canvas

class CanvasRenderingContext2DToCanvasAdapter(
    private val context: CanvasRenderingContext2D
) : Canvas {

    override fun drawLine(start: ScreenPoint, finish: ScreenPoint, color: RGBAColor) {
        context.fillStyle = "rgb(${color.redChannel}, ${color.greenChannel}, ${color.blueChannel})"
        context.moveTo(start.x.toDouble(), start.y.toDouble())
        context.lineTo(finish.x.toDouble(), finish.y.toDouble())
    }

    override fun drawPoint(coord: ScreenPointColored, pointSize: Double) {
        val color = coord.color
        context.fillStyle = "rgb(${color.redChannel}, ${color.greenChannel}, ${color.blueChannel})"
        val point = coord.point
        context.fillRect(point.x.toDouble(), point.y.toDouble(), pointSize, pointSize)
    }

    override fun canDrawTriangle(triangle: Triangle, color: RGBAColor) = true

    override fun drawTriangle(triangle: Triangle, color: RGBAColor) {
        context.fillStyle = "rgb(${color.redChannel}, ${color.greenChannel}, ${color.blueChannel})"
        context.beginPath()

        context.moveTo(triangle.vertFirst.x, triangle.vertFirst.y)
        context.lineTo(triangle.vertSecond.x, triangle.vertSecond.y)
        context.lineTo(triangle.vertThird.x, triangle.vertThird.y)
        context.fill()
    }
}
