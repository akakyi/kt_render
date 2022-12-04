package utils

interface LogsProvider {

    fun info(msg: String)

    fun debug(msg: String)

    fun debugObj(obj: Any)

    fun error(msg: String, e: Throwable? = null)

}
