package uz.dorieslatma.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.dorieslatma.app.alarm.AlarmScheduler
import uz.dorieslatma.app.data.Medicine
import uz.dorieslatma.app.data.MedicineRepository

class MedicineViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MedicineRepository(app)

    val medicines: StateFlow<List<Medicine>> =
        repo.getAllFlow().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveMedicine(medicine: Medicine, onDone: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            if (medicine.id == 0L) {
                val newId = repo.insert(medicine)
                val saved = medicine.copy(id = newId)
                AlarmScheduler.scheduleMedicine(context, saved)
                onDone(newId)
            } else {
                // Avval eski alarmlarni bekor qilamiz, keyin yangisini o'rnatamiz
                AlarmScheduler.cancelMedicine(context, medicine)
                repo.update(medicine)
                if (medicine.isActive) {
                    AlarmScheduler.scheduleMedicine(context, medicine)
                }
                onDone(medicine.id)
            }
        }
    }

    fun deleteMedicine(medicine: Medicine) {
        viewModelScope.launch {
            AlarmScheduler.cancelMedicine(getApplication(), medicine)
            repo.delete(medicine)
        }
    }

    fun toggleActive(medicine: Medicine) {
        val updated = medicine.copy(isActive = !medicine.isActive)
        saveMedicine(updated)
    }

    suspend fun getById(id: Long): Medicine? = repo.getById(id)
}
