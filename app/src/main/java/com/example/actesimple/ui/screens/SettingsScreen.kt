package com.example.actesimple.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.actesimple.data.model.Language

@Composable
fun SettingsScreen(
    language: Language,
    onToggleLanguage: () -> Unit,
    onOpenOfficialPortals: () -> Unit,
    onOpenLegalVerification: () -> Unit,
    onOpenFeedback: () -> Unit,
    onClearAllData: () -> Unit
) {
    val isRo = language == Language.RO
    var showClearDialog by remember { mutableStateOf(false) }
    var showRoeIdInfoDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (isRo) "Setări & Transparență Civică" else "Settings & Civic Transparency",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // Section: Language
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isRo) "Limba Aplicației (Language)" else "App Language",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isRo)
                        "Selectează limba dorită pentru ghiduri, formulare și asistentul de orientare."
                    else
                        "Select your preferred language for guides, forms, and the orientation wizard.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { if (language != Language.RO) onToggleLanguage() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (language == Language.RO) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (language == Language.RO) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("select_lang_ro")
                    ) {
                        Text("Română (RO)", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { if (language != Language.EN) onToggleLanguage() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (language == Language.EN) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (language == Language.EN) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("select_lang_en")
                    ) {
                        Text("English (EN)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section: Civic Transparency Notice
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (isRo) "Statut Legal & Transparență" else "Legal Status & Civic Disclaimer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = if (isRo)
                        "ActeSimple este un serviciu civic independent de informare și asistență administrativă. Aplicația NU este o platformă guvernamentală și nu are afiliere cu Guvernul României sau alte ministere. Toate informațiile sunt verificate periodic conform actelor normative publicate în Monitorul Oficial."
                    else
                        "ActeSimple is an independent civic information and administrative assistance application. It is NOT an official government application and is not affiliated with the Romanian Government. All legal guidance is verified against official statutory publications.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodySmall.lineHeight
                )
            }
        }

        // Section: Official Services & Verification
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isRo) "Surse Oficiale & Integrări" else "Official Sources & Integrations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                SettingsClickableItem(
                    icon = Icons.Default.AccountBalance,
                    title = if (isRo) "Portaluri Guvernamentale Oficiale" else "Official Government Portals",
                    subtitle = if (isRo) "Directoare HUB MAI, Ghișeul.ro, IGI, ANAF, ONRC" else "Directory for HUB MAI, Ghiseul.ro, IGI, ANAF, ONRC",
                    onClick = onOpenOfficialPortals
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                SettingsClickableItem(
                    icon = Icons.Default.VerifiedUser,
                    title = if (isRo) "Audit & Verificare Editorială" else "Editorial & Legal Verification Audit",
                    subtitle = if (isRo) "Data ultimei verificări și metodologia juridică 2026" else "Last verified dates and 2026 legal methodology",
                    onClick = onOpenLegalVerification
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                SettingsClickableItem(
                    icon = Icons.Default.Security,
                    title = if (isRo) "Raport Integrare ROeID (ADR)" else "ROeID Government Integration Report",
                    subtitle = if (isRo) "Cerințe tehnice, protocoale SAML/OIDC și statut privat" else "Technical requirements, OIDC protocols & provider status",
                    onClick = { showRoeIdInfoDialog = true }
                )
            }
        }

        // Section: Support & Reporting
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isRo) "Comunitate & Feedback" else "Community & Feedback",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                SettingsClickableItem(
                    icon = Icons.Default.ReportProblem,
                    title = if (isRo) "Raportează o informație eronată" else "Report outdated or changed info",
                    subtitle = if (isRo) "Semnalează taxe modificate sau cerințe noi de la ghișeu" else "Alert about updated fees or new counter requirements",
                    onClick = onOpenFeedback
                )
            }
        }

        // Section: Privacy & Local Data
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isRo) "Confidențialitate & Date Locale" else "Privacy & Local Data",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isRo)
                        "ActeSimple nu colectează CNP-uri, parole sau scanări de buletine. Toate bifările din checklist, mementourile și notițele tale sunt stocate exclusiv pe telefonul tău."
                    else
                        "ActeSimple never collects national ID numbers, passwords or document scans. Your checklist progress, reminders and notes are stored strictly on your device.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedButton(
                    onClick = { showClearDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clear_all_data_button")
                ) {
                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isRo) "Șterge toate datele locale din telefon" else "Clear all local data from device")
                }
            }
        }

        // App Version
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ActeSimple v1.0.0 (Build 2026)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = if (isRo) "Creat cu grijă pentru simplificarea vieții civice" else "Crafted with care to simplify civic life in Romania",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }

    // Clear Confirmation Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text(if (isRo) "Ștergi toate datele salvate?" else "Clear all saved data?") },
            text = {
                Text(
                    if (isRo)
                        "Această acțiune va reseta toate checklist-urile completate, ghidurile salvate la favorite și mementourile programate. Datele nu vor mai putea fi recuperate."
                    else
                        "This will reset all your document checklists, bookmarked procedures, and scheduled reminders. This action cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_clear_data_btn")
                ) {
                    Text(if (isRo) "Da, șterge tot" else "Yes, clear all")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(if (isRo) "Anulează" else "Cancel")
                }
            }
        )
    }

    // ROeID Integration Report Dialog
    if (showRoeIdInfoDialog) {
        AlertDialog(
            onDismissRequest = { showRoeIdInfoDialog = false },
            icon = { Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text(if (isRo) "Studiu Fezabilitate ROeID (ADR)" else "ROeID Integration Feasibility") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isRo)
                            "1. Cadrul Legal: ROeID este identitatea digitală a României gestionată de Autoritatea pentru Digitalizarea României (ADR), conform OUG 89/2022.\n\n" +
                            "2. Condiții Onboarding: Furnizorii privați de servicii pot solicita integrarea prin protocol tehnic OIDC / SAML 2.0 cu ADR. Integrarea este supusă aprobării instituționale bilaterale și auditului de securitate cibernetică.\n\n" +
                            "3. Statut Curent ActeSimple: În această versiune, ActeSimple oferă acces securizat direct (Deep Link) către portalurile oficiale autentificate prin ROeID (HUB MAI, Ghișeul.ro), fără a colecta sau stoca date de autentificare.\n\n" +
                            "4. Fără Credențiale: ActeSimple nu solicită și nu va solicita niciodată parolele sau credențialele tale guvernamentale."
                        else
                            "1. Legal Framework: ROeID is Romania's official national digital identity platform managed by ADR (Authority for Digitalization of Romania) under GEO 89/2022.\n\n" +
                            "2. Onboarding Requirements: Private service providers can integrate via OIDC / SAML 2.0 protocols subject to bilateral authorization and cybersecurity compliance.\n\n" +
                            "3. Current ActeSimple Status: ActeSimple currently routes users via secure deep links to official ROeID portals (HUB MAI, Ghiseul.ro) without capturing authentication data.\n\n" +
                            "4. Zero-Trust Credentials: ActeSimple never requests or stores government passwords or credentials.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoeIdInfoDialog = false }) {
                    Text(if (isRo) "Am înțeles" else "Understood")
                }
            }
        )
    }
}

@Composable
private fun SettingsClickableItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(18.dp)
        )
    }
}
