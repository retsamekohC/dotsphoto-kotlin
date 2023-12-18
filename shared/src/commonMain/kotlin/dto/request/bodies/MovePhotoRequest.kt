package dto.request.bodies

import kotlinx.serialization.Serializable

@Serializable
data class MovePhotoRequest(val photoId: Long, val albumId: Long)