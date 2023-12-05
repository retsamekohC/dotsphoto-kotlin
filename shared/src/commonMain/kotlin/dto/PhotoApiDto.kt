package dto

import com.dotsphoto.orm.enums.Statuses
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class PhotoApiDto(
    val id: Long,
    val content: ByteArray,
    val fileName: String,
    val createdAt: LocalDateTime,
    val lastUpdatedAt: LocalDateTime,
    val metadataId: Long?,
    val status: Statuses,
    val albumId: Long
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        other as PhotoApiDto

        if (!content.contentEquals(other.content)) return false
        if (fileName != other.fileName) return false
        if (createdAt != other.createdAt) return false
        if (lastUpdatedAt != other.lastUpdatedAt) return false
        if (metadataId != other.metadataId) return false
        if (status != other.status) return false
        if (albumId != other.albumId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content.contentHashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + lastUpdatedAt.hashCode()
        result = 31 * result + metadataId.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + albumId.hashCode()
        return result
    }
}