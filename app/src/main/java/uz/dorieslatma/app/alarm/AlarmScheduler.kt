package uz.dorieslatma.app.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import uz.dorieslatma.app.data.Medicine
import java.util.Calendar

/**
 * Har bir dori uchun belgilangan vaqtlarda kunlik takrorlanuvchi eslatma o'rnatadi.
 * AlarmManager ishlatiladi - telefon uxlab yotsa ham ishlaydi.
 */
object AlarmScheduler {

    const val EXTRA_MED_ID = "med_id"
    const val EXTRA_MED_NAME = "med_name"
    const val EXTRA_MED_DOSAGE = "med_dosage"
    const val EXTRA_MED_MEAL = "med_meal"
    const val EXTRA_TIME_INDEX = "time_index"

    /**
     * Har bir vaqt uchun alohida requestCode hosil qilamiz.
     * id * 100 + timeIndex -> noyob bo'ladi.
     */
    private fun requestCode(medId: Long, timeIndex: Int): Int =
        (medId * 100 + timeIndex).toInt()

    fun scheduleMedicine(context: Context, medicine: Medicine) {
        if (!medicine.isActive) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val times = medicine.timeList()

        times.forEachIndexed { index, time ->
            val parts = time.split(":")
            if (parts.size != 2) return@forEachIndexed
            val hour = parts[0].toIntOrNull() ?: return@forEachIndexed
            val minute = parts[1].toIntOrNull() ?: return@forEachIndexed

            val triggerAt = nextTriggerMillis(hour, minute)

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra(EXTRA_MED_ID, medicine.id)
                putExtra(EXTRA_MED_NAME, medicine.name)
                putExtra(EXTRA_MED_DOSAGE, medicine.dosage)
                putExtra(EXTRA_MED_MEAL, medicine.mealRelationEnum().label)
                putExtra(EXTRA_TIME_INDEX, index)
            }

            val pending = PendingIntent.getBroadcast(
                context,
                requestCode(medicine.id, index),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Kunlik takrorlanuvchi, aniq vaqtli alarm
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAt,
                    pending
                )
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            }
        }
    }

    fun cancelMedicine(context: Context, medicine: Medicine) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        medicine.timeList().forEachIndexed { index, _ ->
            val intent = Intent(context, AlarmReceiver::class.java)
            val pending = PendingIntent.getBroadcast(
                context,
                requestCode(medicine.id, index),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pending)
        }
    }

    /** Keyingi mos keladigan vaqtni (bugun yoki ertaga) hisoblaydi */
    private fun nextTriggerMillis(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_MONTH, 1)
        }
        return target.timeInMillis
    }
}
