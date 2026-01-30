package fr.taoufikcode.domain.repository.smartphone

import fr.taoufikcode.domain.model.smartphone.SmartphoneDetails

interface SmartphoneDetailsRepository {
    suspend fun getSmartphoneById(id: String): Result<SmartphoneDetails>
}
