import helper.protocol.MagicNumber
import helper.toByteArray
import helper.toInt


fun main() {
    val magicNumber = MagicNumber.START.value
    val buffer = magicNumber.toByteArray()
    val number = buffer.toInt()
    assert(number == magicNumber)
}