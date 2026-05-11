package com.kevinfreyap.ecommerce.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.kevinfreyap.core.domain.notification.INotificationService
import com.kevinfreyap.shared_ui.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationServiceImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
): INotificationService {
    private val CHANNEL_ID = "Ecommerce_Alerts"
    private val CHANNEL_NAME = "E-commerce Notifications"

    override fun showNotification(
        title: String,
        message: String,
        type: String
    ) {
        createNotificationChannel()

        val deeplinkUri = when(type) {
            "VOUCHER" -> "app://ecommerce/voucher".toUri()
            "TRANSACTION" -> "app://ecommerce/transaction".toUri()
            "WISHLIST" -> "app://ecommerce/wishlist".toUri()
            else -> "app://ecommerce/home".toUri()
        }

        val intent = Intent(
            Intent.ACTION_VIEW,
            deeplinkUri
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for status changes and stock."
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}