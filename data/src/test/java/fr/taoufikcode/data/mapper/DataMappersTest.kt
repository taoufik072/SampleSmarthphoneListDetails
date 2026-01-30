package fr.taoufikcode.data.mapper

import fr.taoufikcode.data.smartphones.local.entity.SmartphoneSummaryEntity
import fr.taoufikcode.data.smartphones.local.entity.toDomain
import fr.taoufikcode.data.smartphones.remote.dto.SmartphoneDetailsDto
import fr.taoufikcode.data.smartphones.remote.dto.SmartphoneSummaryDto
import fr.taoufikcode.data.smartphones.remote.dto.toDomain
import fr.taoufikcode.data.smartphones.remote.dto.toEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class DataMappersTest {

    @Test
    fun `map SmartphoneSummaryDto to Entity`() {
        // Given
        val dto = SmartphoneSummaryDto(
            id = "1",
            model = "iPhone",
            imageUrl = "url"
        )

        // When
        val entity = dto.toEntity()

        // Then
        assertEquals(dto.id, entity.id)
        assertEquals(dto.model, entity.model)
        assertEquals(dto.imageUrl, entity.imageUrl)
    }

    @Test
    fun `map SmartphoneSummaryEntity to Domain`() {
        // Given
        val entity = SmartphoneSummaryEntity(
            id = "1",
            model = "iPhone",
            imageUrl = "url"
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals(entity.id, domain.id)
        assertEquals(entity.model, domain.model)
        assertEquals(entity.imageUrl, domain.imageUrl)
    }

    @Test
    fun `map SmartphoneDetailsDto to Domain`() {
        // Given
        val dto = SmartphoneDetailsDto(
            id = "1",
            model = "iPhone",
            price = 999.0,
            description = "Desc",
            imageUrl = "url",
            constructionDate = "2023-10-10",
        )

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals(dto.id, domain.id)
        assertEquals(dto.model, domain.model)
        assertEquals(dto.price ?: 0.0, domain.price, 0.0)
        assertEquals(dto.description, domain.description)
        assertEquals(dto.imageUrl, domain.imageUrl)
        assertEquals(2023, domain.constructionDate?.year)
        assertEquals(10, domain.constructionDate?.monthValue)
        assertEquals(10, domain.constructionDate?.dayOfMonth)
    }
}
