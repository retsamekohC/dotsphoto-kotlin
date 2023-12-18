package dto.request.bodies

import kotlinx.serialization.Serializable

@Serializable
data class NewAlbumRequest(val albumName: String)
