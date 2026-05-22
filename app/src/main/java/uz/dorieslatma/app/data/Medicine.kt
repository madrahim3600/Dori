package uz.dorieslatma.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Dori haqidagi asosiy ma'lumot.
 * Barcha ma'lumotlar telefon xotirasida (Room DB) saqlanadi - internet kerak emas.
 */
@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Dori nomi, masalan "Paratsetamol"
    val name: String,

    // Qo'shimcha izoh / doza, masalan "500 mg"
    val dosage: String = "",

    // Ovqatga nisbatan: BEFORE / AFTER / DURING / NONE
    val mealRelation: String = MealRelation.NONE.name,

    // Eslatma vaqtlari "HH:mm" formatida, vergul bilan ajratilgan. Masalan "08:00,14:00,20:00"
    val times: String = "08:00",

    // Har bir qabulda nechta dona/qoshiq ichiladi
    val amountPerDose: Double = 1.0,

    // Davolanish necha kun davom etadi
    val durationDays: Int = 7,

    // Boshlangan sana (millis)
    val startDateMillis: Long = System.currentTimeMillis(),

    // Boshlang'ich umumiy dori zaxirasi (nechta dona bor edi)
    val totalStock: Double = 0.0,

    // Hozir qolgan zaxira (har qabulda kamayadi)
    val remainingStock: Double = 0.0,

    // Eslatma yoqilganmi
    val isActive: Boolean = true
) {
    fun timeList(): List<String> =
        times.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    fun mealRelationEnum(): MealRelation =
        runCatching { MealRelation.valueOf(mealRelation) }.getOrDefault(MealRelation.NONE)

    /** Davolanish tugash sanasi (millis) */
    fun endDateMillis(): Long =
        startDateMillis + durationDays.toLong() * 24L * 60L * 60L * 1000L

    /** Davolanish tugaganmi */
    fun isFinished(): Boolean = System.currentTimeMillis() > endDateMillis()
}

enum class MealRelation(val label: String) {
    BEFORE("Ovqatdan oldin"),
    AFTER("Ovqatdan keyin"),
    DURING("Ovqat orasida"),
    NONE("Farqi yo'q")
}
