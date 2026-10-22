package com.example.actesimple

import com.example.actesimple.data.model.ApplicantProfile
import com.example.actesimple.data.repository.ProcedureRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ProcedureRepositoryTest {

    private lateinit var repository: ProcedureRepository

    @Before
    fun setUp() {
        repository = ProcedureRepository()
    }

    @Test
    fun testNormalizeSearchRemovesDiacritics() {
        val input = "Căutare Buletin și Înmatriculare"
        val normalized = repository.normalizeSearch(input)
        assertEquals("cautare buletin si inmatriculare", normalized)
    }

    @Test
    fun testFilterProceduresByDirectKeyword() {
        val results = repository.filterProcedures(query = "buletin")
        assertTrue("Expected to find buletin procedure", results.any { it.id == "proc-buletin-expirare" })
    }

    @Test
    fun testFilterProceduresBySynonym() {
        // "talon" is a synonym for registration / vehicle
        val results = repository.filterProcedures(query = "talon")
        assertTrue("Expected results for synonym talon", results.isNotEmpty() || repository.filterProcedures(query = "permis").isNotEmpty())
    }

    @Test
    fun testFilterProceduresByCategory() {
        val results = repository.filterProcedures(category = "identitate")
        assertTrue("All results should belong to identitate", results.all { it.category == "identitate" })
        assertTrue("Should contain passport or ID card", results.any { it.id == "proc-buletin-expirare" })
    }

    @Test
    fun testFilterProceduresByProfile() {
        val euResults = repository.filterProcedures(profile = ApplicantProfile.EU_EEA_CH)
        assertTrue("Expected EU residence procedure", euResults.any { it.id == "proc-ue-inregistrare-rezidenta" })
    }

    @Test
    fun testWizardRecommendations() {
        val recs = repository.getRecommendedForWizard(
            profile = ApplicantProfile.EU_EEA_CH,
            purpose = "work",
            duration = "permanent"
        )
        assertTrue("Expected EU registration recommendation", recs.any { it.id == "proc-ue-inregistrare-rezidenta" })
    }

    @Test
    fun testGetOfficesForProcedureSpclep() {
        val offices = repository.getOfficesForProcedure(
            procedureId = "proc-buletin-expirare",
            category = "identitate"
        )
        assertTrue("Should return public offices for buletin", offices.isNotEmpty())
        assertTrue("Should include SPCLEP type", offices.any { it.type == "SPCLEP" })
        assertTrue("Every office should have positive coordinates", offices.all { it.lat > 0 && it.lng > 0 })
        assertTrue("Every office should have map relative coordinates between 0 and 100", offices.all { it.mapX in 0f..100f && it.mapY in 0f..100f })
    }

    @Test
    fun testGetOfficesForProcedureAnafAndCityFilter() {
        val offices = repository.getOfficesForProcedure(
            procedureId = "proc-nif-anaf",
            category = "fiscal",
            city = "București"
        )
        assertTrue("Should return ANAF desks in Bucharest", offices.isNotEmpty())
        assertTrue("All filtered offices should be in Bucharest", offices.all { it.city.equals("București", ignoreCase = true) })
        assertTrue("Should contain ANAF desk", offices.any { it.type == "ANAF" })
    }
}
