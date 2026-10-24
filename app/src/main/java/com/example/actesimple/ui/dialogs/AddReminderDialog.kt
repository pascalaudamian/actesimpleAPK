package com.example.actesimple.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.actesimple.data.model.Language
import com.example.actesimple.data.model.Procedure
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    language: Language,
    procedures: List<Procedure>,
    preselectedProcedure: Procedure? = null,
    onDismiss: () -> Unit,
    onSaveReminder: (procedureId: String, procedureTitle: String, title: String, targetDate: String, presetDays: Int?, notes: String) -> Unit
) {
    val isRo = language == Language.RO

    var selectedProc by remember { mutableStateOf(preselectedProcedure ?: procedures.firstOrNull()) }
    var reminderTitle by remember {
        mutableStateOf(
            if (isRo) "Expirare / Reînnoire ${preselectedProcedure?.title ?: "document"}"
            else "Expiry / Renewal of ${preselectedProcedure?.titleEn ?: preselectedProcedure?.title ?: "document"}"
        )
    }
    var targetDate by remember {
        mutableStateOf(
            preselectedProcedure?.expiryDate ?: LocalDate.now().plusMonths(6).toString()
        )
    }
    var selectedPreset by remember { mutableStateOf<Int?>(30) }
    var notes by remember { mutableStateOf("") }
    var showProcDropdown by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isRo) "Adaugă Memento Termen" else "Add Deadline Reminder",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Închide")
                    }
                }

                // Info banner
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isRo)
                                "Mementourile sunt stocate local pe dispozitivul tău și sunt pur informative."
                            else
                                "Reminders are stored locally on your device and are strictly informational.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Select Procedure
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (isRo) "Procedura vizată:" else "Target procedure:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedCard(
                        onClick = { showProcDropdown = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedProc?.let { if (isRo) it.title else it.titleEn ?: it.title }
                                    ?: (if (isRo) "Selectează o procedură" else "Select a procedure"),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    if (showProcDropdown) {
                        DropdownMenu(
                            expanded = showProcDropdown,
                            onDismissRequest = { showProcDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            procedures.forEach { proc ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = if (isRo) proc.title else proc.titleEn ?: proc.title,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    },
                                    onClick = {
                                        selectedProc = proc
                                        reminderTitle = if (isRo) "Expirare / Reînnoire ${proc.title}"
                                        else "Expiry / Renewal of ${proc.titleEn ?: proc.title}"
                                        if (!proc.expiryDate.isNullOrBlank()) {
                                            targetDate = proc.expiryDate
                                        }
                                        showProcDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Reminder Title
                OutlinedTextField(
                    value = reminderTitle,
                    onValueChange = { reminderTitle = it },
                    label = { Text(if (isRo) "Titlu memento" else "Reminder title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reminder_title_input"),
                    singleLine = true
                )

                // Target Date Input
                OutlinedTextField(
                    value = targetDate,
                    onValueChange = { targetDate = it },
                    label = { Text(if (isRo) "Data termenului limită (AAAA-LL-ZZ)" else "Target deadline date (YYYY-MM-DD)") },
                    placeholder = { Text("2026-12-31") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reminder_date_input"),
                    singleLine = true
                )

                // Presets: 90, 60, 30, 7 days before
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isRo) "Avertizare înainte de termen:" else "Alert before deadline:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(90, 60, 30, 7).forEach { days ->
                            FilterChip(
                                selected = selectedPreset == days,
                                onClick = {
                                    selectedPreset = if (selectedPreset == days) null else days
                                },
                                label = { Text("${days} ${if (isRo) "zile" else "days"}") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(if (isRo) "Notițe personale (opțional)" else "Personal notes (optional)") },
                    placeholder = {
                        Text(if (isRo) "Ex: Programat la SPCLEP Sector 1 la ora 10:30" else "e.g., Appointment at SPCLEP 10:30 AM")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(if (isRo) "Anulează" else "Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val proc = selectedProc ?: procedures.firstOrNull()
                            if (proc != null && reminderTitle.isNotBlank() && targetDate.isNotBlank()) {
                                onSaveReminder(
                                    proc.id,
                                    if (isRo) proc.title else proc.titleEn ?: proc.title,
                                    reminderTitle.trim(),
                                    targetDate.trim(),
                                    selectedPreset,
                                    notes.trim()
                                )
                                onDismiss()
                            }
                        },
                        modifier = Modifier.testTag("save_reminder_button")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isRo) "Salvează Memento" else "Save Reminder")
                    }
                }
            }
        }
    }
}
