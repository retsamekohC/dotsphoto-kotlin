package dto

import com.dotsphoto.orm.enums.Statuses
import io.ktor.http.content.*
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class AlbumApiDto(
    val id: Long,
    val albumName: String,
    val createdAt: LocalDateTime,
    val lastUpdatedAt: LocalDateTime,
    val status: Statuses
) {
    override fun equals(other: Any?): Boolean {
        if (other == null || other is NullBody) return false

        return (other as AlbumApiDto).id == this.id
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + albumName.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + lastUpdatedAt.hashCode()
        result = 31 * result + status.hashCode()
        return result
    }
}