package uz.dorieslatma.app.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.dorieslatma.app.data.MedicineRepository

/**
 * Telefon o'chib yonganda barcha alarmlar o'chib ketadi.
 * Shu sababli boot tugagach hammasini qayta o'rnatamiz.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val repo = MedicineRepository(context)
                    repo.getAllActive().forEach { med ->
                        if (!med.isFinished()) {
                            AlarmScheduler.scheduleMedicine(context, med)
                        }
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
