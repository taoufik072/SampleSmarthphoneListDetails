@file:OptIn(ExperimentalCoroutinesApi::class)

package fr.taoufikcode.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import fr.taoufikcode.data.core.DataError
import fr.taoufikcode.data.core.DataResult
import fr.taoufikcode.data.smartphones.remote.SmartphoneRemoteDataSource
import fr.taoufikcode.data.smartphones.remote.dto.SmartphoneDetailsDto
import fr.taoufikcode.data.smartphones.repository.SmartphoneDetailsRepositoryImpl
import fr.taoufikcode.data.utils.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SmartphoneDetailsRepositoryImplTest {
    private val dispatchers = TestDispatcherProvider(UnconfinedTestDispatcher())
    private lateinit var remoteDataSource: SmartphoneRemoteDataSource
    private lateinit var repository: SmartphoneDetailsRepositoryImpl

    private val successDto = SmartphoneDetailsDto(
        id = "1",
        model = "iPhone 15",
        price = 999.99,
        description = "Apple flagship",
        constructionDate = "2023-09-12",
        imageUrl = "https://img.test/1.jpg",
    )

    @Before
    fun setUp() {
        remoteDataSource = mockk()
        coEvery { remoteDataSource.getSmartphoneDetails(any()) } returns DataResult.Success(successDto)

        repository = SmartphoneDetailsRepositoryImpl(
            remoteDataSource = remoteDataSource,
            dispatchers = dispatchers,
        )
    }

    @Test
    fun `getSmartphoneById on 200 returns SmartphoneDetails with correct fields`() =
        runTest {
            val result = repository.getSmartphoneById("1")

            assertThat(result.isSuccess).isTrue()
            val details = result.getOrThrow()
            assertThat(details.id).isEqualTo("1")
            assertThat(details.model).isEqualTo("iPhone 15")
            assertThat(details.price).isEqualTo(999.99)
            assertThat(details.constructionDate).isEqualTo(LocalDate.of(2023, 9, 12))
        }

    @Test
    fun `getSmartphoneById on 500 returns failure with server error message`() =
        runTest {
            coEvery { remoteDataSource.getSmartphoneDetails(any()) } returns DataResult.Error(DataError.Remote.SERVER)

            val result = repository.getSmartphoneById("1")

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).isEqualTo("Server error. Please try again later.")
        }

    @Test
    fun `getSmartphoneById on 408 returns failure with timeout message`() =
        runTest {
            coEvery { remoteDataSource.getSmartphoneDetails(any()) } returns DataResult.Error(DataError.Remote.REQUEST_TIMEOUT)

            val result = repository.getSmartphoneById("1")

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).isEqualTo("Request timed out. Please try again.")
        }

    @Test
    fun `getSmartphoneById on 429 returns failure with TOO_MANY_REQUESTS message`() =
        runTest {
            coEvery { remoteDataSource.getSmartphoneDetails(any()) } returns DataResult.Error(DataError.Remote.TOO_MANY_REQUESTS)

            val result = repository.getSmartphoneById("1")

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).isEqualTo("Too many requests. Please wait and retry.")
        }

    @Test
    fun `getSmartphoneById on 401 returns failure with UNKNOWN error message`() =
        runTest {
            coEvery { remoteDataSource.getSmartphoneDetails(any()) } returns DataResult.Error(DataError.Remote.UNKNOWN)

            val result = repository.getSmartphoneById("1")

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).isEqualTo("An unknown error occurred.")
        }
}
