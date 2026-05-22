package uz.dorieslatma.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import uz.dorieslatma.app.data.MealRelation
import uz.dorieslatma.app.data.Medicine
import uz.dorieslatma.app.ui.MedicineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    viewModel: MedicineViewModel,
    medicineId: Long,
    onBack: () -> Unit
) {
    val isNew = medicineId == 0L
    var loaded by remember { mutableStateOf(isNew) }

    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var meal by remember { mutableStateOf(MealRelation.NONE) }
    var amountPerDose by remember { mutableStateOf("1") }
    var durationDays by remember { mutableStateOf("7") }
    var totalStock by remember { mutableStateOf("") }
    val times = remember { mutableStateListOf("08:00") }
    var existing by remember { mutableStateOf<Medicine?>(null) }

    var showTimePicker by remember { mutableStateOf(false) }
    var editingTimeIndex by remember { mutableStateOf(-1) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Tahrirlash uchun mavjud ma'lumotni yuklash
    LaunchedEffect(medicineId) {
        if (!isNew) {
            val med = viewModel.getById(medicineId)
            if (med != null) {
                existing = med
                name = med.name
                dosage = med.dosage
                meal = med.mealRelationEnum()
                amountPerDose = fmt(med.amountPerDose)
                durationDays = med.durationDays.toString()
                totalStock = if (med.totalStock > 0) fmt(med.totalStock) else ""
                times.clear()
                times.addAll(med.timeList())
            }
            loaded = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNew) "Yangi dori" else "Tahrirlash", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Orqaga")
                    }
                },
                actions = {
                    if (!isNew) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "O'chirish")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionCard("Dori haqida") {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Dori nomi *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { dosage = it },
                        label = { Text("Doza (masalan: 500 mg)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            item {
                SectionCard("Ovqatga nisbatan") {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MealRelation.values().forEach { rel ->
                            FilterChip(
                                selected = meal == rel,
                                onClick = { meal = rel },
                                label = { Text(rel.label, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            item {
                SectionCard("Eslatma vaqtlari (necha mahal)") {
                    times.forEachIndexed { index, t ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AssistChip(
                                onClick = {
                                    editingTimeIndex = index
                                    showTimePicker = true
                                },
                                label = { Text("🕐 $t", fontSize = 15.sp) }
                            )
                            Spacer(Modifier.weight(1f))
                            if (times.size > 1) {
                                IconButton(onClick = { times.removeAt(index) }) {
                                    Icon(Icons.Filled.Close, contentDescription = "O'chirish",
                                        modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            editingTimeIndex = -1
                            showTimePicker = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null,
                            modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Vaqt qo'shish")
                    }
                }
            }

            item {
                SectionCard("Miqdor va muddat") {
                    OutlinedTextField(
                        value = amountPerDose,
                        onValueChange = { amountPerDose = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Har qabulda nechta dona") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = durationDays,
                        onValueChange = { durationDays = it.filter { c -> c.isDigit() } },
                        label = { Text("Necha kun davom etadi") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = totalStock,
                        onValueChange = { totalStock = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Jami zaxira (ixtiyoriy, nechta dona bor)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        if (name.isBlank()) return@Button
                        val total = totalStock.toDoubleOrNull() ?: 0.0
                        val med = (existing ?: Medicine(name = "")).copy(
                            id = medicineId,
                            name = name.trim(),
                            dosage = dosage.trim(),
                            mealRelation = meal.name,
                            times = times.joinToString(","),
                            amountPerDose = amountPerDose.toDoubleOrNull() ?: 1.0,
                            durationDays = durationDays.toIntOrNull() ?: 7,
                            totalStock = total,
                            // Yangi dorida qolgan = jami; tahrirda total o'zgarmasa eski qoladi
                            remainingStock = if (isNew) total
                                else (existing?.let {
                                    if (it.totalStock != total) total else it.remainingStock
                                } ?: total)
                        )
                        viewModel.saveMedicine(med) { onBack() }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        if (isNew) "Saqlash va eslatmani yoqish" else "O'zgarishlarni saqlash",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (showTimePicker) {
        val initial = if (editingTimeIndex >= 0) times[editingTimeIndex] else "08:00"
        val parts = initial.split(":")
        val state = rememberTimePickerState(
            initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 8,
            initialMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val formatted = "%02d:%02d".format(state.hour, state.minute)
                    if (editingTimeIndex >= 0) {
                        times[editingTimeIndex] = formatted
                    } else {
                        times.add(formatted)
                    }
                    showTimePicker = false
                }) { Text("Tanlash") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Bekor") }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Eslatma vaqtini tanlang", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    TimePicker(state = state)
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("O'chirish") },
            text = { Text("Ushbu dorini ro'yxatdan o'chirmoqchimisiz? Eslatmalar ham bekor qilinadi.") },
            confirmButton = {
                TextButton(onClick = {
                    existing?.let { viewModel.deleteMedicine(it) }
                    showDeleteDialog = false
                    onBack()
                }) { Text("O'chirish", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Bekor") }
            }
        )
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

private fun fmt(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
