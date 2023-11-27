package dto

import com.dotsphoto.orm.enums.Statuses
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class AlbumDto(
    val id: Long,
    val albumName: String,
    val createdAt: LocalDateTime,
    val lastUpdatedAt: LocalDateTime,
    val status: Statuses
)