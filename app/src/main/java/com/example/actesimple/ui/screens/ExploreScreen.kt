package com.example.actesimple.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.actesimple.data.model.*
import com.example.actesimple.ui.components.CategoryFilterBar
import com.example.actesimple.ui.components.ProcedureCardItem
import com.example.actesimple.ui.components.ProfileFilterRow

@Composable
fun ExploreScreen(
    language: Language,
    searchQuery: String,
    selectedCategory: String,
    selectedProfile: ApplicantProfile,
    onlineOnly: Boolean,
    categories: List<CategoryMeta>,
    procedures: List<Procedure>,
    bookmarkedIds: Set<String>,
    summaries: List<ChecklistSummary>,
    onSearchChange: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onSelectProfile: (ApplicantProfile) -> Unit,
    onToggleOnlineOnly: () -> Unit,
    onResetFilters: () -> Unit,
    onSelectProcedure: (Procedure) -> Unit,
    onToggleBookmark: (String) -> Unit
) {
    val isRo = language == Language.RO
    val hasFilters = searchQuery.isNotBlank() || selectedCategory != "toate" || selectedProfile != ApplicantProfile.ALL || onlineOnly

    LazyColumn(
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("explore_screen_content")
    ) {
        // Search bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (isRo) "Catalog Proceduri Administrative" else "Administrative Procedures Catalog",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            if (isRo) "Caută buletin, cazier, permis, viză, SRL, flotant..."
                            else "Search ID card, criminal record, driving, visa, SRL...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Căutare")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Șterge")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("explore_search_input"),
                    singleLine = true
                )
            }
        }

        // Category filter chips
        item {
            CategoryFilterBar(
                language = language,
                categories = categories,
                selectedCategory = selectedCategory,
                onSelectCategory = onSelectCategory
            )
        }

        // Profile filter & Online toggle row
        item {
            ProfileFilterRow(
                language = language,
                selectedProfile = selectedProfile,
                onlineOnly = onlineOnly,
                procedureCount = procedures.size,
                onSelectProfile = onSelectProfile,
                onToggleOnlineOnly = onToggleOnlineOnly,
                onResetFilters = onResetFilters,
                hasActiveFilters = hasFilters
            )
        }

        // Empty state or list
        if (procedures.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isRo) "Nicio procedură găsită" else "No procedures found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isRo)
                            "Încearcă să schimbi termenii de căutare sau să resetezi filtrele de profil."
                        else
                            "Try modifying your search terms or resetting profile filters.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onResetFilters) {
                        Text(if (isRo) "Resetează toate filtrele" else "Reset all filters")
                    }
                }
            }
        } else {
            items(
                items = procedures,
                key = { it.id }
            ) { procedure ->
                val summary = summaries.find { it.procedureId == procedure.id }
                val isSaved = bookmarkedIds.contains(procedure.id)
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    ProcedureCardItem(
                        procedure = procedure,
                        language = language,
                        summary = summary,
                        isBookmarked = isSaved,
                        onToggleBookmark = { onToggleBookmark(procedure.id) },
                        onClick = { onSelectProcedure(procedure) }
                    )
                }
            }
        }
    }
}
