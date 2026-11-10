package com.ne.muslimprayers.qiblafinder.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.ne.muslimprayers.qiblafinder.R
import com.ne.muslimprayers.qiblafinder.core.common.PrayerNotificationMode
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerAlarmItem
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerType
import com.ne.muslimprayers.qiblafinder.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrayerAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "islamic_prayer_alert_channel_v2"
        const val CHANNEL_NAME = "Prayer Times & Alerts"
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        const val EXTRA_PRAYER_NAME = "extra_prayer_name"
        const val EXTRA_PRAYER_TIME = "extra_prayer_time"
        const val ACTION_STOP_ADHAN = "com.ne.muslimprayers.qiblafinder.ACTION_STOP_ADHAN"

        // Play only a short opening Takbeer snippet (~14 seconds) rather than the full 3.5-minute Adhan
        private const val SHORT_ADHAN_DURATION_MS = 14_000L
        private const val SHORT_TONE_DURATION_MS = 4_000L

        private var activeMediaPlayer: MediaPlayer? = null
        private var activeRingtone: Ringtone? = null
        private val mainHandler = Handler(Looper.getMainLooper())
        private var autoStopRunnable: Runnable? = null

        fun stopActiveAdhan() {
            try {
                autoStopRunnable?.let { mainHandler.removeCallbacks(it) }
                autoStopRunnable = null
            } catch (_: Exception) {
            }
            try {
                activeMediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        mp.stop()
                    }
                    mp.release()
                }
            } catch (_: Exception) {
            } finally {
                activeMediaPlayer = null
            }
            try {
                activeRingtone?.let { rt ->
                    if (rt.isPlaying) {
                        rt.stop()
                    }
                }
            } catch (_: Exception) {
            } finally {
                activeRingtone = null
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_STOP_ADHAN) {
            stopActiveAdhan()
            val notifId = intent.getIntExtra(EXTRA_ALARM_ID, -1)
            if (notifId >= 0) {
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.cancel(notifId)
            }
            return
        }

        val alarmId = intent.getIntExtra(EXTRA_ALARM_ID, 0)
        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: "Prayer"
        val prayerTime = intent.getStringExtra(EXTRA_PRAYER_TIME) ?: ""

        val prefs = context.getSharedPreferences("prayer_settings_prefs", Context.MODE_PRIVATE)
        val masterEnabled = prefs.getBoolean("notifications_enabled", true)
        val prayerKey = prayerName.uppercase()
        val isSpecificEnabled = prefs.getBoolean("alarm_enabled_$prayerKey", true)

        if (!masterEnabled || !isSpecificEnabled) {
            return
        }

        val modeKey = prefs.getString("notif_mode_$prayerKey", PrayerNotificationMode.TONE.key)
        val mode = PrayerNotificationMode.fromKey(modeKey)

        showPrayerNotification(context, alarmId, prayerName, prayerTime, mode)
        when (mode) {
            PrayerNotificationMode.ADHAN -> playShortAdhanSnippet(context, prayerName)
            PrayerNotificationMode.TONE -> playShortAlarmTone(context)
            PrayerNotificationMode.SILENT -> Unit
        }

        // Automatically reschedule for tomorrow at the same prayer time
        if (prayerTime.isNotBlank()) {
            val type = try {
                PrayerType.valueOf(prayerKey)
            } catch (_: Exception) {
                PrayerType.FAJR
            }
            AlarmScheduler(context).scheduleAlarm(
                PrayerAlarmItem(
                    id = alarmId,
                    prayerType = type,
                    prayerTime = prayerTime,
                    isAlarmActive = true
                )
            )
        }
    }

    private fun showPrayerNotification(
        context: Context,
        notificationId: Int,
        prayerName: String,
        prayerTime: String,
        mode: PrayerNotificationMode
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for Muslim Prayer Times"
                setSound(null, null) // Sound is controlled explicitly per mode
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 400, 200, 400)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = ACTION_STOP_ADHAN
            putExtra(EXTRA_ALARM_ID, notificationId)
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 100,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.icon)
            .setContentTitle("Time for $prayerName Prayer ($prayerTime)")
            .setContentText("Hayya 'ala-s-Salah — It is time for $prayerName prayer.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setSilent(true)
            .setContentIntent(pendingIntent)
            .setColor(ContextCompat.getColor(context, R.color.white))

        if (mode != PrayerNotificationMode.SILENT) {
            builder.addAction(
                android.R.drawable.ic_media_pause,
                "Dismiss Alert",
                stopPendingIntent
            )
        }

        notificationManager.notify(notificationId, builder.build())
    }

    /**
     * Plays a short notification/alarm tone (~4 seconds) instead of the full Adhan.
     */
    private fun playShortAlarmTone(context: Context) {
        try {
            stopActiveAdhan()
            val toneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val ringtone = RingtoneManager.getRingtone(context.applicationContext, toneUri)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ringtone?.audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            }
            activeRingtone = ringtone
            ringtone?.play()

            val stopTask = Runnable { stopActiveAdhan() }
            autoStopRunnable = stopTask
            mainHandler.postDelayed(stopTask, SHORT_TONE_DURATION_MS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Plays only the opening Takbeer snippet (~14 seconds) of the Adhan so it never plays the whole 3-minute audio.
     */
    private fun playShortAdhanSnippet(context: Context, prayerName: String) {
        try {
            stopActiveAdhan()
            val audioRes = if (prayerName.equals("Fajr", ignoreCase = true)) {
                R.raw.adzan_fajr
            } else {
                R.raw.adzan_makkah
            }
            val mediaPlayer = MediaPlayer.create(context.applicationContext, audioRes)
            activeMediaPlayer = mediaPlayer
            mediaPlayer?.setOnCompletionListener { mp ->
                mp.release()
                if (activeMediaPlayer === mp) {
                    activeMediaPlayer = null
                }
            }
            mediaPlayer?.start()

            val stopTask = Runnable { stopActiveAdhan() }
            autoStopRunnable = stopTask
            mainHandler.postDelayed(stopTask, SHORT_ADHAN_DURATION_MS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
