package dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class PhotoMetadataApiDto(
    val id: Long,
    val widthInPixels: Int?,
    val heightInPixels: Int?,
    val cameraMegapixels: Float?,
    val kilobyteSize: Int?,
    val geolocation: String?,
    val shotAt: LocalDateTime?
)