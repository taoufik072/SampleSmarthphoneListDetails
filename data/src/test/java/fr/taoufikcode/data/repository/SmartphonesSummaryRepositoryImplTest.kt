@file:OptIn(ExperimentalCoroutinesApi::class)

package fr.taoufikcode.data.repository

import android.database.sqlite.SQLiteFullException
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import fr.taoufikcode.data.core.DataError
import fr.taoufikcode.data.core.DataResult
import fr.taoufikcode.data.core.toDomain
import fr.taoufikcode.data.smartphones.local.dao.HomeDao
import fr.taoufikcode.data.smartphones.local.datastore.SyncDataStore
import fr.taoufikcode.data.smartphones.local.entity.SmartphoneSummaryEntity
import fr.taoufikcode.data.smartphones.remote.SmartphoneRemoteDataSource
import fr.taoufikcode.data.smartphones.remote.dto.HomeResponseDto
import fr.taoufikcode.data.smartphones.remote.dto.SmartphoneSummaryDto
import fr.taoufikcode.data.smartphones.repository.SmartphonesSummaryRepositoryImpl
import fr.taoufikcode.data.utils.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SmartphonesSummaryRepositoryImplTest {
    private val dispatchers = TestDispatcherProvider(UnconfinedTestDispatcher())
    private lateinit var dao: HomeDao
    private lateinit var dataStore: SyncDataStore
    private lateinit var remoteDataSource: SmartphoneRemoteDataSource
    private lateinit var repository: SmartphonesSummaryRepositoryImpl

    private val successResponse = DataResult.Success(
        HomeResponseDto(
            smartphones = listOf(
                SmartphoneSummaryDto("1", "iPhone 15", "https://img.test/1.jpg"),
                SmartphoneSummaryDto("2", "Galaxy S24", "https://img.test/2.jpg"),
            )
        )
    )

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        dataStore = mockk(relaxed = true)
        remoteDataSource = mockk()
        coEvery { remoteDataSource.getSmartphoneList() } returns successResponse

        repository = SmartphonesSummaryRepositoryImpl(
            remoteDataSource = remoteDataSource,
            homeDao = dao,
            homeSyncDate = dataStore,
            dispatchers = dispatchers,
        )
    }

    @Test
    fun `observeSmartphonesList returns mapped domain list from dao`() =
        runTest {
            val entity = SmartphoneSummaryEntity("1", "iPhone 15", "https://img.test/1.jpg")
            every { dao.observeHomeItems() } returns flowOf(listOf(entity))

            val result = repository.observeSmartphonesList().first()

            assertThat(result.size).isEqualTo(1)
            assertThat(result[0].model).isEqualTo("iPhone 15")
        }

    @Test
    fun `syncHome on 200 saves entities to dao and returns success`() =
        runTest {
            val result = repository.syncHome()

            assertThat(result.isSuccess).isTrue()
            coVerify { dao.replaceAll(any()) }
        }

    @Test
    fun `syncHome on 500 returns failure with server error message`() =
        runTest {
            coEvery { remoteDataSource.getSmartphoneList() } returns DataResult.Error(DataError.Remote.SERVER)

            val result = repository.syncHome()

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).isEqualTo("Server error. Please try again later.")
        }

    @Test
    fun `syncHome when dao throws SQLiteFullException returns failure with DISK_FULL message`() =
        runTest {
            coEvery { dao.replaceAll(any()) } throws SQLiteFullException()

            val result = repository.syncHome()

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message)
                .isEqualTo(DataError.Local.DISK_FULL.toDomain())
        }

    @Test
    fun `syncHome when dao throws generic exception returns failure with UNKNOWN message`() =
        runTest {
            coEvery { dao.replaceAll(any()) } throws RuntimeException("unexpected db error")

            val result = repository.syncHome()

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message)
                .isEqualTo(DataError.Local.UNKNOWN.toDomain())
        }

    @Test
    fun `saveSyncDateHome when dataStore throws IOException returns failure with DISK_FULL message`() =
        runTest {
            coEvery { dataStore.saveSyncDateHome(any()) } throws IOException("unexpected db error")

            val result = repository.saveSyncDateHome(System.currentTimeMillis())

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message)
                .isEqualTo(DataError.Local.UNKNOWN.toDomain())
        }

    @Test
    fun `saveSyncDateHome when dataStore succeeds returns success`() =
        runTest {
            val result = repository.saveSyncDateHome(System.currentTimeMillis())

            assertThat(result.isSuccess).isTrue()
            coVerify { dataStore.saveSyncDateHome(any()) }
        }
}
