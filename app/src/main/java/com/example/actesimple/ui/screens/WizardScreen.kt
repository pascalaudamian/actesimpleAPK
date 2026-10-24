package com.example.actesimple.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.actesimple.data.model.ApplicantProfile
import com.example.actesimple.data.model.Language
import com.example.actesimple.data.model.Procedure

@Composable
fun WizardScreen(
    language: Language,
    step: Int,
    selectedProfile: ApplicantProfile,
    selectedPurpose: String,
    selectedDuration: String,
    recommendations: List<Procedure>,
    onSelectProfile: (ApplicantProfile) -> Unit,
    onSelectPurpose: (String) -> Unit,
    onSelectDuration: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onSelectProcedure: (Procedure) -> Unit,
    onBackToHome: () -> Unit
) {
    val isRo = language == Language.RO

    BackHandler {
        if (step > 1) {
            onBack()
        } else {
            onBackToHome()
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("wizard_screen")
    ) {
        // Screen Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Assistant,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRo) "Asistent de Orientare" else "Orientation Wizard",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = if (isRo)
                            "Ghidare pas-cu-pas fără colectare de date personale sau CNP."
                        else
                            "Step-by-step guidance without collecting CNPs or personal data.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AssistChip(
                    onClick = { /* Step counter */ },
                    label = { Text(if (isRo) "Pasul $step din 3" else "Step $step of 3") }
                )
            }
        }

        // Progress Indicator
        item {
            LinearProgressIndicator(
                progress = { step.toFloat() / 3f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Step 1: Statut cetățenie
        if (step == 1) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (isRo) "1. Care este statutul tău cetățenesc?" else "1. What is your citizenship status?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isRo)
                            "Procedurile administrative și instituțiile competente (DGEP vs. IGI) diferă în funcție de cetățenie."
                        else
                            "Administrative procedures and responsible authorities (DGEP vs. IGI) depend on your citizenship.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val profiles = listOf(
                        Triple(
                            ApplicantProfile.ROMANIAN,
                            if (isRo) "Cetățean Român" else "Romanian Citizen",
                            if (isRo) "Carte de identitate, pașaport, permis auto, stare civilă în România" else "National ID, passport, driving license, civil registry in Romania"
                        ),
                        Triple(
                            ApplicantProfile.EU_EEA_CH,
                            if (isRo) "Cetățean UE / SEE / Elveția" else "EU / EEA / Swiss Citizen",
                            if (isRo) "Înregistrare rezidență IGI, certificat de înregistrare, CNP românesc, muncă/studii" else "IGI residence registration, registration certificate, Romanian CNP, work/study"
                        ),
                        Triple(
                            ApplicantProfile.NON_EU,
                            if (isRo) "Cetățean Non-UE (Stat Terț)" else "Non-EU Citizen (Third-Country)",
                            if (isRo) "Aviz de angajare, viză de lungă ședere (D), permis de ședere temporară IGI" else "Work authorization, long-stay visa (D), IGI temporary residence permit"
                        ),
                        Triple(
                            ApplicantProfile.FAMILY_MEMBER,
                            if (isRo) "Membru de familie (Cetățean Român / UE)" else "Family Member (Romanian / EU Citizen)",
                            if (isRo) "Reîntregirea familiei, permis de ședere pentru membri de familie" else "Family reunification, residence permit for family members"
                        )
                    )

                    profiles.forEach { (p, name, desc) ->
                        WizardSelectCard(
                            title = name,
                            subtitle = desc,
                            isSelected = selectedProfile == p,
                            onClick = { onSelectProfile(p) },
                            testTag = "wizard_profile_${p.name.lowercase()}"
                        )
                    }
                }
            }
        }

        // Step 2: Scopul procedurii
        if (step == 2) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (isRo) "2. Ce procedură sau nevoie administrativă ai?" else "2. What is your administrative need?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isRo)
                            "Selectează domeniul de interes pentru a primi ghidul oficial potrivit."
                        else
                            "Select the relevant area to view verified procedures.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val purposes = if (selectedProfile == ApplicantProfile.ROMANIAN) listOf(
                        Pair("id_civil_status", if (isRo) "Carte de identitate sau pașaport (buletin, reînnoire, pierdere)" else "ID Card or Passport (new, renewal, lost)"),
                        Pair("auto", if (isRo) "Permis de conducere sau înmatriculare auto (DRPCIV)" else "Driving license or vehicle registration"),
                        Pair("business", if (isRo) "Înființare firmă SRL, PFA sau înregistrare fiscală ANAF" else "Company formation (LLC / Sole Trader) or ANAF tax registration"),
                        Pair("housing", if (isRo) "Viza de flotant (stabilire reședință temporară)" else "Temporary residence endorsement (Flotant)"),
                        Pair("family", if (isRo) "Alocație copii, certificat naștere sau stare civilă" else "Child allowance, birth certificate, civil status")
                    ) else listOf(
                        Pair("work", if (isRo) "Muncă sau angajare în România (Aviz de muncă, contract)" else "Employment or working in Romania (Work notice, contract)"),
                        Pair("id_civil_status", if (isRo) "Certificat de înregistrare rezidență sau permis de ședere IGI" else "IGI residence certificate or residence permit"),
                        Pair("business", if (isRo) "Înregistrare fiscală (NIF / CIF) sau afaceri în România" else "Tax identification number (NIF) or business in Romania"),
                        Pair("studies", if (isRo) "Studii universitare sau formare profesională" else "University studies or vocational training")
                    )

                    purposes.forEach { (key, label) ->
                        WizardSelectCard(
                            title = label,
                            subtitle = null,
                            isSelected = selectedPurpose == key,
                            onClick = { onSelectPurpose(key) },
                            testTag = "wizard_purpose_$key"
                        )
                    }
                }
            }
        }

        // Step 3: Recomandări rezultate
        if (step == 3) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isRo) "3. Ghiduri Oficiale Recomandate" else "3. Recommended Official Guides",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isRo)
                            "Pe baza răspunsurilor tale, am identificat următoarele proceduri oficiale verificate:"
                        else
                            "Based on your responses, here are the verified procedures matching your profile:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (recommendations.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isRo) "Nicio procedură exactă găsită" else "No exact procedures found",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isRo)
                                    "Poți explora catalogul complet pentru toate opțiunile disponibile."
                                else
                                    "You can explore the full catalog for all available options.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            } else {
                items(recommendations) { proc ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectProcedure(proc) }
                            .testTag("wizard_rec_${proc.id}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isRo) proc.title else proc.titleEn ?: proc.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isRo) proc.competentAuthority else proc.competentAuthorityEn ?: proc.competentAuthority,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                proc.estimatedTimeMinutes?.let { mins ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ) {
                                        Text(
                                            text = "~$mins min",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            // Rationale
                            val rationale = if (isRo) proc.recommendationRationale else proc.recommendationRationaleEn ?: proc.recommendationRationale
                            if (!rationale.isNullOrBlank()) {
                                Text(
                                    text = "💡 $rationale",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${proc.totalFee} • ${proc.estimatedTime}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Button(
                                    onClick = { onSelectProcedure(proc) },
                                    modifier = Modifier.height(36.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp)
                                ) {
                                    Text(if (isRo) "Deschide Ghid" else "Open Guide", style = MaterialTheme.typography.labelSmall)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Navigation Action Buttons
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 1) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("wizard_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isRo) "Înapoi" else "Back")
                    }
                } else {
                    OutlinedButton(
                        onClick = onBackToHome,
                        modifier = Modifier.testTag("wizard_home_button")
                    ) {
                        Text(if (isRo) "Acasă" else "Home")
                    }
                }

                if (step < 3) {
                    Button(
                        onClick = onNext,
                        modifier = Modifier.testTag("wizard_next_button")
                    ) {
                        Text(if (isRo) "Continuă" else "Continue")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                } else {
                    Button(
                        onClick = onBackToHome,
                        modifier = Modifier.testTag("wizard_done_button")
                    ) {
                        Text(if (isRo) "Finalizează" else "Done")
                    }
                }
            }
        }
    }
}

@Composable
private fun WizardSelectCard(
    title: String,
    subtitle: String?,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else
            null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
                if (!subtitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
