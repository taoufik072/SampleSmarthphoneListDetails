package fr.taoufikcode.presentation.details

import fr.taoufikcode.common.DateFormatters.toDisplayFormat
import fr.taoufikcode.domain.model.home.SmartphoneSummary
import fr.taoufikcode.domain.model.smartphone.SmartphoneDetails
import fr.taoufikcode.presentation.details.SmartphoneDetailsUi
import fr.taoufikcode.presentation.list.SmartphoneSummaryUi

internal fun SmartphoneDetails.toUi(): SmartphoneDetailsUi {
    return SmartphoneDetailsUi(
        id = id,
        model = model,
        imageUrl = imageUrl,
        price = price,
        constructionDate = constructionDate?.toDisplayFormat()?:"",
        description = description
    )
}
