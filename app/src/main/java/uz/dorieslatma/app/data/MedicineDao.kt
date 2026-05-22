package uz.dorieslatma.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {

    @Query("SELECT * FROM medicines ORDER BY isActive DESC, name ASC")
    fun getAllFlow(): Flow<List<Medicine>>

    @Query("SELECT * FROM medicines WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Medicine?

    @Query("SELECT * FROM medicines WHERE isActive = 1")
    suspend fun getAllActive(): List<Medicine>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medicine: Medicine): Long

    @Update
    suspend fun update(medicine: Medicine)

    @Delete
    suspend fun delete(medicine: Medicine)

    @Query("UPDATE medicines SET remainingStock = :remaining WHERE id = :id")
    suspend fun updateRemaining(id: Long, remaining: Double)
}
