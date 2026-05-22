package uz.dorieslatma.app.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class MedicineRepository(context: Context) {
    private val dao = AppDatabase.get(context).medicineDao()

    fun getAllFlow(): Flow<List<Medicine>> = dao.getAllFlow()

    suspend fun getById(id: Long): Medicine? = dao.getById(id)

    suspend fun getAllActive(): List<Medicine> = dao.getAllActive()

    suspend fun insert(medicine: Medicine): Long = dao.insert(medicine)

    suspend fun update(medicine: Medicine) = dao.update(medicine)

    suspend fun delete(medicine: Medicine) = dao.delete(medicine)

    /** Bir qabul amalga oshganda zaxirani kamaytirish */
    suspend fun consumeDose(medicine: Medicine) {
        val newRemaining = (medicine.remainingStock - medicine.amountPerDose).coerceAtLeast(0.0)
        dao.updateRemaining(medicine.id, newRemaining)
    }
}
