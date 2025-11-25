package com.example.keepr.notifications

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.keepr.R
import java.util.concurrent.TimeUnit
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import com.example.keepr.MainActivity

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
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Come back to Keepr!")
            .setContentText("You haven't visited the app in 3 days.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(999, notification)
    }
}