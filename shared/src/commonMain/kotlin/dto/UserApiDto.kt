package dto

import com.dotsphoto.orm.enums.Statuses
import kotlinx.serialization.Serializable

@Serializable
data class UserApiDto(
    val id: Long,
    val nickname: String,
    val rootAlbumId: Long,
    val subscriptionId: Long,
    val status: Statuses
)