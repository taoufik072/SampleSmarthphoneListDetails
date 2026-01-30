package fr.taoufikcode.domain.usecase.home

import fr.taoufikcode.domain.repository.home.SmartphonesSummaryRepository
import javax.inject.Inject

class SyncSmartphonesUseCase @Inject constructor(
    private val smartphonesSummaryRepository: SmartphonesSummaryRepository
) {
    suspend operator fun invoke() = smartphonesSummaryRepository.syncHome()
}
