package fr.taoufikcode.domain.usecase.home

import fr.taoufikcode.domain.repository.home.SmartphonesSummaryRepository
import javax.inject.Inject

class GetSyncStatusUseCase @Inject constructor(
    private val repository: SmartphonesSummaryRepository
) {
    operator fun invoke() = repository.getSyncDateHomeStatus()
}
