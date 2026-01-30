package fr.taoufikcode.domain.usecase.home

import fr.taoufikcode.domain.repository.home.SmartphonesSummaryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class GetSyncStatusUseCaseTest {

    private lateinit var repository: SmartphonesSummaryRepository
    private lateinit var useCase: GetSyncStatusUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetSyncStatusUseCase(repository)
    }

    @Test
    fun `invoke should return repository flow`() = runTest {
        // Given
        val timestamp = 1234L
        coEvery { repository.getSyncDateHomeStatus() } returns flowOf(timestamp)

        // When
        val result = useCase.invoke()

        // Then
        assertEquals(timestamp, result.first())
        coVerify { repository.getSyncDateHomeStatus() }
    }
}
