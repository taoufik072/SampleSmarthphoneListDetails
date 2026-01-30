package fr.taoufikcode.presentation.details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import fr.taoufikcode.domain.model.smartphone.SmartphoneDetails
import fr.taoufikcode.domain.usecase.smartphone.GetSmartphoneDetailsByIdUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class SmartphoneDetailsViewModelTest {

    private lateinit var getSmartphoneDetailsByIdUseCase: GetSmartphoneDetailsByIdUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: SmartphoneDetailsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getSmartphoneDetailsByIdUseCase = mockk()
        savedStateHandle = SavedStateHandle(mapOf("smartphoneId" to "1"))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when load success then state contains details`() = runTest {
        // Given
        val details = SmartphoneDetails(
            id = "1",
            model = "iPhone 15",
            description = "Desc",
            price = 999.0,
            imageUrl = "url",
            constructionDate = LocalDate.now()
        )
        coEvery { getSmartphoneDetailsByIdUseCase("1") } returns Result.success(details)

        // When
        viewModel = SmartphoneDetailsViewModel(getSmartphoneDetailsByIdUseCase, savedStateHandle)

        // Then
        testScheduler.advanceUntilIdle()
        val state = viewModel.state.value
        
        assertNotNull(state.smartphone)
        assertEquals("iPhone 15", state.smartphone?.model)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `when load failure then state contains error`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { getSmartphoneDetailsByIdUseCase("1") } returns Result.failure(Exception(errorMessage))

        // When
        viewModel = SmartphoneDetailsViewModel(getSmartphoneDetailsByIdUseCase, savedStateHandle)


        // Then
        testScheduler.advanceUntilIdle()
        val state = viewModel.state.value

        assertNull(state.smartphone)
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }
}
