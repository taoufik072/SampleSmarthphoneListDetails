package fr.taoufikcode.data.smartphones.repository


import fr.taoufikcode.data.smartphones.remote.api.SmartphoneApi
import fr.taoufikcode.data.smartphones.remote.dto.toDomain
import fr.taoufikcode.domain.repository.smartphone.SmartphoneDetailsRepository
import javax.inject.Inject

class SmartphoneDetailsRepositoryImpl @Inject constructor(
    private val api: SmartphoneApi
) : SmartphoneDetailsRepository {
    override suspend fun getSmartphoneById(id: String) =
        Result.runCatching {
            api.getSmartphoneDetails(id).toDomain()
        }
}
