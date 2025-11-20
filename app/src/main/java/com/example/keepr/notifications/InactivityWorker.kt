package com.example.keepr.notifications

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.keepr.R
import java.util.concurrent.TimeUnit
import android.annotation.SuppressLint

class InactivityWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        val prefs = context.getSharedPreferences("keepr_prefs", Context.MODE_PRIVATE)
        val lastOpened = prefs.getLong("last_opened", 0L)

        val now = System.currentTimeMillis()
        val threeDays = TimeUnit.DAYS.toMillis(3)

        if (now - lastOpened >= threeDays) {
            sendNotification()
        }

        return Result.success()
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification() {
        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.notification_inactivity_title))
            .setContentText(context.getString(R.string.notification_inactivity_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(999, notification)
        } catch (e: SecurityException) {
            // Hvis brukeren har blokkert notifications, gjør vi bare ingenting
        }
    }
}