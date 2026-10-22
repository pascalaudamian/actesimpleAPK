package com.example.actesimple.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.actesimple.data.model.Language

@Composable
fun HeroSearchSection(
    language: Language,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onQuickSearch: (String) -> Unit,
    onStartWizard: () -> Unit
) {
    val isRo = language == Language.RO

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Verification badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isRo) "Ghid Oficial Verificat • Actualizat 2026" else "Verified Civic Guide • Updated 2026",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Title & subtitle
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = if (isRo) "Ghidul tău prin birocrația din România" else "Navigate Romanian Public Administration",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isRo)
                    "Acte necesare, taxe oficiale, pași clari și checklist interactiv pentru cetățeni români și internaționali."
                else
                    "Required documents, verified fees, step-by-step instructions and interactive checklists for citizens & expats.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Search Input Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = {
                Text(
                    text = if (isRo) "Caută procedură (ex: buletin, permis, cazier, rezidență, SRL)..."
                    else "Search procedure (e.g., ID card, driving, residence, visa)...",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Căutare",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchChange("") },
                        modifier = Modifier.testTag("clear_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Șterge căutarea"
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_input")
        )

        // Quick suggestions pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val suggestions = if (isRo) listOf(
                "Buletin", "Permis auto", "Cazier judiciar", "Rezidență UE", "Firmă SRL", "Viza flotant", "Alocație copii"
            ) else listOf(
                "ID Card", "Driving License", "Criminal Record", "EU Residence", "Company Setup", "Flotant Visa", "Child Allowance"
            )

            suggestions.forEach { suggestion ->
                SuggestionChip(
                    onClick = { onQuickSearch(suggestion) },
                    label = { Text(suggestion, style = MaterialTheme.typography.labelSmall) },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // Orientation Wizard Banner Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isRo) "Asistent de Orientare" else "Orientation Wizard",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isRo)
                            "Nu știi exact de ce acte ai nevoie? Răspunde la 2 întrebări simple."
                        else
                            "Not sure where to begin? Answer 2 simple questions to find your guide.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onStartWizard,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("start_wizard_button")
                ) {
                    Text(
                        text = if (isRo) "Începe" else "Start",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
