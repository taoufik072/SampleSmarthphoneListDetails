package fr.taoufikcode.domain.usecase.home

import fr.taoufikcode.domain.repository.home.SmartphonesSummaryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SyncSmartphonesUseCaseTest {

    private lateinit var repository: SmartphonesSummaryRepository
    private lateinit var useCase: SyncSmartphonesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = SyncSmartphonesUseCase(repository)
    }

    @Test
    fun `when sync succeeds then return success`() = runTest {
        // Given
        coEvery { repository.syncHome() } returns Result.success(Unit)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        coVerify { repository.syncHome() }
    }

    @Test
    fun `when sync fails then return failure`() = runTest {
        // Given
        val error = Exception("Network error")
        coEvery { repository.syncHome() } returns Result.failure(error)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
        coVerify { repository.syncHome() }
    }

    @Test
    fun `when invoked then should delegate to repository`() = runTest {
        // Given
        coEvery { repository.syncHome() } returns Result.success(Unit)

        // When
        useCase()

        // Then
        coVerify(exactly = 1) { repository.syncHome() }
    }
}
