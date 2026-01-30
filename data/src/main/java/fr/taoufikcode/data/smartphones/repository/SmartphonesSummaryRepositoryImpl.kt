package fr.taoufikcode.data.smartphones.repository


import fr.taoufikcode.data.smartphones.local.dao.HomeDao
import fr.taoufikcode.data.smartphones.local.datastore.SyncDataStore
import fr.taoufikcode.data.smartphones.local.entity.toDomain
import fr.taoufikcode.data.smartphones.remote.api.SmartphoneApi
import fr.taoufikcode.data.smartphones.remote.dto.toEntity
import fr.taoufikcode.domain.repository.home.SmartphonesSummaryRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.map

class SmartphonesSummaryRepositoryImpl @Inject constructor(
    private val smartphoneApi: SmartphoneApi,
    private val homeDao: HomeDao,
    private val homeSyncDate: SyncDataStore
) : SmartphonesSummaryRepository {

    override fun observeSmartphonesList() =
        homeDao.observeHomeItems().map { it -> it.map { it.toDomain() } }

    override suspend fun syncHome() =
        Result.runCatching {
            val response = smartphoneApi.getSmartphoneList()
            val entities = response.smartphones.map { it.toEntity() }
            homeDao.replaceAll(entities)
        }

    override suspend fun saveSyncDateHome(timestamp: Long) =
        Result.runCatching {
            homeSyncDate.saveSyncDateHome(timestamp)
        }

    override fun getSyncDateHomeStatus() = homeSyncDate.lastSyncDateHome()

}
