package reader.obj

class ObjStubLinesProvider : ObjLinesProvider {

    override fun forEachLine(handler: (line: String) -> Unit) = LINES.forEach { handler(it) }

}
