package com.example.actesimple.data.repository

import com.example.actesimple.data.model.*
import java.text.Normalizer

class ProcedureRepository {

    fun getCategories(): List<CategoryMeta> = SeedData.CATEGORIES

    fun getOfficialPortals(): List<OfficialPortal> = SeedData.OFFICIAL_PORTALS

    fun getAllProcedures(): List<Procedure> = SeedData.PROCEDURES

    fun getProcedureById(id: String): Procedure? {
        return SeedData.PROCEDURES.find { it.id == id || it.slug == id }
    }

    fun normalizeSearch(text: String): String {
        if (text.isBlank()) return ""
        val normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
        return normalized.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .replace("ș", "s")
            .replace("ş", "s")
            .replace("ț", "t")
            .replace("ţ", "t")
            .lowercase()
            .trim()
    }

    fun filterProcedures(
        query: String = "",
        category: String = "toate",
        profile: ApplicantProfile = ApplicantProfile.ALL,
        difficulty: String = "all",
        onlineOnly: Boolean = false,
        lang: Language = Language.RO
    ): List<Procedure> {
        val normQuery = normalizeSearch(query)

        // Find synonyms for any matched keyword
        val queryTokens = normQuery.split("\\s+".toRegex()).filter { it.isNotBlank() }
        val synonymsExpanded = mutableSetOf<String>()
        for (token in queryTokens) {
            synonymsExpanded.add(token)
            SeedData.FRONTEND_SYNONYMS[token]?.let { synList ->
                synList.forEach { synonymsExpanded.add(normalizeSearch(it)) }
            }
        }

        return SeedData.PROCEDURES.filter { proc ->
            // Category filter
            if (category != "toate" && proc.category != category) {
                return@filter false
            }

            // Profile filter
            if (profile != ApplicantProfile.ALL) {
                val matches = proc.targetProfiles.contains(ApplicantProfile.ALL) ||
                              proc.targetProfiles.contains(profile)
                if (!matches) return@filter false
            }

            // Difficulty filter
            if (difficulty != "all" && proc.difficulty.id != difficulty) {
                return@filter false
            }

            // Online only filter
            if (onlineOnly && !proc.isOnlineAvailable) {
                return@filter false
            }

            // Search query filter
            if (normQuery.isNotBlank()) {
                val searchableTexts = listOf(
                    proc.title,
                    proc.titleEn ?: "",
                    proc.shortDescription,
                    proc.shortDescriptionEn ?: "",
                    proc.competentAuthority,
                    proc.competentAuthorityEn ?: "",
                    proc.keywords.joinToString(" "),
                    proc.synonyms.joinToString(" ")
                ).map { normalizeSearch(it) }

                val matchesDirect = searchableTexts.any { it.contains(normQuery) }
                val matchesSynonyms = synonymsExpanded.any { syn ->
                    searchableTexts.any { it.contains(syn) }
                }

                if (!matchesDirect && !matchesSynonyms) {
                    return@filter false
                }
            }

            true
        }
    }

    fun getRecommendedForWizard(
        profile: ApplicantProfile,
        purpose: String,
        duration: String
    ): List<Procedure> {
        val all = SeedData.PROCEDURES
        return when (profile) {
            ApplicantProfile.ROMANIAN -> {
                when (purpose) {
                    "id_civil_status" -> all.filter { it.id == "proc-buletin-expirare" || it.id == "proc-pasaport-electronic" }
                    "auto" -> all.filter { it.id == "proc-permis-conducere" }
                    "business" -> all.filter { it.id == "proc-srl-onrc" }
                    "housing" -> all.filter { it.id == "proc-viza-flotant" }
                    "family" -> all.filter { it.id == "proc-alocatie-copii" }
                    else -> all.filter { it.isPopular }
                }
            }
            ApplicantProfile.EU_EEA_CH -> {
                when (purpose) {
                    "work", "studies", "business" -> {
                        all.filter { it.id == "proc-ue-inregistrare-rezidenta" || it.id == "proc-nif-anaf" || it.id == "proc-cazier-judiciar" }
                    }
                    else -> all.filter { it.targetProfiles.contains(ApplicantProfile.EU_EEA_CH) }
                }
            }
            ApplicantProfile.NON_EU -> {
                when (purpose) {
                    "work" -> all.filter { it.id == "proc-non-ue-sedere-munca" || it.id == "proc-nif-anaf" }
                    "business" -> all.filter { it.id == "proc-srl-onrc" || it.id == "proc-nif-anaf" }
                    else -> all.filter { it.targetProfiles.contains(ApplicantProfile.NON_EU) }
                }
            }
            ApplicantProfile.FAMILY_MEMBER -> {
                all.filter { it.id == "proc-alocatie-copii" || it.id == "proc-ue-inregistrare-rezidenta" }
            }
            ApplicantProfile.ALL -> all.filter { it.isPopular }
        }
    }

    fun getAllPublicOffices(): List<PublicOffice> = SeedData.PUBLIC_OFFICES

    fun getOfficesForProcedure(procedureId: String, category: String, city: String = "toate"): List<PublicOffice> {
        val matching = SeedData.PUBLIC_OFFICES.filter { office ->
            val matchesProcedure = office.procedureIds.contains(procedureId)
            val matchesCategory = office.categories.contains(category)
            matchesProcedure || matchesCategory
        }

        val baseList = if (matching.isNotEmpty()) matching else SeedData.PUBLIC_OFFICES

        return if (city == "toate" || city.isBlank()) {
            baseList
        } else {
            baseList.filter { it.city.equals(city, ignoreCase = true) }
        }
    }
}
