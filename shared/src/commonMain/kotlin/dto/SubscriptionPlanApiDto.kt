package dto

import com.dotsphoto.orm.enums.Statuses
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionPlanApiDto(
    val id: Long,
    val planName: String,
    val availableSpaceGb: Int,
    val periodMonths: Int,
    val priceCop: Int,
    val status: Statuses
)