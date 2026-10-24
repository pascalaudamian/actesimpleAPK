package com.example.actesimple.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.actesimple.data.model.AppNavigationTab
import com.example.actesimple.data.model.Language
import com.example.actesimple.data.model.Procedure
import com.example.actesimple.ui.components.AppTopBar
import com.example.actesimple.ui.dialogs.*
import com.example.actesimple.ui.viewmodel.ActeSimpleViewModel

@Composable
fun MainAppScreen(
    viewModel: ActeSimpleViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRo = uiState.language == Language.RO
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddReminderDialog by remember { mutableStateOf(false) }
    var procedureForReminder by remember { mutableStateOf<Procedure?>(null) }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissToast()
        }
    }

    if (uiState.selectedProcedure != null) {
        val selected = uiState.selectedProcedure!!
        ProcedureDetailScreen(
            procedure = selected,
            language = uiState.language,
            activeTab = uiState.activeDetailTab,
            checklistState = uiState.currentChecklist,
            isBookmarked = uiState.bookmarkedProcedureIds.contains(selected.id),
            onBack = { viewModel.closeDetail() },
            onTabSelected = { viewModel.setActiveDetailTab(it) },
            onToggleDoc = { viewModel.toggleDocumentCheck(it) },
            onToggleStep = { viewModel.toggleStepCheck(it) },
            onAddCustomDoc = { viewModel.addCustomDoc(it) },
            onToggleCustomDoc = { viewModel.toggleCustomDoc(it) },
            onCheckAll = { viewModel.checkAllDocuments() },
            onResetChecklist = { viewModel.resetChecklist() },
            onUpdateNotes = { n, d, l -> viewModel.updateChecklistNotes(n, d, l) },
            onToggleBookmark = { viewModel.toggleBookmark(selected.id) },
            onAddReminder = {
                procedureForReminder = selected
                showAddReminderDialog = true
            }
        )
    } else {
        if (uiState.currentTab != AppNavigationTab.HOME) {
            BackHandler {
                viewModel.setNavigationTab(AppNavigationTab.HOME)
            }
        }

        Scaffold(
            topBar = {
                AppTopBar(
                    language = uiState.language,
                    savedCount = uiState.bookmarkedProcedureIds.size,
                    remindersCount = uiState.reminders.count { it.isEnabled },
                    isDetailOpen = false,
                    onBack = {},
                    onToggleLanguage = {
                        val next = if (uiState.language == Language.RO) Language.EN else Language.RO
                        viewModel.setLanguage(next)
                    },
                    onOpenSavedDossiers = { viewModel.setNavigationTab(AppNavigationTab.SAVED) },
                    onOpenReminders = { viewModel.setNavigationTab(AppNavigationTab.REMINDERS) },
                    onOpenSettings = { viewModel.setNavigationTab(AppNavigationTab.SETTINGS) },
                    onOpenOfficialPortals = { viewModel.openOfficialPortals() },
                    onOpenVerificationInfo = { viewModel.openLegalVerification() }
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.testTag("main_bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = uiState.currentTab == AppNavigationTab.HOME,
                        onClick = { viewModel.setNavigationTab(AppNavigationTab.HOME) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Acasă") },
                        label = { Text(if (isRo) "Acasă" else "Home") },
                        modifier = Modifier.testTag("nav_item_home")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == AppNavigationTab.EXPLORE,
                        onClick = { viewModel.setNavigationTab(AppNavigationTab.EXPLORE) },
                        icon = { Icon(Icons.Default.Search, contentDescription = "Explorează") },
                        label = { Text(if (isRo) "Explorează" else "Explore") },
                        modifier = Modifier.testTag("nav_item_explore")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == AppNavigationTab.WIZARD,
                        onClick = { viewModel.setNavigationTab(AppNavigationTab.WIZARD) },
                        icon = { Icon(Icons.Default.Assistant, contentDescription = "Asistent") },
                        label = { Text(if (isRo) "Asistent" else "Wizard") },
                        modifier = Modifier.testTag("nav_item_wizard")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == AppNavigationTab.CHECKLIST,
                        onClick = { viewModel.setNavigationTab(AppNavigationTab.CHECKLIST) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (uiState.savedSummaries.isNotEmpty()) {
                                        Badge { Text("${uiState.savedSummaries.size}") }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Checklist, contentDescription = "Dosare")
                            }
                        },
                        label = { Text(if (isRo) "Dosare" else "Checklists") },
                        modifier = Modifier.testTag("nav_item_checklist")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == AppNavigationTab.REMINDERS,
                        onClick = { viewModel.setNavigationTab(AppNavigationTab.REMINDERS) },
                        icon = {
                            val activeCount = uiState.reminders.count { it.isEnabled }
                            BadgedBox(
                                badge = {
                                    if (activeCount > 0) {
                                        Badge { Text("$activeCount") }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Alarm, contentDescription = "Mementouri")
                            }
                        },
                        label = { Text(if (isRo) "Mementouri" else "Reminders") },
                        modifier = Modifier.testTag("nav_item_reminders")
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (uiState.currentTab) {
                    AppNavigationTab.HOME -> {
                        HomeScreen(
                            language = uiState.language,
                            searchQuery = uiState.searchQuery,
                            procedures = uiState.procedures,
                            categories = uiState.categories,
                            summaries = uiState.savedSummaries,
                            bookmarkedIds = uiState.bookmarkedProcedureIds,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onQuickSearch = { viewModel.setSearchQuery(it) },
                            onSelectCategory = { viewModel.selectCategory(it) },
                            onSelectProcedure = { viewModel.selectProcedure(it) },
                            onToggleBookmark = { viewModel.toggleBookmark(it) },
                            onStartWizard = { viewModel.setNavigationTab(AppNavigationTab.WIZARD) },
                            onNavigateToTab = { viewModel.setNavigationTab(it) }
                        )
                    }

                    AppNavigationTab.EXPLORE -> {
                        ExploreScreen(
                            language = uiState.language,
                            searchQuery = uiState.searchQuery,
                            selectedCategory = uiState.selectedCategory,
                            selectedProfile = uiState.selectedProfile,
                            onlineOnly = uiState.onlineOnly,
                            categories = uiState.categories,
                            procedures = uiState.filteredProcedures,
                            bookmarkedIds = uiState.bookmarkedProcedureIds,
                            summaries = uiState.savedSummaries,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onSelectCategory = { viewModel.selectCategory(it) },
                            onSelectProfile = { viewModel.selectProfile(it) },
                            onToggleOnlineOnly = { viewModel.toggleOnlineOnly() },
                            onResetFilters = { viewModel.resetFilters() },
                            onSelectProcedure = { viewModel.selectProcedure(it) },
                            onToggleBookmark = { viewModel.toggleBookmark(it) }
                        )
                    }

                    AppNavigationTab.WIZARD -> {
                        WizardScreen(
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
                            onSelectProcedure = { viewModel.selectProcedure(it) },
                            onBackToHome = { viewModel.setNavigationTab(AppNavigationTab.HOME) }
                        )
                    }

                    AppNavigationTab.CHECKLIST -> {
                        ChecklistScreen(
                            language = uiState.language,
                            summaries = uiState.savedSummaries,
                            procedures = uiState.procedures,
                            onSelectProcedure = { viewModel.selectProcedure(it) },
                            onExploreClick = { viewModel.setNavigationTab(AppNavigationTab.EXPLORE) },
                            onStartWizard = { viewModel.setNavigationTab(AppNavigationTab.WIZARD) }
                        )
                    }

                    AppNavigationTab.SAVED -> {
                        SavedGuidesScreen(
                            language = uiState.language,
                            procedures = uiState.procedures,
                            bookmarkedIds = uiState.bookmarkedProcedureIds,
                            summaries = uiState.savedSummaries,
                            onSelectProcedure = { viewModel.selectProcedure(it) },
                            onToggleBookmark = { viewModel.toggleBookmark(it) },
                            onExploreClick = { viewModel.setNavigationTab(AppNavigationTab.EXPLORE) }
                        )
                    }

                    AppNavigationTab.REMINDERS -> {
                        RemindersScreen(
                            language = uiState.language,
                            reminders = uiState.reminders,
                            procedures = uiState.procedures,
                            onAddReminder = { pId, pTitle, title, date, days, notes ->
                                viewModel.addReminder(pId, pTitle, title, date, days, notes)
                            },
                            onToggleReminder = { viewModel.toggleReminder(it) },
                            onDeleteReminder = { viewModel.deleteReminder(it) },
                            onSelectProcedureById = { id ->
                                val found = uiState.procedures.find { it.id == id }
                                if (found != null) viewModel.selectProcedure(found)
                            },
                            onTestNotification = { title, msg ->
                                viewModel.triggerTestNotification(title, msg)
                            }
                        )
                    }

                    AppNavigationTab.SETTINGS -> {
                        SettingsScreen(
                            language = uiState.language,
                            onToggleLanguage = {
                                val next = if (uiState.language == Language.RO) Language.EN else Language.RO
                                viewModel.setLanguage(next)
                            },
                            onOpenOfficialPortals = { viewModel.openOfficialPortals() },
                            onOpenLegalVerification = { viewModel.openLegalVerification() },
                            onOpenFeedback = { viewModel.openFeedback() },
                            onClearAllData = { viewModel.clearAllLocalData() }
                        )
                    }
                }
            }
        }
    }

    // Add Reminder Dialog
    if (showAddReminderDialog) {
        AddReminderDialog(
            language = uiState.language,
            procedures = uiState.procedures,
            preselectedProcedure = procedureForReminder,
            onDismiss = {
                showAddReminderDialog = false
                procedureForReminder = null
            },
            onSaveReminder = { pId, pTitle, title, date, days, notes ->
                viewModel.addReminder(pId, pTitle, title, date, days, notes)
                showAddReminderDialog = false
                procedureForReminder = null
            }
        )
    }

    // Feedback Report Dialog
    if (uiState.isFeedbackOpen) {
        FeedbackReportDialog(
            language = uiState.language,
            onDismiss = { viewModel.closeFeedback() },
            onSubmit = { category, msg ->
                viewModel.closeFeedback()
                val message = if (isRo) "Mulțumim! Sesizarea ta a fost înregistrată." else "Thank you! Your feedback has been recorded."
                viewModel.showToast(message)
            }
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
