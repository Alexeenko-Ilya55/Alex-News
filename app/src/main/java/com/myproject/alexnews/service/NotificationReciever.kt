package com.myproject.alexnews.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings.Global.getString
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.CHANNEL_ID
import com.myproject.alexnews.activity.MainActivity
import java.util.stream.DoubleStream.builder

class NotificationReceiver : BroadcastReceiver() {
    lateinit var notificationManager : NotificationManager

    override fun onReceive(context: Context, intent: Intent?) {
         notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intentRepeating = Intent(context, MainActivity::class.java)

        intentRepeating.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent = PendingIntent.getActivity(
            context,
            100,
            intentRepeating,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        createNotificationChannel()
        var builder = Notification.Builder(context, "MyChannel")
            .setSmallIcon(R.drawable.search_icon)
            .setContentTitle("My notification")
            .setContentText("Much longer text that cannot fit one line...")
    }
    private fun createNotificationChannel() {
        val name = "MyChannel"
        val descriptionText = "description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("MyChannel", name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }

}
