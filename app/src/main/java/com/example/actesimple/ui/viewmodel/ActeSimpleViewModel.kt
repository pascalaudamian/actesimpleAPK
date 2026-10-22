package com.example.actesimple.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.actesimple.data.local.ChecklistPreferences
import com.example.actesimple.data.model.*
import com.example.actesimple.data.repository.ProcedureRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ActeSimpleUiState(
    val language: Language = Language.RO,
    val searchQuery: String = "",
    val selectedCategory: String = "toate",
    val selectedProfile: ApplicantProfile = ApplicantProfile.ALL,
    val selectedDifficulty: String = "all",
    val onlineOnly: Boolean = false,
    val categories: List<CategoryMeta> = emptyList(),
    val procedures: List<Procedure> = emptyList(),
    val filteredProcedures: List<Procedure> = emptyList(),
    val selectedProcedure: Procedure? = null,
    val activeDetailTab: Int = 0,
    val currentChecklist: UserChecklistState = UserChecklistState(""),
    val savedSummaries: List<ChecklistSummary> = emptyList(),
    val isWizardOpen: Boolean = false,
    val wizardStep: Int = 1,
    val wizardProfile: ApplicantProfile = ApplicantProfile.ROMANIAN,
    val wizardPurpose: String = "id_civil_status",
    val wizardDuration: String = "permanent",
    val wizardRecommendations: List<Procedure> = emptyList(),
    val isSavedDossiersOpen: Boolean = false,
    val isOfficialPortalsOpen: Boolean = false,
    val isLegalVerificationOpen: Boolean = false,
    val officialPortals: List<OfficialPortal> = emptyList(),
    val toastMessage: String? = null
)

class ActeSimpleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProcedureRepository()
    private val preferences = ChecklistPreferences(application)

    private val _uiState = MutableStateFlow(ActeSimpleUiState())
    val uiState: StateFlow<ActeSimpleUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val allProcs = repository.getAllProcedures()
        val cats = repository.getCategories()
        val portals = repository.getOfficialPortals()

        _uiState.update { current ->
            current.copy(
                categories = cats,
                procedures = allProcs,
                filteredProcedures = allProcs,
                officialPortals = portals,
                savedSummaries = computeSavedSummaries(allProcs)
            )
        }
    }

    private fun computeSavedSummaries(procedures: List<Procedure>): List<ChecklistSummary> {
        val savedIds = preferences.getAllSavedProcedureIds()
        return savedIds.mapNotNull { id ->
            val proc = procedures.find { it.id == id } ?: return@mapNotNull null
            val state = preferences.getChecklistState(id)
            val checkedDocs = state.checkedDocumentIds.size + state.customDocuments.count { it.isChecked }
            val totalDocs = proc.documents.size + state.customDocuments.size
            if (checkedDocs > 0 || state.appointmentDate.isNotBlank() || state.personalNotes.isNotBlank()) {
                ChecklistSummary(
                    procedureId = proc.id,
                    procedureTitle = proc.title,
                    completedCount = checkedDocs,
                    totalCount = totalDocs,
                    lastUpdated = state.lastUpdated
                )
            } else null
        }
    }

    private fun applyFilters() {
        val s = _uiState.value
        val filtered = repository.filterProcedures(
            query = s.searchQuery,
            category = s.selectedCategory,
            profile = s.selectedProfile,
            difficulty = s.selectedDifficulty,
            onlineOnly = s.onlineOnly,
            lang = s.language
        )
        _uiState.update { it.copy(filteredProcedures = filtered) }
    }

    fun setLanguage(lang: Language) {
        _uiState.update { it.copy(language = lang) }
        applyFilters()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun selectCategory(catId: String) {
        _uiState.update { it.copy(selectedCategory = catId) }
        applyFilters()
    }

    fun selectProfile(profile: ApplicantProfile) {
        _uiState.update { it.copy(selectedProfile = profile) }
        applyFilters()
    }

    fun selectDifficulty(diff: String) {
        _uiState.update { it.copy(selectedDifficulty = diff) }
        applyFilters()
    }

    fun toggleOnlineOnly() {
        _uiState.update { it.copy(onlineOnly = !it.onlineOnly) }
        applyFilters()
    }

    fun resetFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedCategory = "toate",
                selectedProfile = ApplicantProfile.ALL,
                selectedDifficulty = "all",
                onlineOnly = false
            )
        }
        applyFilters()
    }

    fun selectProcedure(procedure: Procedure) {
        val checklistState = preferences.getChecklistState(procedure.id)
        _uiState.update {
            it.copy(
                selectedProcedure = procedure,
                activeDetailTab = 0,
                currentChecklist = checklistState,
                isWizardOpen = false,
                isSavedDossiersOpen = false,
                isOfficialPortalsOpen = false
            )
        }
    }

    fun closeDetail() {
        _uiState.update {
            it.copy(
                selectedProcedure = null,
                savedSummaries = computeSavedSummaries(it.procedures)
            )
        }
    }

    fun setActiveDetailTab(tabIndex: Int) {
        _uiState.update { it.copy(activeDetailTab = tabIndex) }
    }

    fun toggleDocumentCheck(docId: String) {
        val proc = _uiState.value.selectedProcedure ?: return
        val updated = preferences.toggleDocument(proc.id, docId)
        _uiState.update {
            it.copy(
                currentChecklist = updated,
                savedSummaries = computeSavedSummaries(it.procedures)
            )
        }
    }

    fun toggleStepCheck(stepNumber: Int) {
        val proc = _uiState.value.selectedProcedure ?: return
        val updated = preferences.toggleStep(proc.id, stepNumber)
        _uiState.update { it.copy(currentChecklist = updated) }
    }

    fun addCustomDoc(name: String) {
        val proc = _uiState.value.selectedProcedure ?: return
        if (name.isBlank()) return
        val updated = preferences.addCustomDoc(proc.id, name)
        _uiState.update {
            it.copy(
                currentChecklist = updated,
                savedSummaries = computeSavedSummaries(it.procedures)
            )
        }
    }

    fun toggleCustomDoc(customDocId: String) {
        val proc = _uiState.value.selectedProcedure ?: return
        val updated = preferences.toggleCustomDoc(proc.id, customDocId)
        _uiState.update {
            it.copy(
                currentChecklist = updated,
                savedSummaries = computeSavedSummaries(it.procedures)
            )
        }
    }

    fun checkAllDocuments() {
        val proc = _uiState.value.selectedProcedure ?: return
        val allIds = proc.documents.map { it.id }
        val updated = preferences.checkAllDocuments(proc.id, allIds)
        _uiState.update {
            it.copy(
                currentChecklist = updated,
                savedSummaries = computeSavedSummaries(it.procedures)
            )
        }
    }

    fun resetChecklist() {
        val proc = _uiState.value.selectedProcedure ?: return
        val updated = preferences.resetChecklist(proc.id)
        _uiState.update {
            it.copy(
                currentChecklist = updated,
                savedSummaries = computeSavedSummaries(it.procedures)
            )
        }
    }

    fun updateChecklistNotes(notes: String, date: String, location: String) {
        val proc = _uiState.value.selectedProcedure ?: return
        val updated = preferences.updateNotesAndAppointment(proc.id, notes, date, location)
        _uiState.update {
            it.copy(
                currentChecklist = updated,
                savedSummaries = computeSavedSummaries(it.procedures)
            )
        }
    }

    // Wizard
    fun openWizard() {
        _uiState.update {
            it.copy(
                isWizardOpen = true,
                wizardStep = 1,
                wizardProfile = ApplicantProfile.ROMANIAN,
                wizardPurpose = "id_civil_status",
                wizardDuration = "permanent",
                wizardRecommendations = emptyList()
            )
        }
    }

    fun closeWizard() {
        _uiState.update { it.copy(isWizardOpen = false) }
    }

    fun setWizardProfile(profile: ApplicantProfile) {
        _uiState.update { it.copy(wizardProfile = profile) }
    }

    fun setWizardPurpose(purpose: String) {
        _uiState.update { it.copy(wizardPurpose = purpose) }
    }

    fun setWizardDuration(duration: String) {
        _uiState.update { it.copy(wizardDuration = duration) }
    }

    fun advanceWizardStep() {
        val currentStep = _uiState.value.wizardStep
        if (currentStep < 3) {
            val nextStep = currentStep + 1
            if (nextStep == 3) {
                val recs = repository.getRecommendedForWizard(
                    profile = _uiState.value.wizardProfile,
                    purpose = _uiState.value.wizardPurpose,
                    duration = _uiState.value.wizardDuration
                )
                _uiState.update { it.copy(wizardStep = nextStep, wizardRecommendations = recs) }
            } else {
                _uiState.update { it.copy(wizardStep = nextStep) }
            }
        }
    }

    fun backWizardStep() {
        _uiState.update { it.copy(wizardStep = (it.wizardStep - 1).coerceAtLeast(1)) }
    }

    // Dialogs
    fun openSavedDossiers() {
        _uiState.update {
            it.copy(
                isSavedDossiersOpen = true,
                savedSummaries = computeSavedSummaries(it.procedures)
            )
        }
    }

    fun closeSavedDossiers() {
        _uiState.update { it.copy(isSavedDossiersOpen = false) }
    }

    fun openOfficialPortals() {
        _uiState.update { it.copy(isOfficialPortalsOpen = true) }
    }

    fun closeOfficialPortals() {
        _uiState.update { it.copy(isOfficialPortalsOpen = false) }
    }

    fun openLegalVerification() {
        _uiState.update { it.copy(isLegalVerificationOpen = true) }
    }

    fun closeLegalVerification() {
        _uiState.update { it.copy(isLegalVerificationOpen = false) }
    }

    fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun dismissToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
