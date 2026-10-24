package com.example.actesimple.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.actesimple.data.model.*
import com.example.actesimple.ui.components.HeroSearchSection
import com.example.actesimple.ui.components.ProcedureCardItem

@Composable
fun HomeScreen(
    language: Language,
    searchQuery: String,
    procedures: List<Procedure>,
    categories: List<CategoryMeta>,
    summaries: List<ChecklistSummary>,
    bookmarkedIds: Set<String>,
    onSearchChange: (String) -> Unit,
    onQuickSearch: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onSelectProcedure: (Procedure) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onStartWizard: () -> Unit,
    onNavigateToTab: (AppNavigationTab) -> Unit
) {
    val isRo = language == Language.RO

    LazyColumn(
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_content")
    ) {
        // 1. Hero & Search Section
        item {
            HeroSearchSection(
                language = language,
                searchQuery = searchQuery,
                onSearchChange = onSearchChange,
                onQuickSearch = onQuickSearch,
                onStartWizard = onStartWizard
            )
        }

        // 2. Quick Navigation Shortcuts Grid (Explore, Wizard, Checklist, Reminders)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isRo) "Acces Rapid" else "Quick Access",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        icon = Icons.Default.Search,
                        title = if (isRo) "Explorează" else "Explore",
                        subtitle = if (isRo) "${procedures.size} proceduri" else "${procedures.size} guides",
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_explore"),
                        onClick = { onNavigateToTab(AppNavigationTab.EXPLORE) }
                    )
                    QuickActionTile(
                        icon = Icons.Default.Assistant,
                        title = if (isRo) "Asistent" else "Wizard",
                        subtitle = if (isRo) "Ghid pas-cu-pas" else "Step-by-step",
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_wizard"),
                        onClick = { onNavigateToTab(AppNavigationTab.WIZARD) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        icon = Icons.Default.Checklist,
                        title = if (isRo) "Dosarele Mele" else "Checklists",
                        subtitle = if (isRo) "${summaries.size} în lucru" else "${summaries.size} in progress",
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_checklists"),
                        onClick = { onNavigateToTab(AppNavigationTab.CHECKLIST) }
                    )
                    QuickActionTile(
                        icon = Icons.Default.Alarm,
                        title = if (isRo) "Mementouri" else "Reminders",
                        subtitle = if (isRo) "Alerte termene" else "Deadline alerts",
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_reminders"),
                        onClick = { onNavigateToTab(AppNavigationTab.REMINDERS) }
                    )
                }
            }
        }

        // 3. Active Dossiers in Progress (if any)
        if (summaries.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onNavigateToTab(AppNavigationTab.CHECKLIST) }
                        .testTag("active_dossiers_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.secondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChecklistRtl,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isRo) "Dosare active în pregătire" else "Active checklists in progress",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isRo)
                                    "Ai ${summaries.size} dosare salvate local. Continuă completarea documentelor."
                                else
                                    "You have ${summaries.size} saved dossiers. Tap to manage documents.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 4. International Citizens Category Shortcuts
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isRo) "Categorii Principale" else "Top Categories",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = { onNavigateToTab(AppNavigationTab.EXPLORE) }) {
                        Text(if (isRo) "Vezi toate" else "View all", style = MaterialTheme.typography.labelMedium)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.take(6).forEach { category ->
                        ElevatedFilterChip(
                            selected = false,
                            onClick = {
                                onSelectCategory(category.id)
                                onNavigateToTab(AppNavigationTab.EXPLORE)
                            },
                            label = {
                                Text(
                                    text = if (isRo) category.nameRo else category.nameEn,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            modifier = Modifier.testTag("home_cat_${category.id}")
                        )
                    }
                }
            }
        }

        // 5. Popular Procedures List Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isRo) "Proceduri Frecvente Verificate" else "Popular Verified Procedures",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { onNavigateToTab(AppNavigationTab.EXPLORE) }) {
                        Text(if (isRo) "Toate (${procedures.size})" else "All (${procedures.size})")
                    }
                }
                Text(
                    text = if (isRo)
                        "Informații verificate editorial la instituțiile emitente din România."
                    else
                        "Editorially verified guidance for institutions across Romania.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 6. Popular Procedure Cards
        val popularProcedures = procedures.take(5)
        items(popularProcedures, key = { it.id }) { procedure ->
            val summary = summaries.find { it.procedureId == procedure.id }
            Box(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            ) {
                ProcedureCardItem(
                    procedure = procedure,
                    language = language,
                    summary = summary,
                    isBookmarked = bookmarkedIds.contains(procedure.id),
                    onToggleBookmark = { onToggleBookmark(procedure.id) },
                    onClick = { onSelectProcedure(procedure) }
                )
            }
        }

        // 7. Civic Disclaimer Banner at bottom
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("home_civic_disclaimer"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (isRo) "Avertisment Civic și Juridic" else "Civic & Legal Notice",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isRo)
                                "ActeSimple este o inițiativă civică independentă, fără afiliere guvernamentală. Aplicația nu solicită și nu reține CNP, parole sau documente confidențiale. Pentru autentificare electronică oficială și depunere, utilizați portalurile oficiale ale instituțiilor competente."
                            else
                                "ActeSimple is an independent civic initiative with no government affiliation. The app never captures CNP, passwords, or confidential document scans. For official electronic submission and signatures, proceed directly to official institutional portals.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.8f)
            )
        }
    }
}
