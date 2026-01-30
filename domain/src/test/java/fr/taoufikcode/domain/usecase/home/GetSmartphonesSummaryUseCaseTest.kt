package fr.taoufikcode.domain.usecase.home

import fr.taoufikcode.domain.model.home.SmartphoneSummary
import fr.taoufikcode.domain.repository.home.SmartphonesSummaryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class GetSmartphonesSummaryUseCaseTest {

    private lateinit var repository: SmartphonesSummaryRepository
    private lateinit var useCase: GetSmartphonesSummaryUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetSmartphonesSummaryUseCase(repository)
    }

    @Test
    fun `when invoked then should return repository flow`() = runTest {
        // Given
        val smartphones = listOf(
            SmartphoneSummary("1", "iPhone 15", "url1"),
            SmartphoneSummary("2", "Samsung S24", "url2")
        )
        every { repository.observeSmartphonesList() } returns flowOf(smartphones)

        // When
        val result = useCase().first()

        // Then
        assertEquals(smartphones, result)
        verify { repository.observeSmartphonesList() }
    }

    @Test
    fun `when invoked then should delegate to repository`() = runTest {
        // Given
        every { repository.observeSmartphonesList() } returns flowOf(emptyList())

        // When
        useCase()

        // Then
        verify(exactly = 1) { repository.observeSmartphonesList() }
    }
}
