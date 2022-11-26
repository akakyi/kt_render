package dto

data class RGBAColor(
    val redChannel: Short,
    val greenChannel: Short,
    val blueChannel: Short,
    val alphaChannel: Short
) {

    fun scale(mult: Double) = RGBAColor(
        redChannel = (redChannel * mult).toInt().toShort(),
        greenChannel = (greenChannel * mult).toInt().toShort(),
        blueChannel = (blueChannel * mult).toInt().toShort(),
        alphaChannel = alphaChannel
    )

}
