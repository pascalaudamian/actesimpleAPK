package com.example.actesimple.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.actesimple.data.model.Language

@Composable
fun LegalVerificationDialog(
    language: Language,
    onClose: () -> Unit
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRo) "Transparență & Verificare" else "Transparency & Verification",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Închide")
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isRo) "Despre ActeSimple" else "About ActeSimple",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isRo)
                            "ActeSimple este un proiect civic independent dedicat simplificării birocrației și procedurilor administrative din România pentru toți cetățenii și rezidenții."
                        else
                            "ActeSimple is an independent civic platform dedicated to simplifying public administrative procedures in Romania for citizens and international residents.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider()

                    Text(
                        text = if (isRo) "Metodologie de Verificare Editorială" else "Editorial Verification Methodology",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isRo)
                            "Fiecare procedură inclusă în portal este verificată periodic pe portalurile oficiale (hub.mai.gov.ro, dgpci.mai.gov.ro, portaligi.mai.gov.ro, anaf.ro, onrc.ro) și în Monitorul Oficial al României pentru a asigura acuratețea taxelor și documentelor solicitate."
                        else
                            "Every procedure is cross-checked against official government databases (HUB MAI, DGPCI, IGI, ANAF, ONRC) and the Official Gazette to ensure fees and lists are up to date.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider()

                    Text(
                        text = if (isRo) "Declinare a Responsabilității (Disclaimer)" else "Legal Disclaimer",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isRo)
                            "ActeSimple este un ghid informativ independent și NU este un serviciu public guvernamental. Depunerea cererilor și eliberarea documentelor oficiale se realizează exclusiv de către autoritățile competente ale statului român."
                        else
                            "ActeSimple is an independent informational guide and NOT an official government portal. Final document issuance and decisions belong exclusively to competent Romanian authorities.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isRo) "Am înțeles" else "Understood")
                }
            }
        }
    }
}
