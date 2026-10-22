package com.example.actesimple.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.actesimple.data.model.CustomDoc
import com.example.actesimple.data.model.UserChecklistState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ChecklistPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    fun getChecklistState(procedureId: String): UserChecklistState {
        val raw = prefs.getString(KEY_PREFIX + procedureId, null) ?: return UserChecklistState(procedureId)
        return try {
            json.decodeFromString<UserChecklistState>(raw)
        } catch (e: Exception) {
            UserChecklistState(procedureId)
        }
    }

    fun saveChecklistState(state: UserChecklistState) {
        val raw = json.encodeToString(state)
        prefs.edit().putString(KEY_PREFIX + state.procedureId, raw).apply()
    }

    fun toggleDocument(procedureId: String, docId: String): UserChecklistState {
        val current = getChecklistState(procedureId)
        val newChecked = if (current.checkedDocumentIds.contains(docId)) {
            current.checkedDocumentIds - docId
        } else {
            current.checkedDocumentIds + docId
        }
        val updated = current.copy(
            checkedDocumentIds = newChecked,
            lastUpdated = System.currentTimeMillis().toString()
        )
        saveChecklistState(updated)
        return updated
    }

    fun toggleStep(procedureId: String, stepNumber: Int): UserChecklistState {
        val current = getChecklistState(procedureId)
        val newChecked = if (current.checkedStepNumbers.contains(stepNumber)) {
            current.checkedStepNumbers - stepNumber
        } else {
            current.checkedStepNumbers + stepNumber
        }
        val updated = current.copy(
            checkedStepNumbers = newChecked,
            lastUpdated = System.currentTimeMillis().toString()
        )
        saveChecklistState(updated)
        return updated
    }

    fun addCustomDoc(procedureId: String, name: String): UserChecklistState {
        val current = getChecklistState(procedureId)
        val newDoc = CustomDoc(
            id = "custom-${System.currentTimeMillis()}",
            name = name.trim(),
            isChecked = false
        )
        val updated = current.copy(
            customDocuments = current.customDocuments + newDoc,
            lastUpdated = System.currentTimeMillis().toString()
        )
        saveChecklistState(updated)
        return updated
    }

    fun toggleCustomDoc(procedureId: String, customDocId: String): UserChecklistState {
        val current = getChecklistState(procedureId)
        val updatedDocs = current.customDocuments.map {
            if (it.id == customDocId) it.copy(isChecked = !it.isChecked) else it
        }
        val updated = current.copy(
            customDocuments = updatedDocs,
            lastUpdated = System.currentTimeMillis().toString()
        )
        saveChecklistState(updated)
        return updated
    }

    fun updateNotesAndAppointment(
        procedureId: String,
        notes: String,
        appointmentDate: String,
        appointmentLocation: String
    ): UserChecklistState {
        val current = getChecklistState(procedureId)
        val updated = current.copy(
            personalNotes = notes,
            appointmentDate = appointmentDate,
            appointmentLocation = appointmentLocation,
            lastUpdated = System.currentTimeMillis().toString()
        )
        saveChecklistState(updated)
        return updated
    }

    fun checkAllDocuments(procedureId: String, allDocIds: List<String>): UserChecklistState {
        val current = getChecklistState(procedureId)
        val allSelected = current.checkedDocumentIds.containsAll(allDocIds)
        val newDocs = if (allSelected) emptySet() else allDocIds.toSet()
        val updatedCustom = current.customDocuments.map { it.copy(isChecked = !allSelected) }
        val updated = current.copy(
            checkedDocumentIds = newDocs,
            customDocuments = updatedCustom,
            lastUpdated = System.currentTimeMillis().toString()
        )
        saveChecklistState(updated)
        return updated
    }

    fun resetChecklist(procedureId: String): UserChecklistState {
        val empty = UserChecklistState(procedureId)
        prefs.edit().remove(KEY_PREFIX + procedureId).apply()
        return empty
    }

    fun getAllSavedProcedureIds(): List<String> {
        val keys = prefs.all.keys
        return keys.filter { it.startsWith(KEY_PREFIX) }
            .map { it.removePrefix(KEY_PREFIX) }
    }

    companion object {
        private const val PREFS_NAME = "actesimple_checklists"
        private const val KEY_PREFIX = "chk_"
    }
}
