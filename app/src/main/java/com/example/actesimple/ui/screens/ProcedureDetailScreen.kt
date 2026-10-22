package com.example.actesimple.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.actesimple.data.model.*
import com.example.actesimple.ui.components.PublicOfficesMapView
import com.example.actesimple.ui.theme.StatusOnlineBlue
import com.example.actesimple.ui.theme.StatusOnlineBlueContainer
import com.example.actesimple.ui.theme.StatusVerifiedGreen
import com.example.actesimple.ui.theme.StatusVerifiedGreenContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcedureDetailScreen(
    procedure: Procedure,
    language: Language,
    activeTab: Int,
    checklistState: UserChecklistState,
    onBack: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onToggleDoc: (String) -> Unit,
    onToggleStep: (Int) -> Unit,
    onAddCustomDoc: (String) -> Unit,
    onToggleCustomDoc: (String) -> Unit,
    onCheckAll: () -> Unit,
    onResetChecklist: () -> Unit,
    onUpdateNotes: (String, String, String) -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val isRo = language == Language.RO

    val title = if (isRo) procedure.title else procedure.titleEn ?: procedure.title
    val fullDesc = if (isRo) procedure.fullDescription else procedure.fullDescriptionEn ?: procedure.fullDescription
    val fee = if (isRo) procedure.totalFee else procedure.totalFeeEn ?: procedure.totalFee
    val time = if (isRo) procedure.estimatedTime else procedure.estimatedTimeEn ?: procedure.estimatedTime
    val authority = if (isRo) procedure.competentAuthority else procedure.competentAuthorityEn ?: procedure.competentAuthority
    val proTips = if (isRo) procedure.proTips else procedure.proTipsEn.ifEmpty { procedure.proTips }

    var customDocText by remember { mutableStateOf("") }
    var notesText by remember(checklistState.personalNotes) { mutableStateOf(checklistState.personalNotes) }
    var apptDateText by remember(checklistState.appointmentDate) { mutableStateOf(checklistState.appointmentDate) }
    var apptLocText by remember(checklistState.appointmentLocation) { mutableStateOf(checklistState.appointmentLocation) }

    val totalDocs = procedure.documents.size + checklistState.customDocuments.size
    val checkedDocs = checklistState.checkedDocumentIds.size + checklistState.customDocuments.count { it.isChecked }
    val progressFraction = if (totalDocs > 0) checkedDocs.toFloat() / totalDocs.toFloat() else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Înapoi"
                        )
                    }
                },
                actions = {
                    // Share summary
                    IconButton(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "ActeSimple: $title\nTaxă: $fee\nTimp: $time\nAutoritate: $authority\nInformații complete pe ActeSimple."
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Trimite sumar procedură"))
                        },
                        modifier = Modifier.testTag("share_procedure_button")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Distribuie")
                    }
                }
            )
        },
        bottomBar = {
            // Bottom sticky button for official website
            val officialUrl = procedure.officialUrl ?: procedure.sources.firstOrNull()?.url
            if (!officialUrl.isNullOrBlank()) {
                Surface(
                    tonalElevation = 4.dp,
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp)
                    ) {
                        Button(
                            onClick = {
                                try {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(officialUrl))
                                    context.startActivity(browserIntent)
                                } catch (e: Exception) {
                                    // Fallback
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("open_official_portal_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isRo) "Accesează Platforma Oficială" else "Open Official Government Portal",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Header card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = procedure.category.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(StatusVerifiedGreenContainer)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = StatusVerifiedGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isRo) "Verificat legal" else "Verified legal",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StatusVerifiedGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = fullDesc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // 4 Key metrics grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            KeyMetricCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Payments,
                                label = if (isRo) "Cost / Taxă" else "Total Fee",
                                value = fee
                            )
                            KeyMetricCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Schedule,
                                label = if (isRo) "Timp estimat" else "Duration",
                                value = time
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            KeyMetricCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.AccountBalance,
                                label = if (isRo) "Autoritate" else "Authority",
                                value = authority
                            )
                            KeyMetricCard(
                                modifier = Modifier.weight(1f),
                                icon = if (procedure.isOnlineAvailable) Icons.Default.CloudDone else Icons.Default.Storefront,
                                label = if (isRo) "Depunere" else "Submission",
                                value = if (procedure.isOnlineAvailable) (if (isRo) "Online posibil" else "Online available") else (if (isRo) "Prezență la ghișeu" else "Counter attendance")
                            )
                        }

                        // Quick shortcut to physical offices & map
                        OutlinedButton(
                            onClick = { onTabSelected(2) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_view_offices_map")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isRo) "Vezi ghișeele pe hartă (SPCLEP, ANAF, etc.)" else "View offices on map (SPCLEP, ANAF, etc.)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Tabs row
            item {
                val tabs = if (isRo) listOf(
                    "Documente (${procedure.documents.size})",
                    "Pași (${procedure.steps.size})",
                    "Locații & Hartă",
                    "Sfaturi utile",
                    "Surse & Legal"
                ) else listOf(
                    "Documents (${procedure.documents.size})",
                    "Steps (${procedure.steps.size})",
                    "Offices & Map",
                    "Pro Tips",
                    "Sources & Legal"
                )

                PrimaryScrollableTabRow(
                    selectedTabIndex = activeTab,
                    edgePadding = 8.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    tabs.forEachIndexed { index, tabTitle ->
                        Tab(
                            selected = activeTab == index,
                            onClick = { onTabSelected(index) },
                            text = { Text(tabTitle, style = MaterialTheme.typography.labelMedium) }
                        )
                    }
                }
            }

            // Tab 0: Documente & Checklist
            if (activeTab == 0) {
                // Checklist progress card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
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
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isRo) "Checklist interactiv dosar" else "Interactive dossier checklist",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${(progressFraction * 100).toInt()}%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            LinearProgressIndicator(
                                progress = { progressFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isRo) "$checkedDocs din $totalDocs documente pregătite" else "$checkedDocs of $totalDocs documents ready",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(
                                        onClick = onCheckAll,
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.testTag("check_all_docs_button")
                                    ) {
                                        Text(
                                            text = if (isRo) "Bifează tot" else "Check all",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }

                                    if (checkedDocs > 0) {
                                        TextButton(
                                            onClick = onResetChecklist,
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                            modifier = Modifier.testTag("reset_checklist_button")
                                        ) {
                                            Text(
                                                text = if (isRo) "Resetează" else "Reset",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Official documents list
                items(procedure.documents) { doc ->
                    val isChecked = checklistState.checkedDocumentIds.contains(doc.id)
                    val docName = if (isRo) doc.name else doc.nameEn ?: doc.name
                    val docDesc = if (isRo) doc.description else doc.descriptionEn ?: doc.description
                    val whereToGet = if (isRo) doc.whereToGet else doc.whereToGetEn ?: doc.whereToGet

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isChecked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleDoc(doc.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { onToggleDoc(doc.id) },
                                modifier = Modifier.testTag("doc_checkbox_${doc.id}")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (doc.isMandatory) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.errorContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = if (isRo) "Obligatoriu" else "Mandatory",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    } else {
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = if (isRo) "Opțional" else "Optional",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = doc.type.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = docName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                                    color = if (isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = docDesc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (!whereToGet.isNullOrBlank()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Place,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${if (isRo) "De unde se obține:" else "Source:"} $whereToGet",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                if (!doc.downloadUrl.isNullOrBlank()) {
                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(doc.downloadUrl))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {}
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FileDownload,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = doc.downloadLabel ?: (if (isRo) "Descarcă formular (PDF)" else "Download form"),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Custom user added documents
                items(checklistState.customDocuments) { customDoc ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleCustomDoc(customDoc.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = customDoc.isChecked,
                                onCheckedChange = { onToggleCustomDoc(customDoc.id) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = customDoc.name,
                                style = MaterialTheme.typography.bodyMedium,
                                textDecoration = if (customDoc.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (isRo) "Personal" else "Custom",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Add custom document input row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customDocText,
                            onValueChange = { customDocText = it },
                            placeholder = {
                                Text(
                                    text = if (isRo) "Adaugă alt act necesar (ex: procură)..." else "Add custom doc (e.g. power of attorney)...",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("custom_doc_input")
                        )
                        Button(
                            onClick = {
                                if (customDocText.isNotBlank()) {
                                    onAddCustomDoc(customDocText)
                                    customDocText = ""
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("add_custom_doc_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Adaugă")
                        }
                    }
                }

                // Personal Appointment & Notes Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = if (isRo) "Programare & Notițe Personale" else "Appointment & Personal Notes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = apptDateText,
                                onValueChange = {
                                    apptDateText = it
                                    onUpdateNotes(notesText, apptDateText, apptLocText)
                                },
                                label = { Text(if (isRo) "Data și ora programării" else "Appointment Date & Time") },
                                placeholder = { Text("Ex: 28 Octombrie 2026, ora 10:30") },
                                leadingIcon = { Icon(Icons.Default.Event, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = apptLocText,
                                onValueChange = {
                                    apptLocText = it
                                    onUpdateNotes(notesText, apptDateText, apptLocText)
                                },
                                label = { Text(if (isRo) "Locația ghișeului / adresa" else "Office location / address") },
                                placeholder = { Text("Ex: SPCLEP Sector 3, str. Matei Basarab") },
                                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = notesText,
                                onValueChange = {
                                    notesText = it
                                    onUpdateNotes(notesText, apptDateText, apptLocText)
                                },
                                label = { Text(if (isRo) "Notițe personale pentru dosar" else "Personal dossier notes") },
                                placeholder = { Text("Ex: Dosar cu șină, să iau pix albastru, bani potriviți") },
                                shape = RoundedCornerShape(12.dp),
                                minLines = 2,
                                maxLines = 4,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Tab 1: Pași de urmat
            if (activeTab == 1) {
                items(procedure.steps) { step ->
                    val isChecked = checklistState.checkedStepNumbers.contains(step.stepNumber)
                    val stepTitle = if (isRo) step.title else step.titleEn ?: step.title
                    val stepDesc = if (isRo) step.description else step.descriptionEn ?: step.description
                    val stepTip = if (isRo) step.tip else step.tipEn ?: step.tip

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isChecked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleStep(step.stepNumber) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isChecked) StatusVerifiedGreen else MaterialTheme.colorScheme.primary
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isChecked) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Text(
                                        text = "${step.stepNumber}",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = stepTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                                )

                                Text(
                                    text = stepDesc,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (!step.locationOrTool.isNullOrBlank()) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PinDrop,
                                                contentDescription = null,
                                                modifier = Modifier.size(13.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = step.locationOrTool,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                }

                                if (!stepTip.isNullOrBlank()) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.tertiaryContainer,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lightbulb,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = stepTip,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                        }
                                    }
                                }

                                if (!step.externalUrl.isNullOrBlank()) {
                                    TextButton(
                                        onClick = {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(step.externalUrl))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {}
                                        },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Link,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = step.externalLabel ?: (if (isRo) "Deschide linkul" else "Open link"),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tab 2: Locații & Hartă Ghișee
            if (activeTab == 2) {
                item {
                    PublicOfficesMapView(
                        procedure = procedure,
                        language = language
                    )
                }
            }

            // Tab 3: Sfaturi utile & Pro Tips
            if (activeTab == 3) {
                if (proTips.isNotEmpty()) {
                    items(proTips) { tip ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TipsAndUpdates,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = tip,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Text(
                            text = if (isRo) "Nu există sfaturi suplimentare pentru această procedură." else "No specific tips for this procedure.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Tab 4: Surse & Legal
            if (activeTab == 4) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = if (isRo) "Validare Editorială & Transparență" else "Editorial Validation & Transparency",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "${if (isRo) "Ultima verificare:" else "Last verified:"} ${procedure.lastVerifiedAt}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "${if (isRo) "Verificat de:" else "Verified by:"} ${procedure.verifiedBy}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (!procedure.verificationNotes.isNullOrBlank()) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = procedure.verificationNotes,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                items(procedure.sources) { source ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = source.institutionName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (!source.notes.isNullOrBlank()) {
                                Text(
                                    text = source.notes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            TextButton(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {}
                                },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(source.url, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyMetricCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
