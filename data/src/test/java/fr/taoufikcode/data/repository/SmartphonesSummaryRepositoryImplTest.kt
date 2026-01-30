package fr.taoufikcode.data.repository

import fr.taoufikcode.data.smartphones.local.dao.HomeDao
import fr.taoufikcode.data.smartphones.local.datastore.SyncDataStore
import fr.taoufikcode.data.smartphones.local.entity.SmartphoneSummaryEntity
import fr.taoufikcode.data.smartphones.remote.api.SmartphoneApi
import fr.taoufikcode.data.smartphones.remote.dto.HomeResponseDto
import fr.taoufikcode.data.smartphones.remote.dto.SmartphoneSummaryDto
import fr.taoufikcode.data.smartphones.repository.SmartphonesSummaryRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SmartphonesSummaryRepositoryImplTest {

    private lateinit var api: SmartphoneApi
    private lateinit var dao: HomeDao
    private lateinit var dataStore: SyncDataStore
    private lateinit var repository: SmartphonesSummaryRepositoryImpl

    @Before
    fun setup() {
        api = mockk()
        dao = mockk(relaxed = true)
        dataStore = mockk(relaxed = true)
        repository = SmartphonesSummaryRepositoryImpl(api, dao, dataStore)
    }

    @Test
    fun `observeSmartphonesList should return mapped domain objects from dao`() = runTest {
        // Given
        val entity = SmartphoneSummaryEntity("1", "iPhone", "url")
        coEvery { dao.observeHomeItems() } returns flowOf(listOf(entity))

        // When
        val result = repository.observeSmartphonesList().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("iPhone", result[0].model)
    }

    @Test
    fun `syncHome success should fetch from api and save to dao`() = runTest {
        // Given
        val dto = SmartphoneSummaryDto("1", "iPhone", "url")
        val response = HomeResponseDto(listOf(dto))
        coEvery { api.getSmartphoneList() } returns response

        // When
        val result = repository.syncHome()

        // Then
        assertTrue(result.isSuccess)
        coVerify { api.getSmartphoneList() }
        coVerify { dao.replaceAll(any()) }
    }

    @Test
    fun `syncHome failure should return failure`() = runTest {
        // Given
        val error = Exception("Network")
        coEvery { api.getSmartphoneList() } throws error

        // When
        val result = repository.syncHome()

        // Then
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }
}
