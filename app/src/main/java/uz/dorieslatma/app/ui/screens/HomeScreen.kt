package uz.dorieslatma.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.dorieslatma.app.data.Medicine
import uz.dorieslatma.app.ui.MedicineViewModel
import uz.dorieslatma.app.ui.theme.DangerRed
import uz.dorieslatma.app.ui.theme.SuccessGreen
import uz.dorieslatma.app.ui.theme.WarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MedicineViewModel,
    onAddClick: () -> Unit,
    onItemClick: (Long) -> Unit
) {
    val medicines by viewModel.medicines.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Dori Eslatma", fontWeight = FontWeight.Bold)
                        Text(
                            "Sog'lig'ingiz nazoratda",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Yangi dori qo'shish", tint = Color.White)
            }
        }
    ) { padding ->
        if (medicines.isEmpty()) {
            EmptyState(Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(medicines, key = { it.id }) { med ->
                    MedicineCard(
                        medicine = med,
                        onClick = { onItemClick(med.id) },
                        onToggle = { viewModel.toggleActive(med) }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.Medication,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Hozircha dori qo'shilmagan",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Pastdagi + tugmasi orqali birinchi\ndorini qo'shing",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun MedicineCard(
    medicine: Medicine,
    onClick: () -> Unit,
    onToggle: () -> Unit
) {
    val stockRatio = if (medicine.totalStock > 0)
        (medicine.remainingStock / medicine.totalStock).toFloat().coerceIn(0f, 1f)
    else 0f

    val stockColor = when {
        medicine.totalStock <= 0 -> MaterialTheme.colorScheme.primary
        stockRatio > 0.5f -> SuccessGreen
        stockRatio > 0.2f -> WarningAmber
        else -> DangerRed
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Medication,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        medicine.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val sub = buildString {
                        if (medicine.dosage.isNotEmpty()) append(medicine.dosage)
                        if (medicine.dosage.isNotEmpty()) append(" • ")
                        append(medicine.mealRelationEnum().label)
                    }
                    Text(
                        sub,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Switch(
                    checked = medicine.isActive,
                    onCheckedChange = { onToggle() }
                )
            }

            Spacer(Modifier.height(12.dp))

            // Vaqtlar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (medicine.isActive) Icons.Filled.Notifications else Icons.Filled.NotificationsOff,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    medicine.timeList().joinToString("   ") { "🕐 $it" },
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }

            if (medicine.totalStock > 0) {
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Qolgan zaxira", fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text(
                        "${fmt(medicine.remainingStock)} / ${fmt(medicine.totalStock)} dona",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = stockColor
                    )
                }
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { stockRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = stockColor,
                    trackColor = stockColor.copy(alpha = 0.15f)
                )
                if (medicine.remainingStock <= medicine.amountPerDose && medicine.remainingStock >= 0) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "⚠️ Zaxira tugayapti, yangisini oling!",
                        fontSize = 12.sp,
                        color = DangerRed,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun fmt(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
