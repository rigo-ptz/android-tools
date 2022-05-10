package com.oxygen.ktx_ui.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.oxygen.ktx_ui.LOG_TAG
import com.oxygen.ktx_ui.context.color

object NotificationUtil {

    fun buildNotification(
        context: Context,
        @StringRes titleResId: Int,
        messageBody: String,
        pendingIntent: PendingIntent?,
        notificationChannelId: String,
        timeoutMills: Long? = null,
        @ColorRes backgroundColorId: Int? = null,
        @DrawableRes smallIconId: Int? = null
    ): Notification {

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        return NotificationCompat.Builder(context, notificationChannelId)
            .setContentTitle(context.getString(titleResId))
            .setContentText(messageBody)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setAutoCancel(true)
            .apply {
                timeoutMills?.let {
                    setTimeoutAfter(it)
                }

                setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)

                backgroundColorId?.let {
                    color = context.color(it)
                }

                smallIconId?.let {
                    setSmallIcon(it)
                }
            }
            .build()
    }

    /**
     * Get list of all available notification channels for the app
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createAllNotificationChannels(context: Context, vararg channelIdsNames: Pair<String, String>): List<NotificationChannel> {
        removeDefaultFirebaseNotificationChannel(context)
        return channelIdsNames.map {
            createNotificationChannel(it.first, it.second)
        }
    }

    /**
     * Until 15.08.18 there was no default Firebase notification channel in the AndroidManifest.
     * Firebase was creating a missing channel. We're not using it and it should be deleted.
     *
     * Last public release without default channel: 1.5.25
     */
    private fun removeDefaultFirebaseNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        try {
            notificationManager?.deleteNotificationChannel("fcm_fallback_notification_channel")
        } catch (e: Exception) {
            Log.e(
                LOG_TAG,
                "Exception occurred when removing Firebase the default notification channel",
                e
            )
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        channelId: String,
        channelName: String,
    ): NotificationChannel {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.enableVibration(true)
        return channel
    }

    fun cancelNotificationByTag(context: Context, tag: String) {
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.apply {
            cancel(tag, 0 /* ID of notification */)
        }
    }

    fun areNotificationsEnabled(context: Context, vararg channelsIds: String): Boolean {
        val areNotificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()

        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            areNotificationsEnabled
        } else {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            val channelsEnabled = channelsIds
                .map {
                    val channel = manager?.getNotificationChannel(it)
                    channel != null && channel.importance != NotificationManager.IMPORTANCE_NONE
                }
                .all { it }

            areNotificationsEnabled && channelsEnabled
        }
    }

}
