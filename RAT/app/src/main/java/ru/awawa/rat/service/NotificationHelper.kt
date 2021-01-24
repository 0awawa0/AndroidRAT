package ru.awawa.rat.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import ru.awawa.rat.MainActivity
import ru.awawa.rat.R


fun getServiceNotification(context: Context): Notification {
    return createNotification(context)
}


// Создаёт уведомление для форграунд свервиса
private fun createNotification(context: Context): Notification {
    createNotificationChannel(context)
    val notificationIntent = Intent(context.applicationContext, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context.applicationContext,
        0,
        notificationIntent,
        0
    )

    return NotificationCompat.Builder(context.applicationContext,
        "RatForegroundServiceChannel")
        .setContentTitle("Service is running")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentIntent(pendingIntent)
        .setCategory(NotificationCompat.CATEGORY_SERVICE)
        .setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
        .build()
}


private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            "RatForegroundServiceChannel",
            "RatForegroundServiceChannel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.setSound(null, null)
        notificationChannel.setShowBadge(false)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.createNotificationChannel(notificationChannel)
    }
}
