package fr.taoufikcode.presentation.list

import app.cash.turbine.test
import fr.taoufikcode.domain.model.home.SmartphoneSummary
import fr.taoufikcode.domain.usecase.home.GetSmartphonesSummaryUseCase
import fr.taoufikcode.domain.usecase.home.GetSyncStatusUseCase
import fr.taoufikcode.domain.usecase.home.SaveSyncDateUseCase
import fr.taoufikcode.domain.usecase.home.SyncSmartphonesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SmartphoneListViewModelTest {

    private lateinit var getSmartphonesUseCase: GetSmartphonesSummaryUseCase
    private lateinit var syncUseCase: SyncSmartphonesUseCase
    private lateinit var getSyncStatusUseCase: GetSyncStatusUseCase
    private lateinit var saveSyncDateUseCase: SaveSyncDateUseCase

    private lateinit var viewModel: SmartphoneListViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getSmartphonesUseCase = mockk(relaxed = true)
        syncUseCase = mockk(relaxed = true)
        getSyncStatusUseCase = mockk(relaxed = true)
        saveSyncDateUseCase = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when smartphones loaded successfully then state contains smartphones`() = runTest {
        //Given
        val smartphones = listOf(
            SmartphoneSummary("1", "iPhone 15", "url1"),
            SmartphoneSummary("2", "Samsung S24", "url2")
        )
        every { getSmartphonesUseCase() } returns flowOf(smartphones)
        coEvery { getSyncStatusUseCase.invoke() } returns flowOf(0L)

        // When
        viewModel = SmartphoneListViewModel(
            getSmartphonesUseCase,
            syncUseCase,
            getSyncStatusUseCase,
            saveSyncDateUseCase
        )

        // Then
        testScheduler.advanceUntilIdle()
        
        val state = viewModel.state.value
        assertEquals(2, state.smartphones.size)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `when OnRefresh action then sync is triggered`() = runTest {
        // Given
        every { getSmartphonesUseCase() } returns flowOf(emptyList())
        coEvery { getSyncStatusUseCase.invoke() } returns flowOf(0L)
        coEvery { syncUseCase() } returns Result.success(Unit)

        viewModel = SmartphoneListViewModel(
            getSmartphonesUseCase,
            syncUseCase,
            getSyncStatusUseCase,
            saveSyncDateUseCase
        )

        testScheduler.advanceUntilIdle()

        // When
        viewModel.onAction(ListActions.Refresh)
        testScheduler.advanceUntilIdle()

        // Then
        coVerify { syncUseCase() }
    }

    @Test
    fun `when sync fails then error event is emitted`() = runTest {
        // Given
        val errorMessage = "Sync failed"
        every { getSmartphonesUseCase() } returns flowOf(emptyList())
        coEvery { getSyncStatusUseCase.invoke() } returns flowOf(0L)
        coEvery { syncUseCase() } returns Result.failure(Exception(errorMessage))

        viewModel = SmartphoneListViewModel(
            getSmartphonesUseCase,
            syncUseCase,
            getSyncStatusUseCase,
            saveSyncDateUseCase
        )

        testScheduler.advanceUntilIdle()

        // When/Then
        viewModel.events.test {
            viewModel.onAction(ListActions.Refresh)
            testScheduler.advanceUntilIdle()

            val event = awaitItem()
            assertTrue(event is SmartphoneListEvent.ShowError)
            assertTrue((event as SmartphoneListEvent.ShowError).message.contains(errorMessage))
        }
    }

}
