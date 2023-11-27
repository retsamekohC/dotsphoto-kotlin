package dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionDto(
    val id: Long,
    val planId: Long,
    val dateFrom: LocalDateTime,
    val dateTo: LocalDateTime
)