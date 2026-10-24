package com.example.actesimple.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.actesimple.data.model.ChecklistSummary
import com.example.actesimple.data.model.Language
import com.example.actesimple.data.model.Procedure

@Composable
fun ChecklistScreen(
    language: Language,
    summaries: List<ChecklistSummary>,
    procedures: List<Procedure>,
    onSelectProcedure: (Procedure) -> Unit,
    onExploreClick: () -> Unit,
    onStartWizard: () -> Unit
) {
    val isRo = language == Language.RO
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("my_checklist_screen")
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Checklist,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (isRo) "Dosarele Mele de Acte" else "My Document Checklists",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = if (isRo)
                    "Urmărește documentele bifate, programările la ghișeu și notițele tale salvate pe telefon."
                else
                    "Track completed documents, scheduled appointments, and personal notes saved on your device.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (summaries.isEmpty()) {
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
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(54.dp)
                    )
                    Text(
                        text = if (isRo) "Niciun dosar activ încă" else "No active checklists yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isRo)
                            "Când deschizi un ghid și bifezi acte sau adaugi notițe, dosarul tău va apărea aici automat."
                        else
                            "When you open any procedure guide and check items or add notes, your dossier appears here automatically.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = onExploreClick) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isRo) "Găsește o procedură" else "Find a procedure")
                        }
                        OutlinedButton(onClick = onStartWizard) {
                            Text(if (isRo) "Asistent" else "Wizard")
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
                    items = summaries,
                    key = { it.procedureId }
                ) { summary ->
                    val procedure = procedures.find { it.id == summary.procedureId }
                    if (procedure != null) {
                        DossierCard(
                            summary = summary,
                            procedure = procedure,
                            language = language,
                            onClick = { onSelectProcedure(procedure) },
                            onCopySummary = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                val text = if (isRo)
                                    "Dosar ${procedure.title}:\n${summary.completedCount} din ${summary.totalCount} acte pregătite pe ActeSimple."
                                else
                                    "Dossier ${procedure.titleEn ?: procedure.title}:\n${summary.completedCount} of ${summary.totalCount} documents prepared on ActeSimple."
                                val clip = ClipData.newPlainText("Dossier summary", text)
                                clipboard?.setPrimaryClip(clip)
                                Toast.makeText(context, if (isRo) "Rezumat copiat!" else "Summary copied!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DossierCard(
    summary: ChecklistSummary,
    procedure: Procedure,
    language: Language,
    onClick: () -> Unit,
    onCopySummary: () -> Unit
) {
    val isRo = language == Language.RO
    val title = if (isRo) procedure.title else procedure.titleEn ?: procedure.title
    val progress = if (summary.totalCount > 0) summary.completedCount.toFloat() / summary.totalCount.toFloat() else 0f
    val isComplete = summary.completedCount >= summary.totalCount && summary.totalCount > 0

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (isComplete) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isComplete) (if (isRo) "DOSAR COMPLET" else "DOSSIER READY") else procedure.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isComplete) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "${summary.completedCount} / ${summary.totalCount} ${if (isRo) "acte" else "docs"}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isComplete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isComplete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCopySummary) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copiază rezumat", modifier = Modifier.size(18.dp))
                }

                Button(
                    onClick = onClick,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(if (isRo) "Continuă dosarul" else "Open dossier")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
