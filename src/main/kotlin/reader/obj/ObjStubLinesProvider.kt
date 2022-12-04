package reader.obj

class ObjStubLinesProvider : ObjLinesProvider {

    override fun forEachLine(handler: (line: String) -> Unit) = AFRICAN_MAN_LINES.forEach { handler(it) }

}
