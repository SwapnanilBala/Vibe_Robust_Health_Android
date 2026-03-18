package com.robusthealth.android.data.network

import com.robusthealth.android.data.model.MemberRecord
import com.robusthealth.android.data.model.MemberRequest
import com.robusthealth.android.data.model.PlanInsertRequest
import com.robusthealth.android.data.model.PlanRecord
import com.robusthealth.android.data.model.ProfilePayload
import com.robusthealth.android.data.model.ProfileRecord
import com.robusthealth.android.data.model.TrainerClientRecord
import com.robusthealth.android.data.model.TrainerRecord
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApi {

    @Headers("Prefer: return=representation")
    @POST("rest/v1/user_profiles")
    suspend fun createProfile(
        @Body payload: ProfilePayload
    ): List<ProfileRecord>

    @GET("rest/v1/plans")
    suspend fun latestPlan(
        @Query("profile_id") profileIdFilter: String,
        @Query("order") order: String = "created_at.desc",
        @Query("limit") limit: Int = 1
    ): List<PlanRecord>

    @Headers("Prefer: return=representation")
    @POST("rest/v1/plans")
    suspend fun insertPlan(
        @Body payload: PlanInsertRequest
    ): List<PlanRecord>

    @GET("rest/v1/members")
    suspend fun findMember(
        @Query("email") emailFilter: String,
        @Query("limit") limit: Int = 1
    ): List<MemberRecord>

    @GET("rest/v1/members")
    suspend fun findMemberById(
        @Query("id") idFilter: String,
        @Query("limit") limit: Int = 1
    ): List<MemberRecord>

    @Headers("Prefer: return=representation")
    @POST("rest/v1/members")
    suspend fun upsertMember(
        @Body payload: MemberRequest
    ): List<MemberRecord>

    @GET("rest/v1/trainers")
    suspend fun trainerLogin(
        @Query("email") emailFilter: String,
        @Query("credential") credentialFilter: String,
        @Query("limit") limit: Int = 1
    ): List<TrainerRecord>

    @GET("rest/v1/trainer_clients")
    suspend fun trainerClients(
        @Query("trainer_id") trainerIdFilter: String,
        @Query("select") select: String = "member_id,trainer_id,support_area,package,email:members(email),phone:members(phone),goal:user_profiles(goal),days_per_week:user_profiles(days_per_week),equipment:user_profiles(equipment)"
    ): List<TrainerClientRecord>
}
