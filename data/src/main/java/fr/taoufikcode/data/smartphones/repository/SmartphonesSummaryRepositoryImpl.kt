package fr.taoufikcode.data.smartphones.repository

import fr.taoufikcode.common.coroutines.DispatcherProvider
import fr.taoufikcode.data.core.DataResult
import fr.taoufikcode.data.core.toUserMessage
import fr.taoufikcode.data.smartphones.local.dao.HomeDao
import fr.taoufikcode.data.smartphones.local.datastore.SyncDataStore
import fr.taoufikcode.data.smartphones.local.entity.toDomain
import fr.taoufikcode.data.smartphones.remote.SmartphoneRemoteDataSource
import fr.taoufikcode.data.smartphones.remote.dto.toEntity
import fr.taoufikcode.domain.repository.home.SmartphonesSummaryRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton

@Singleton(binds = [SmartphonesSummaryRepository::class])
class SmartphonesSummaryRepositoryImpl(
    private val remoteDataSource: SmartphoneRemoteDataSource,
    private val homeDao: HomeDao,
    private val homeSyncDate: SyncDataStore,
    private val dispatchers: DispatcherProvider,
) : SmartphonesSummaryRepository {
    override fun observeSmartphonesList() =
        homeDao.observeHomeItems().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun syncHome(): Result<Unit> =
        withContext(dispatchers.io) {
            when (val result = remoteDataSource.getSmartphoneList()) {
                is DataResult.Success -> {
                    val entities = result.data.smartphones.map { it.toEntity() }
                    homeDao.replaceAll(entities)
                    Result.success(Unit)
                }

                is DataResult.Error -> {
                    Result.failure(Exception(result.error.toUserMessage()))
                }
            }
        }

    override suspend fun saveSyncDateHome(timestamp: Long): Result<Unit> =
        withContext(dispatchers.io) {
            homeSyncDate.saveSyncDateHome(timestamp)
            Result.success(Unit)
        }

    override fun getSyncDateHomeStatus() = homeSyncDate.lastSyncDateHome()
}
