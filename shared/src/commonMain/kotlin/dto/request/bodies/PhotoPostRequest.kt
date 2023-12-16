package dto.request.bodies

import kotlinx.serialization.Serializable

@Serializable
data class PhotoPostRequest(val b64: String, val photoName: String)