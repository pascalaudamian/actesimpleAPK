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
import com.example.actesimple.data.model.Language

@Composable
fun FeedbackReportDialog(
    language: Language,
    onDismiss: () -> Unit,
    onSubmit: (category: String, message: String) -> Unit
) {
    val isRo = language == Language.RO

    var feedbackCategory by remember { mutableStateOf("outdated_info") }
    var userMessage by remember { mutableStateOf("") }
    var procedureName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            imageVector = Icons.Default.Feedback,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isRo) "Raportează o Schimbare" else "Report a Change / Feedback",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Închide")
                    }
                }

                Text(
                    text = if (isRo)
                        "Ai observat o taxă schimbată, un formular actualizat sau o cerință nouă la ghișeu? Echipa civică ActeSimple verifică fiecare raport cu legislația oficială în vigoare."
                    else
                        "Noticed a changed fee, updated official form or new counter requirement? The ActeSimple civic team verifies every report against the official statutory gazette.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Category Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isRo) "Tipul semnalării:" else "Report type:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = feedbackCategory == "outdated_info",
                            onClick = { feedbackCategory = "outdated_info" },
                            label = { Text(if (isRo) "Informație veche" else "Outdated info") }
                        )
                        FilterChip(
                            selected = feedbackCategory == "new_law",
                            onClick = { feedbackCategory = "new_law" },
                            label = { Text(if (isRo) "Lege nouă" else "New legislation") }
                        )
                        FilterChip(
                            selected = feedbackCategory == "suggestion",
                            onClick = { feedbackCategory = "suggestion" },
                            label = { Text(if (isRo) "Sugestie" else "Suggestion") }
                        )
                    }
                }

                OutlinedTextField(
                    value = procedureName,
                    onValueChange = { procedureName = it },
                    label = { Text(if (isRo) "Procedura vizată (ex: Buletin, Înmatriculare)" else "Target procedure (e.g., ID card, Plates)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = userMessage,
                    onValueChange = { userMessage = it },
                    label = { Text(if (isRo) "Descrie modificarea sau linkul oficial" else "Describe change or official link") },
                    placeholder = { Text(if (isRo) "Ex: Taxa s-a modificat conform HG... sau ghișeul s-a mutat pe strada..." else "e.g., Statutory fee changed per decree...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("feedback_message_input"),
                    minLines = 3,
                    maxLines = 5
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(if (isRo) "Anulează" else "Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (userMessage.isNotBlank()) {
                                onSubmit(feedbackCategory, "[$procedureName] $userMessage")
                                onDismiss()
                            }
                        },
                        modifier = Modifier.testTag("submit_feedback_button")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isRo) "Trimite Raportul" else "Submit Report")
                    }
                }
            }
        }
    }
}
