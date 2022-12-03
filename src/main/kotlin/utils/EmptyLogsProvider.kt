package utils

class EmptyLogsProvider : LogsProvider {

    override fun info(msg: String) {  }

    override fun debug(msg: String) {  }

    override fun debugObj(obj: Any) {  }

    override fun error(msg: String, e: Throwable?) {  }

}
