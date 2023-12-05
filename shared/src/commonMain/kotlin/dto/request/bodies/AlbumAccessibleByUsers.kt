package dto.request.bodies

import dto.AlbumApiDto
import dto.UserApiDto
import kotlinx.serialization.Serializable

@Serializable
data class AlbumAccessibleByUsers(val albumId: AlbumApiDto, val userIds: List<UserApiDto>)