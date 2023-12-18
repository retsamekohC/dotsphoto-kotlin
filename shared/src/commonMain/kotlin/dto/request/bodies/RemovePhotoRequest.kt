package dto.request.bodies

import kotlinx.serialization.Serializable

@Serializable
data class RemovePhotoRequest(val photoId: Long, val albumId: Long)