package com.example.actesimple.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.actesimple.data.model.Language
import com.example.actesimple.ui.components.*
import com.example.actesimple.ui.dialogs.*
import com.example.actesimple.ui.viewmodel.ActeSimpleViewModel

@Composable
fun MainAppScreen(
    viewModel: ActeSimpleViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRo = uiState.language == Language.RO
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissToast()
        }
    }

    if (uiState.selectedProcedure != null) {
        ProcedureDetailScreen(
            procedure = uiState.selectedProcedure!!,
            language = uiState.language,
            activeTab = uiState.activeDetailTab,
            checklistState = uiState.currentChecklist,
            onBack = { viewModel.closeDetail() },
            onTabSelected = { viewModel.setActiveDetailTab(it) },
            onToggleDoc = { viewModel.toggleDocumentCheck(it) },
            onToggleStep = { viewModel.toggleStepCheck(it) },
            onAddCustomDoc = { viewModel.addCustomDoc(it) },
            onToggleCustomDoc = { viewModel.toggleCustomDoc(it) },
            onCheckAll = { viewModel.checkAllDocuments() },
            onResetChecklist = { viewModel.resetChecklist() },
            onUpdateNotes = { n, d, l -> viewModel.updateChecklistNotes(n, d, l) }
        )
    } else {
        Scaffold(
            topBar = {
                AppTopBar(
                    language = uiState.language,
                    savedCount = uiState.savedSummaries.size,
                    isDetailOpen = false,
                    onBack = {},
                    onToggleLanguage = {
                        val next = if (uiState.language == Language.RO) Language.EN else Language.RO
                        viewModel.setLanguage(next)
                    },
                    onOpenSavedDossiers = { viewModel.openSavedDossiers() },
                    onOpenOfficialPortals = { viewModel.openOfficialPortals() },
                    onOpenVerificationInfo = { viewModel.openLegalVerification() }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding() + 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("home_screen_content")
            ) {
                // Hero & Search Header
                item {
                    HeroSearchSection(
                        language = uiState.language,
                        searchQuery = uiState.searchQuery,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onQuickSearch = { viewModel.setSearchQuery(it) },
                        onStartWizard = { viewModel.openWizard() }
                    )
                }

                // Category Chips
                item {
                    CategoryFilterBar(
                        language = uiState.language,
                        categories = uiState.categories,
                        selectedCategory = uiState.selectedCategory,
                        onSelectCategory = { viewModel.selectCategory(it) }
                    )
                }

                // Profile Filters & Online Filter
                item {
                    val hasFilters = uiState.searchQuery.isNotBlank() ||
                                     uiState.selectedCategory != "toate" ||
                                     uiState.selectedProfile != com.example.actesimple.data.model.ApplicantProfile.ALL ||
                                     uiState.onlineOnly

                    ProfileFilterRow(
                        language = uiState.language,
                        selectedProfile = uiState.selectedProfile,
                        onlineOnly = uiState.onlineOnly,
                        procedureCount = uiState.filteredProcedures.size,
                        onSelectProfile = { viewModel.selectProfile(it) },
                        onToggleOnlineOnly = { viewModel.toggleOnlineOnly() },
                        onResetFilters = { viewModel.resetFilters() },
                        hasActiveFilters = hasFilters
                    )
                }

                // Procedure Cards List
                if (uiState.filteredProcedures.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isRo) "Nicio procedură găsită" else "No procedures found",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isRo)
                                    "Încearcă să modifici termenii de căutare sau să resetezi filtrele."
                                else
                                    "Try modifying your search terms or resetting active filters.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.resetFilters() }) {
                                Text(if (isRo) "Resetează toate filtrele" else "Reset all filters")
                            }
                        }
                    }
                } else {
                    items(
                        items = uiState.filteredProcedures,
                        key = { it.id }
                    ) { procedure ->
                        val summary = uiState.savedSummaries.find { it.procedureId == procedure.id }
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                            ProcedureCardItem(
                                procedure = procedure,
                                language = uiState.language,
                                summary = summary,
                                onClick = { viewModel.selectProcedure(procedure) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Orientation Wizard Dialog
    if (uiState.isWizardOpen) {
        OrientationWizardDialog(
            language = uiState.language,
            step = uiState.wizardStep,
            selectedProfile = uiState.wizardProfile,
            selectedPurpose = uiState.wizardPurpose,
            selectedDuration = uiState.wizardDuration,
            recommendations = uiState.wizardRecommendations,
            onSelectProfile = { viewModel.setWizardProfile(it) },
            onSelectPurpose = { viewModel.setWizardPurpose(it) },
            onSelectDuration = { viewModel.setWizardDuration(it) },
            onNext = { viewModel.advanceWizardStep() },
            onBack = { viewModel.backWizardStep() },
            onClose = { viewModel.closeWizard() },
            onSelectProcedure = {
                viewModel.closeWizard()
                viewModel.selectProcedure(it)
            }
        )
    }

    // Saved Dossiers Dialog
    if (uiState.isSavedDossiersOpen) {
        SavedDossiersDialog(
            language = uiState.language,
            summaries = uiState.savedSummaries,
            procedures = uiState.procedures,
            onSelectProcedure = { viewModel.selectProcedure(it) },
            onClose = { viewModel.closeSavedDossiers() }
        )
    }

    // Official Portals Dialog
    if (uiState.isOfficialPortalsOpen) {
        OfficialPortalsDialog(
            language = uiState.language,
            portals = uiState.officialPortals,
            onClose = { viewModel.closeOfficialPortals() }
        )
    }

    // Legal & Editorial Verification Info Dialog
    if (uiState.isLegalVerificationOpen) {
        LegalVerificationDialog(
            language = uiState.language,
            onClose = { viewModel.closeLegalVerification() }
        )
    }
}
