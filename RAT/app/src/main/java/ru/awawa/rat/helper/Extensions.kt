package ru.awawa.rat.helper

fun Int.toByteArray(): ByteArray {
    val result = ByteArray(4)
    result[0] = this.shr(24).and(0xff).toByte()
    result[1] = this.shr(16).and(0xff).toByte()
    result[2] = this.shr(8).and(0xff).toByte()
    result[3] = this.and(0xff).toByte()
    return result
}

fun Short.toByteArray(): ByteArray {
    val result = ByteArray(2)
    result[0] = this.toInt().shr(8).and(0xff).toByte()
    result[1] = this.toInt().and(0xff).toByte()
    return result
}

fun ByteArray.toInt(): Int {
    return if (size != 4)
        0
    else
        (this[0].toInt().and(0xff).shl(24) or this[1].toInt().and(0xff).shl(16) or this[2].toInt().and(0xff).shl(8) or this[3].toInt().and(0xff))
}

fun ByteArray.toShort(): Short {
    return if (size != 2)
        0
    else
        (this[0].toInt().and(0xff).shl(8) or this[1].toInt().and(0xff)).toShort()
}

fun ByteArray.fragment(partSize: Int): List<Pair<Int, ByteArray>> {
    if (this.size <= partSize) return listOf(Pair(this.size, this))

    val partsCount = size / partSize
    var j = 0
    val result = ArrayList<Pair<Int, ByteArray>>()
    for (i in 0 until partsCount) {
        result.add(Pair(partSize, this.sliceArray(j until j + partSize)))
        j += partSize
    }

    val lastPart = ByteArray(size - j)
    for (i in j until size) {
        lastPart[i - j] = this[i]
    }
    result.add(Pair(lastPart.size, lastPart))
    return result.toList()
}

fun Collection<Pair<Int, ByteArray>>.defragment(): ByteArray {
    var size = 0
    for (s in this) { size += s.first }

    val result = ByteArray(size) { 0 }
    var i = 0
    for (s in this) {
        for (j in 0 until s.first) {
            result[i] = s.second[j]
            i += 1
        }
    }

    return result
}

fun ByteArray.toHexString() = this.joinToString(separator = "") { "%02x".format(it.toInt() and 0xFF) }
