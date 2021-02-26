package helper


fun Int.toByteArray(): ByteArray {
    val result = ByteArray(4)
    result[0] = this.and(0xff).toByte()
    result[1] = this.shr(8).and(0xff).toByte()
    result[2] = this.shr(16).and(0xff).toByte()
    result[3] = this.shr(24).and(0xff).toByte()
    return result
}

fun ByteArray.toInt(): Int {
    if (this.count() < 4) return 0
    var result = this[3].toInt().shl(24) and 0xff000000.toInt()
    result = result or (this[2].toInt().shl(16) and 0xff0000)
    result = result or (this[1].toInt().shl(8) and 0xff00)
    result = result or (this[0].toInt() and 0xff)
    return result
}
