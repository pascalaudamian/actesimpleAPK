package com.example.actesimple.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.actesimple.data.model.Language
import com.example.actesimple.data.model.Procedure
import com.example.actesimple.data.model.ReminderItem
import com.example.actesimple.ui.dialogs.AddReminderDialog
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun RemindersScreen(
    language: Language,
    reminders: List<ReminderItem>,
    procedures: List<Procedure>,
    onAddReminder: (procedureId: String, procedureTitle: String, title: String, targetDate: String, presetDays: Int?, notes: String) -> Unit,
    onToggleReminder: (String) -> Unit,
    onDeleteReminder: (String) -> Unit,
    onSelectProcedureById: (String) -> Unit,
    onTestNotification: (title: String, message: String) -> Unit
) {
    val isRo = language == Language.RO
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("reminders_screen")
    ) {
        // Screen Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
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
                        text = if (isRo) "Mementouri & Termene" else "Reminders & Deadlines",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_reminder_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isRo) "Adaugă" else "Add")
                }
            }

            // Legal & civic disclaimer
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isRo)
                            "Notă: Mementourile sunt locale și pur informative. Nu înlocuiesc termenele legale obligatorii comunicate oficial de instituțiile statului."
                        else
                            "Notice: Reminders are local and purely informational. They do not replace statutory deadlines issued by official authorities.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (reminders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(54.dp)
                    )
                    Text(
                        text = if (isRo) "Niciun memento activ" else "No active reminders",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isRo)
                            "Programează alerte pentru reînnoirea buletinului, expirarea vizei de flotant sau plata taxelor anuale."
                        else
                            "Schedule alerts for renewing your ID, temporary residence permit expiry, or statutory payments.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showAddDialog = true },
                            modifier = Modifier.testTag("empty_add_reminder_cta")
                        ) {
                            Icon(imageVector = Icons.Default.AddAlert, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isRo) "Creează memento" else "Create reminder")
                        }

                        OutlinedButton(
                            onClick = {
                                onTestNotification(
                                    if (isRo) "ActeSimple: Test Notificare" else "ActeSimple: Test Notification",
                                    if (isRo) "Sistemul local de mementouri este activ și funcțional pe telefonul tău." else "Local reminders notification system is active and functional."
                                )
                            }
                        ) {
                            Text(if (isRo) "Test alertă" else "Test alert")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = reminders,
                    key = { it.id }
                ) { item ->
                    ReminderCard(
                        item = item,
                        language = language,
                        onToggle = { onToggleReminder(item.id) },
                        onDelete = { onDeleteReminder(item.id) },
                        onClickProcedure = { onSelectProcedureById(item.procedureId) }
                    )
                }

                item {
                    TextButton(
                        onClick = {
                            onTestNotification(
                                if (isRo) "ActeSimple: Notificare Memento" else "ActeSimple: Reminder Alert",
                                if (isRo) "Exemplu: Expiră cartea de identitate în 30 de zile. Verifică actele necesare!" else "Sample: ID card expires in 30 days. Check required documents!"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isRo) "Trimite o notificare de test pe telefon" else "Send a test notification to device")
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddReminderDialog(
            language = language,
            procedures = procedures,
            onDismiss = { showAddDialog = false },
            onSaveReminder = onAddReminder
        )
    }
}

@Composable
private fun ReminderCard(
    item: ReminderItem,
    language: Language,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onClickProcedure: () -> Unit
) {
    val isRo = language == Language.RO

    val daysRemaining = remember(item.targetDate) {
        try {
            val target = LocalDate.parse(item.targetDate)
            val today = LocalDate.now()
            ChronoUnit.DAYS.between(today, target)
        } catch (e: Exception) {
            null
        }
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isEnabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row: Procedure link, remaining days badge, toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Days countdown chip
                if (daysRemaining != null) {
                    val chipColor = when {
                        daysRemaining < 0 -> MaterialTheme.colorScheme.errorContainer
                        daysRemaining <= 30 -> MaterialTheme.colorScheme.errorContainer
                        daysRemaining <= 60 -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.primaryContainer
                    }
                    val textColor = when {
                        daysRemaining < 0 -> MaterialTheme.colorScheme.onErrorContainer
                        daysRemaining <= 30 -> MaterialTheme.colorScheme.onErrorContainer
                        daysRemaining <= 60 -> MaterialTheme.colorScheme.onTertiaryContainer
                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                    }

                    Surface(color = chipColor, shape = RoundedCornerShape(8.dp)) {
                        Text(
                            text = when {
                                daysRemaining < 0 -> if (isRo) "Expirat de ${-daysRemaining} zile" else "Expired ${-daysRemaining}d ago"
                                daysRemaining == 0L -> if (isRo) "Expiră azi!" else "Expires today!"
                                else -> if (isRo) "Termen în $daysRemaining zile" else "Due in ${daysRemaining}d"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)) {
                        Text(
                            text = item.targetDate,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = item.isEnabled,
                        onCheckedChange = { onToggle() },
                        modifier = Modifier.testTag("toggle_reminder_${item.id}")
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("delete_reminder_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Șterge memento",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Title
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (item.isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            // Target procedure link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onClickProcedure)
                    .padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = item.procedureTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Notes
            if (item.notes.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            // Target Date details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${if (isRo) "Data țintă:" else "Target date:"} ${item.targetDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (item.presetDays != null) {
                    Text(
                        text = "${item.presetDays} ${if (isRo) "zile înainte" else "days before"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
