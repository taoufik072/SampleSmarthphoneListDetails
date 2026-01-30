package fr.taoufikcode.domain.usecase.smartphone

import fr.taoufikcode.domain.model.smartphone.SmartphoneDetails
import fr.taoufikcode.domain.repository.smartphone.SmartphoneDetailsRepository
import javax.inject.Inject

class GetSmartphoneDetailsByIdUseCase @Inject constructor(
    private val repository: SmartphoneDetailsRepository
) {
    suspend operator fun invoke(id: String)= repository.getSmartphoneById(id)
}
