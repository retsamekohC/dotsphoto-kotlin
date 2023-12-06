package dto.request.bodies

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(val name: String, val password: String)