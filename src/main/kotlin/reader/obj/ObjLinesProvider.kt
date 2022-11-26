package reader.obj

interface ObjLinesProvider {

    fun forEachLine(handler: (line: String) -> Unit)

}
