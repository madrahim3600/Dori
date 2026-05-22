package uz.dorieslatma.app.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.dorieslatma.app.MainActivity
import uz.dorieslatma.app.R
import uz.dorieslatma.app.data.MedicineRepository

/**
 * Eslatma vaqti kelganda chaqiriladi:
 *  - Ovozli bildirishnoma chiqaradi
 *  - Zaxirani kamaytiradi
 *  - Keyingi kun uchun alarmni qayta o'rnatadi (kunlik takror)
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medId = intent.getLongExtra(AlarmScheduler.EXTRA_MED_ID, -1L)
        val name = intent.getStringExtra(AlarmScheduler.EXTRA_MED_NAME) ?: "Dori"
        val dosage = intent.getStringExtra(AlarmScheduler.EXTRA_MED_DOSAGE) ?: ""
        val meal = intent.getStringExtra(AlarmScheduler.EXTRA_MED_MEAL) ?: ""
        val timeIndex = intent.getIntExtra(AlarmScheduler.EXTRA_TIME_INDEX, 0)

        showNotification(context, medId, name, dosage, meal, timeIndex)

        // Zaxirani kamaytirish va keyingi kunга qayta rejalashtirish
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = MedicineRepository(context)
                val med = repo.getById(medId)
                if (med != null && med.isActive) {
                    repo.consumeDose(med)
                    // Davolanish tugamagan bo'lsa, ertangi kunga qayta o'rnatamiz
                    if (!med.isFinished()) {
                        AlarmScheduler.scheduleMedicine(context, med)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(
        context: Context,
        medId: Long,
        name: String,
        dosage: String,
        meal: String,
        timeIndex: Int
    ) {
        createChannel(context)

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPending = PendingIntent.getActivity(
            context,
            (medId * 100 + timeIndex).toInt(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "💊 Dori vaqti: $name"
        val body = buildString {
            if (dosage.isNotEmpty()) append("$dosage. ")
            if (meal.isNotEmpty()) append(meal)
        }.ifEmpty { "Dorini ichish vaqti keldi" }

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_pill)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(contentPending)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 500, 250, 500, 250, 500))
            .build()

        val notifId = (medId * 100 + timeIndex).toInt()
        try {
            NotificationManagerCompat.from(context).notify(notifId, notification)
        } catch (_: SecurityException) {
            // POST_NOTIFICATIONS ruxsati berilmagan bo'lishi mumkin
        }
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val audioAttrs = android.media.AudioAttributes.Builder()
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                .build()
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Dori eslatmalari",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Dori ichish vaqti haqida eslatmalar"
                enableVibration(true)
                setSound(soundUri, audioAttrs)
            }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "dori_eslatma_channel"
    }
}
