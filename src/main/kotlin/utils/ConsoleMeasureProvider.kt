package utils

import kotlin.js.Date

class ConsoleMeasureProvider : MeasureProvider {

    override fun currentMillis() = Date.now().toLong()

}
