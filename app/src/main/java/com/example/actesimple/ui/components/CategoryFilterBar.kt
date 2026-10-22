package com.example.actesimple.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.actesimple.data.model.CategoryMeta
import com.example.actesimple.data.model.Language

@Composable
fun CategoryFilterBar(
    language: Language,
    categories: List<CategoryMeta>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit
) {
    val isRo = language == Language.RO

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { cat ->
            val isSelected = cat.id == selectedCategory
            val icon = getCategoryIcon(cat.id)

            FilterChip(
                selected = isSelected,
                onClick = { onSelectCategory(cat.id) },
                label = {
                    Text(
                        text = if (isRo) cat.nameRo else cat.nameEn,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.testTag("category_chip_${cat.id}")
            )
        }
    }
}

private fun getCategoryIcon(catId: String): ImageVector {
    return when (catId) {
        "toate" -> Icons.Default.AllInclusive
        "identitate" -> Icons.Default.Badge
        "auto" -> Icons.Default.DirectionsCar
        "juridic" -> Icons.Default.Gavel
        "afaceri" -> Icons.Default.Work
        "locuinta" -> Icons.Default.Home
        "familie" -> Icons.Default.People
        "international" -> Icons.Default.Public
        else -> Icons.Default.Description
    }
}
