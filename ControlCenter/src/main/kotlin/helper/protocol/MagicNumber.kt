package helper.protocol

enum class MagicNumber(val value: Int) {
    START(0xffbbeedd.toInt()),
    PHONE_INFO(0xffeeddbb.toInt())
}