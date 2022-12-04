package utils

class ConsoleLogsProvider : LogsProvider {

    override fun info(msg: String) {
        console.log(msg)
    }

    override fun debug(msg: String) {
        console.log(msg)
    }

    override fun debugObj(obj: Any) {
        console.log(obj)
    }

    override fun error(msg: String, e: Throwable?) {
        console.log(msg + (e?.message?.let { " $it" } ?: ""))
    }

}
