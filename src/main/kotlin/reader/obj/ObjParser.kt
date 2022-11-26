package reader.obj

import dto.*

class ObjParser(
    val linesProvider: ObjLinesProvider
) {

    companion object {

        private const val POINT_LABEL = "v"

        private const val POLIGON_LABEL = "f"

        private const val TEXTURE_LABEL = "vt"

    }

    /**
     * Возвращает список полигонов.
     * ALARM! Архитектура не подразумевает, если посреди описаний полигонов будет описание ещё одной вершины,
     * и наоборот.
     * Не очень, но иф файл написан корректно, то проблем не возникнет.
     * @return Список полигонов.
     */
    fun parse(): List<Poligon> {
        val points = mutableListOf<Point3D>()
        val triangles = mutableListOf<Triangle3D>()
        linesProvider.forEachLine {
            val splitted = it.split(" ")
            when (splitted[0]) {
                POINT_LABEL -> parsePoint(splitted).let { point -> points.add(point) }
                POLIGON_LABEL -> parseTriangle(splitted, points)
                    .let { triangle ->
                        triangles.add(triangle.first)
                        val second = triangle.second
                        if (second != null)
                            triangles.add(second)
                    }
            }
        }

        return triangles
            .map { Poligon(it) }
    }

    private fun parsePoint(splittedLine: List<String>) = Point3D(
        x = splittedLine[1].toDouble(),
        y = splittedLine[2].toDouble(),
        z = splittedLine[3].toDouble()
    )

    private fun parseTriangle(splittedLine: List<String>, points: List<Point3D>): Pair<Triangle3D, Triangle3D?> {
        val firstPointNum = splittedLine[1].split("/")[0].toInt()
        val secondPointNum = splittedLine[2].split("/")[0].toInt()
        val thirdPointNum = splittedLine[3].split("/")[0].toInt()

        val first = Triangle3D(
            vertFirst = points[firstPointNum - 1],
            vertSecond = points[secondPointNum - 1],
            vertThird = points[thirdPointNum - 1]
        )
        return if (splittedLine.size > 4) {
            val fourthPointNum = splittedLine[4].split("/")[0].toInt()
            val second = Triangle3D(
                vertFirst = points[firstPointNum - 1],
                vertSecond = points[thirdPointNum - 1],
                vertThird = points[fourthPointNum - 1]
            )
            first to second
        } else {
            first to null
        }
    }

}
