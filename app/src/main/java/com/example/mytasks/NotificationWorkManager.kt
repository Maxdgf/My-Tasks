package com.example.mytasks

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorkManager(context: Context, workParameter: WorkerParameters) : Worker(context, workParameter) {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "MyTasksPeriodicChannelId"
        const val NOTIFICATION_ID = 23289
    }

    override fun doWork(): Result {
        createNotification()
        return Result.success()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotification() {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val dbTaskHeadHelper = TaskHeadDB(applicationContext)

        val database1 = dbTaskHeadHelper.readableDatabase
        val projection = arrayOf("Id", "TaskName", "TaskDescription", "DateNow", "MustCompleted", "AllTasks", "CompletedTasks", "TagType", "IsCompleted", "RemindDate", "RemindTime")

        val cursor = database1.query("TaskHeadsTable", projection, null, null, null, null, null)

        var isCompleted: String?

        val completed: MutableList<String> = mutableListOf()

        while (cursor.moveToNext()) {
            isCompleted = cursor.getString(cursor.getColumnIndexOrThrow("IsCompleted"))

            completed.add(isCompleted)
        }

        cursor.close()
        database1.close()

        val completedCount = completed.count { it == "Yes" }.toString()
        val notCompletedCount = completed.count { it == "No" }.toString()

        val completedTxt = applicationContext.getString(R.string.completed)
        val notCompletedTxt = applicationContext.getString(R.string.notCompleted)
        val lists = applicationContext.getString(R.string.lists)

        val completedStats = "$completedTxt-$completedCount $lists\n$notCompletedTxt-$notCompletedCount $lists"

        val p1 = applicationContext.getString(R.string.phrase1)
        val p2 = applicationContext.getString(R.string.phrase2)
        val p3 = applicationContext.getString(R.string.phrase3)
        val p4 = applicationContext.getString(R.string.phrase4)
        val p5 = applicationContext.getString(R.string.phrase5)

        val randomPhrases: MutableList<String> = mutableListOf(p1, p2, p3, p4, p5)
        val text = randomPhrases.random() + "\n$completedStats"

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = Notification.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.task_icon)
            .setContentTitle("MyTasks")
            .setContentText(text)
            .setPriority(Notification.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val CHANNEL_NAME = "MyTasksNotificationPeriodicChannelName"
            val CHANNEL_DESCRIPTION = "channel for periodic notification send process."
            val CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, CHANNEL_IMPORTANCE).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(NOTIFICATION_ID, notification.build())
        }
    }
}