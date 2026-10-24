package com.example.actesimple.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.actesimple.data.model.ChecklistSummary
import com.example.actesimple.data.model.Language
import com.example.actesimple.data.model.Procedure
import com.example.actesimple.ui.components.ProcedureCardItem

@Composable
fun SavedGuidesScreen(
    language: Language,
    procedures: List<Procedure>,
    bookmarkedIds: Set<String>,
    summaries: List<ChecklistSummary>,
    onSelectProcedure: (Procedure) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onExploreClick: () -> Unit
) {
    val isRo = language == Language.RO
    val savedProcedures = procedures.filter { bookmarkedIds.contains(it.id) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("saved_guides_screen")
    ) {
        // Screen Header
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
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (isRo) "Ghiduri Salvate" else "Saved Guides",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = if (isRo)
                    "Procedurile tale favorite sunt disponibile offline oricând, chiar și fără conexiune la internet."
                else
                    "Your bookmarked procedures are saved locally and available offline anytime.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (savedProcedures.isEmpty()) {
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
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(54.dp)
                    )
                    Text(
                        text = if (isRo) "Nu ai niciun ghid salvat" else "No saved guides yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isRo)
                            "Apasă pe pictograma de marcaj pentru a salva orice ghid administrativ pentru acces rapid."
                        else
                            "Tap the bookmark icon on any procedure card to save it for quick offline access.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onExploreClick,
                        modifier = Modifier.testTag("explore_guides_cta")
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isRo) "Explorează proceduri" else "Explore procedures")
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = savedProcedures,
                    key = { it.id }
                ) { procedure ->
                    val summary = summaries.find { it.procedureId == procedure.id }
                    ProcedureCardItem(
                        procedure = procedure,
                        language = language,
                        summary = summary,
                        isBookmarked = true,
                        onToggleBookmark = { onToggleBookmark(procedure.id) },
                        onClick = { onSelectProcedure(procedure) }
                    )
                }
            }
        }
    }
}
