package com.robusthealth.android.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ProfilePayload(
    val age: Int,
    @SerialName("sex_at_birth") val sexAtBirth: String,
    @SerialName("height_cm") val heightCm: Int,
    @SerialName("weight_kg") val weightKg: Int,
    val goal: String,
    @SerialName("days_per_week") val daysPerWeek: Int,
    @SerialName("session_minutes") val sessionMinutes: Int,
    @SerialName("activity_level") val activityLevel: String,
    val equipment: String,
    val limitations: String? = null,
    @SerialName("diet_preference") val dietPreference: String? = null,
    val allergies: String? = null,
    @SerialName("sleep_hours") val sleepHours: Double? = null,
    @SerialName("stress_level") val stressLevel: String? = null
)

@Serializable
data class ProfileRecord(
    val id: String,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class PlanRequest(val profileId: String)

@Serializable
data class PlanRecord(
    val id: String,
    @SerialName("profile_id") val profileId: String? = null,
    @SerialName("week_start") val weekStart: String? = null,
    @SerialName("plan_json") val planJson: JsonObject? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class PlanInsertRequest(
    @SerialName("profile_id") val profileId: String,
    @SerialName("week_start") val weekStart: String,
    @SerialName("plan_json") val planJson: JsonObject
)

@Serializable
data class MemberRequest(
    val email: String,
    val phone: String? = null,
    @SerialName("profile_id") val profileId: String? = null
)

@Serializable
data class MemberRecord(
    val id: String,
    val email: String,
    val phone: String? = null,
    @SerialName("profile_id") val profileId: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class MemberPlansResponse(
    val member: MemberRecord,
    val plans: List<PlanRecord> = emptyList()
)

@Serializable
data class TrainerLoginRequest(
    val email: String,
    val credential: String
)

@Serializable
data class TrainerRecord(
    val id: String,
    val email: String,
    @SerialName("full_name") val fullName: String? = null,
    val credential: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null
)

@Serializable
data class TrainerClientRecord(
    @SerialName("member_id") val memberId: String,
    @SerialName("trainer_id") val trainerId: String,
    @SerialName("support_area") val supportArea: String? = null,
    @SerialName("package") val packageTier: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val goal: String? = null,
    @SerialName("days_per_week") val daysPerWeek: Int? = null,
    val equipment: String? = null
)

sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val cause: Throwable? = null) : Resource<Nothing>()
}
