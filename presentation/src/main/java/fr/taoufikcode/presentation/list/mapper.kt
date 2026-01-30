package fr.taoufikcode.presentation.list

import fr.taoufikcode.domain.model.home.SmartphoneSummary

internal fun SmartphoneSummary.toUi(): SmartphoneSummaryUi {
    return SmartphoneSummaryUi(
        id = id,
        model = model,
        imageUrl = imageUrl
    )
}
