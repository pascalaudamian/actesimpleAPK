package com.example.actesimple.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.actesimple.data.model.ApplicantProfile
import com.example.actesimple.data.model.Language

@Composable
fun ProfileFilterRow(
    language: Language,
    selectedProfile: ApplicantProfile,
    onlineOnly: Boolean,
    procedureCount: Int,
    onSelectProfile: (ApplicantProfile) -> Unit,
    onToggleOnlineOnly: () -> Unit,
    onResetFilters: () -> Unit,
    hasActiveFilters: Boolean
) {
    val isRo = language == Language.RO

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Horizontal profiles and online chip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val profiles = listOf(
                ApplicantProfile.ALL,
                ApplicantProfile.ROMANIAN,
                ApplicantProfile.EU_EEA_CH,
                ApplicantProfile.NON_EU
            )

            profiles.forEach { profile ->
                val isSelected = selectedProfile == profile
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectProfile(profile) },
                    label = {
                        Text(
                            text = if (isRo) profile.labelRo else profile.labelEn,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    leadingIcon = {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.testTag("profile_chip_${profile.id}")
                )
            }

            // Online Only Toggle Chip
            FilterChip(
                selected = onlineOnly,
                onClick = onToggleOnlineOnly,
                label = {
                    Text(
                        text = if (isRo) "Disponibil Online" else "Online Available",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.tertiary,
                    selectedLabelColor = MaterialTheme.colorScheme.onTertiary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onTertiary
                ),
                modifier = Modifier.testTag("online_only_filter_chip")
            )
        }

        // Counter and Reset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isRo) "$procedureCount proceduri găsite" else "$procedureCount procedures found",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (hasActiveFilters) {
                TextButton(
                    onClick = onResetFilters,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("reset_filters_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isRo) "Resetează filtre" else "Reset filters",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
