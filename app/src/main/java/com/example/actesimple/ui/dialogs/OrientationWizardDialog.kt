package com.example.actesimple.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.actesimple.data.model.ApplicantProfile
import com.example.actesimple.data.model.Language
import com.example.actesimple.data.model.Procedure

@Composable
fun OrientationWizardDialog(
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
    onClose: () -> Unit,
    onSelectProcedure: (Procedure) -> Unit
) {
    val isRo = language == Language.RO

    Dialog(onDismissRequest = onClose) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isRo) "Asistent de Orientare" else "Orientation Wizard",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = if (isRo) "Pasul $step din 3" else "Step $step of 3",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Închide")
                    }
                }

                LinearProgressIndicator(
                    progress = { step.toFloat() / 3f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary
                )

                // Step 1: Cine ești?
                if (step == 1) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = if (isRo) "Care este statutul tău cetățenesc?" else "What is your citizenship status?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isRo)
                                "Procedurile administrative diferă în funcție de cetățenie."
                            else
                                "Administrative procedures depend on your citizenship.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        val profiles = listOf(
                            Triple(ApplicantProfile.ROMANIAN, if (isRo) "Cetățean Român" else "Romanian Citizen", if (isRo) "Buletin, permis, acte civile în România" else "ID card, driver license, local civil status"),
                            Triple(ApplicantProfile.EU_EEA_CH, if (isRo) "Cetățean UE / SEE / Elveția" else "EU / EEA / Swiss Citizen", if (isRo) "Rezidență IGI, CNP românesc, muncă/studii" else "IGI residence certificate, CNP, work/study"),
                            Triple(ApplicantProfile.NON_EU, if (isRo) "Cetățean Non-UE" else "Non-EU Citizen", if (isRo) "Vize de lungă ședere, permis de ședere IGI" else "Long-stay visas, IGI residence permit, work"),
                            Triple(ApplicantProfile.FAMILY_MEMBER, if (isRo) "Membru de familie" else "Family Member", if (isRo) "Reîntregire familie, căsătorie, copii" else "Family reunification, marriage, children")
                        )

                        profiles.forEach { (p, name, desc) ->
                            WizardOptionCard(
                                title = name,
                                subtitle = desc,
                                isSelected = selectedProfile == p,
                                onClick = { onSelectProfile(p) }
                            )
                        }
                    }
                }

                // Step 2: Ce dorești?
                if (step == 2) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = if (isRo) "Ce procedură sau serviciu cauți?" else "What procedure or service are you seeking?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        val purposes = if (selectedProfile == ApplicantProfile.ROMANIAN) listOf(
                            Pair("id_civil_status", if (isRo) "Carte de identitate sau pașaport" else "ID Card or Passport"),
                            Pair("auto", if (isRo) "Permis auto sau înmatriculare mașină" else "Driving license or car registration"),
                            Pair("business", if (isRo) "Înființare firmă SRL sau PFA" else "Company formation (LLC / Freelance)"),
                            Pair("housing", if (isRo) "Viza de flotant (reședință)" else "Temporary residence (Flotant)"),
                            Pair("family", if (isRo) "Alocație copii sau stare civilă" else "Child allowance or civil registry")
                        ) else listOf(
                            Pair("work", if (isRo) "Muncă sau angajare în România" else "Employment or working in Romania"),
                            Pair("studies", if (isRo) "Studii universitare sau liceu" else "University or school studies"),
                            Pair("business", if (isRo) "Afaceri, investiții sau NIF" else "Business, investments or tax ID"),
                            Pair("id_civil_status", if (isRo) "Rezidență sau permis de ședere" else "Residence certificate or permit")
                        )

                        purposes.forEach { (key, label) ->
                            WizardOptionCard(
                                title = label,
                                subtitle = null,
                                isSelected = selectedPurpose == key,
                                onClick = { onSelectPurpose(key) }
                            )
                        }
                    }
                }

                // Step 3: Recomandări
                if (step == 3) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isRo) "Ghiduri recomandate pentru tine:" else "Recommended guides for you:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 280.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(recommendations) { proc ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSelectProcedure(proc) }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (isRo) proc.title else proc.titleEn ?: proc.title,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${proc.totalFee} • ${proc.estimatedTime}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom navigation buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        OutlinedButton(onClick = onBack) {
                            Text(if (isRo) "Înapoi" else "Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (step < 3) {
                        Button(onClick = onNext) {
                            Text(if (isRo) "Continuă" else "Continue")
                        }
                    } else {
                        Button(onClick = onClose) {
                            Text(if (isRo) "Închide" else "Done")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WizardOptionCard(
    title: String,
    subtitle: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
