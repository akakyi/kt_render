package utils

operator fun Array<Array<Double>>.times(right: Array<Array<Double>>): Array<Array<Double>> {
    val n = this.size
    val m = right[0].size
    val z = right.size
    val result = Array(n) { Array(m) { .0 } }

    for (i in 0 until n) {
        for (j in 0 until m) {
            for (k in 0 until z) {
                result[i][j] += this[i][k] * right[k][j]
            }
        }
    }
    return result
}

object MatrixUtils {

//    fun concat(left: Array<Array<Double>>, right: Array<Array<Double>>): Array<Array<Double>> {
//        val rows = left.size
//        val columns = left[0].size + right[0].size
//        val
//    }

}
