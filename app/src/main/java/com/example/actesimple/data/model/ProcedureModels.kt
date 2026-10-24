package com.example.actesimple.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class Language {
    RO, EN
}

@Serializable
enum class ApplicantProfile(val id: String, val labelRo: String, val labelEn: String) {
    ALL("all", "Toți solicitanții", "All applicants"),
    ROMANIAN("romanian", "Cetățeni Români", "Romanian Citizens"),
    EU_EEA_CH("eu_eea_ch", "Cetățeni UE / SEE / CH", "EU / EEA / Swiss Citizens"),
    NON_EU("non_eu", "Cetățeni Non-UE", "Non-EU Citizens"),
    FAMILY_MEMBER("family_member", "Membri de familie", "Family Members")
}

@Serializable
enum class ProcedureDifficulty(val id: String, val labelRo: String, val labelEn: String) {
    USOR("usor", "Ușor", "Easy"),
    MEDIU("mediu", "Mediu", "Moderate"),
    COMPLEX("complex", "Complex", "Complex")
}

@Serializable
data class CategoryMeta(
    val id: String,
    val nameRo: String,
    val nameEn: String,
    val descriptionRo: String,
    val descriptionEn: String
)

@Serializable
data class DocumentItem(
    val id: String,
    val procedureId: String,
    val name: String,
    val nameEn: String? = null,
    val type: String = "original", // original, copie, formular, dovada_plata
    val isMandatory: Boolean = true,
    val description: String,
    val descriptionEn: String? = null,
    val whereToGet: String? = null,
    val whereToGetEn: String? = null,
    val downloadUrl: String? = null,
    val downloadLabel: String? = null,
    val orderIndex: Int = 1
)

@Serializable
data class StepItem(
    val id: String,
    val procedureId: String,
    val stepNumber: Int,
    val title: String,
    val titleEn: String? = null,
    val description: String,
    val descriptionEn: String? = null,
    val locationOrTool: String? = null,
    val tip: String? = null,
    val tipEn: String? = null,
    val externalUrl: String? = null,
    val externalLabel: String? = null
)

@Serializable
data class SourceItem(
    val id: String,
    val procedureId: String,
    val institutionName: String,
    val url: String,
    val lastVerifiedAt: String,
    val notes: String? = null
)

@Serializable
data class FaqItem(
    val question: String,
    val questionEn: String? = null,
    val answer: String,
    val answerEn: String? = null
)

@Serializable
data class Procedure(
    val id: String,
    val slug: String,
    val title: String,
    val titleEn: String? = null,
    val shortDescription: String,
    val shortDescriptionEn: String? = null,
    val fullDescription: String,
    val fullDescriptionEn: String? = null,
    val category: String,
    val estimatedTime: String,
    val estimatedTimeEn: String? = null,
    val estimatedTimeMinutes: Int? = null,
    val difficulty: ProcedureDifficulty = ProcedureDifficulty.USOR,
    val totalFee: String,
    val totalFeeEn: String? = null,
    val competentAuthority: String,
    val competentAuthorityEn: String? = null,
    val authorityCityNote: String? = null,
    val authorityCityNoteEn: String? = null,
    val isOnlineAvailable: Boolean = false,
    val lastVerifiedAt: String = "2026-10-15",
    val verificationStatus: String = "verified",
    val verifiedBy: String = "Specialist Proceduri ActeSimple",
    val verificationNotes: String? = null,
    val isPopular: Boolean = false,
    val targetProfiles: List<ApplicantProfile> = listOf(ApplicantProfile.ALL),
    val stayPurposes: List<String> = emptyList(),
    val keywords: List<String> = emptyList(),
    val synonyms: List<String> = emptyList(),
    val proTips: List<String> = emptyList(),
    val proTipsEn: List<String> = emptyList(),
    val documents: List<DocumentItem> = emptyList(),
    val steps: List<StepItem> = emptyList(),
    val sources: List<SourceItem> = emptyList(),
    val faq: List<FaqItem> = emptyList(),
    val officialUrl: String? = null,
    val recommendationRationaleRo: String? = null,
    val recommendationRationaleEn: String? = null,
    val expiryDate: String? = null
)

@Serializable
enum class AppNavigationTab {
    HOME, EXPLORE, WIZARD, CHECKLIST, SAVED, REMINDERS, SETTINGS
}

@Serializable
data class ReminderItem(
    val id: String,
    val procedureId: String,
    val procedureTitle: String,
    val title: String,
    val notes: String = "",
    val targetDate: String, // YYYY-MM-DD
    val presetDays: Int? = null, // e.g. 90, 60, 30, 7
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class CustomDoc(
    val id: String,
    val name: String,
    val isChecked: Boolean = false
)

@Serializable
data class UserChecklistState(
    val procedureId: String,
    val checkedDocumentIds: Set<String> = emptySet(),
    val checkedStepNumbers: Set<Int> = emptySet(),
    val customDocuments: List<CustomDoc> = emptyList(),
    val appointmentDate: String = "",
    val appointmentLocation: String = "",
    val personalNotes: String = "",
    val lastUpdated: String = ""
)

@Serializable
data class ChecklistSummary(
    val procedureId: String,
    val procedureTitle: String,
    val completedCount: Int,
    val totalCount: Int,
    val lastUpdated: String
)

data class OfficialPortal(
    val id: String,
    val name: String,
    val institution: String,
    val descriptionRo: String,
    val descriptionEn: String,
    val url: String,
    val category: String,
    val badgeRo: String,
    val badgeEn: String
)

@Serializable
data class PublicOffice(
    val id: String,
    val name: String,
    val nameEn: String? = null,
    val type: String,
    val typeLabelRo: String,
    val typeLabelEn: String,
    val categories: List<String>,
    val procedureIds: List<String> = emptyList(),
    val city: String,
    val county: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val mapX: Float,
    val mapY: Float,
    val scheduleRo: String,
    val scheduleEn: String,
    val publicTransitRo: String? = null,
    val publicTransitEn: String? = null,
    val phone: String? = null,
    val appointmentUrl: String? = null,
    val googleMapsQuery: String
)
