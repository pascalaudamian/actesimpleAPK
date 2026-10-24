package com.example.actesimple.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.actesimple.data.model.Language

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    language: Language,
    savedCount: Int,
    isDetailOpen: Boolean,
    remindersCount: Int = 0,
    onBack: () -> Unit,
    onToggleLanguage: () -> Unit,
    onOpenSavedDossiers: () -> Unit,
    onOpenOfficialPortals: () -> Unit,
    onOpenVerificationInfo: () -> Unit,
    onOpenReminders: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "ActeSimple Logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
                Column {
                    Text(
                        text = "ActeSimple",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (language == Language.RO) "Ghid Administrativ Oficial" else "Romanian Civic Guide",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        navigationIcon = {
            if (isDetailOpen) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("top_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Înapoi la proceduri"
                    )
                }
            }
        },
        actions = {
            // Language Toggle Button (RO / EN)
            OutlinedButton(
                onClick = onToggleLanguage,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("language_toggle_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Schimbă limba",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (language == Language.RO) "EN" else "RO",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Official Portals Directory
            IconButton(
                onClick = onOpenOfficialPortals,
                modifier = Modifier.testTag("official_portals_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = "Portaluri Oficiale"
                )
            }

            // Saved Checklists / Dossiers with Badge
            IconButton(
                onClick = onOpenSavedDossiers,
                modifier = Modifier.testTag("saved_dossiers_button")
            ) {
                BadgedBox(
                    badge = {
                        if (savedCount > 0) {
                            Badge { Text("$savedCount") }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Dosarele mele salvate"
                    )
                }
            }

            // Reminders with Badge
            if (onOpenReminders != null) {
                IconButton(
                    onClick = onOpenReminders,
                    modifier = Modifier.testTag("top_bar_reminders_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (remindersCount > 0) {
                                Badge { Text("$remindersCount") }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = "Mementouri termene"
                        )
                    }
                }
            }

            // Settings
            if (onOpenSettings != null) {
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.testTag("top_bar_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Setări & Transparență"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}
