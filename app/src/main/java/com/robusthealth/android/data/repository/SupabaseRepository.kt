package com.robusthealth.android.data.repository

import com.robusthealth.android.data.model.MemberPlansResponse
import com.robusthealth.android.data.model.MemberRecord
import com.robusthealth.android.data.model.MemberRequest
import com.robusthealth.android.data.model.PlanInsertRequest
import com.robusthealth.android.data.model.PlanRecord
import com.robusthealth.android.data.model.ProfilePayload
import com.robusthealth.android.data.model.ProfileRecord
import com.robusthealth.android.data.model.Resource
import com.robusthealth.android.data.model.TrainerClientRecord
import com.robusthealth.android.data.model.TrainerLoginRequest
import com.robusthealth.android.data.model.TrainerRecord
import com.robusthealth.android.data.network.SupabaseApi
import com.robusthealth.android.data.network.SupabaseClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

interface HealthRepository {
    suspend fun createProfile(payload: ProfilePayload): Resource<ProfileRecord>
    suspend fun generatePlan(profileId: String): Resource<PlanRecord>
    suspend fun fetchLatestPlan(profileId: String): Resource<PlanRecord?>
    suspend fun memberLogin(email: String): Resource<MemberRecord>
    suspend fun memberRegister(request: MemberRequest): Resource<MemberRecord>
    suspend fun memberPlans(memberId: String, profileId: String?): Resource<MemberPlansResponse>
    suspend fun trainerLogin(request: TrainerLoginRequest): Resource<TrainerRecord>
    suspend fun trainerClients(trainerId: String): Resource<List<TrainerClientRecord>>
}

class SupabaseRepository(
    private val api: SupabaseApi = SupabaseClient.api,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : HealthRepository {

    override suspend fun createProfile(payload: ProfilePayload): Resource<ProfileRecord> = runCatching {
        withContext(Dispatchers.IO) {
            if (!SupabaseClient.isConfigured) {
                return@withContext demoProfile()
            }
            val inserted = api.createProfile(payload)
            inserted.firstOrNull() ?: demoProfile()
        }
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error("Failed to create profile", it) }
    )

    override suspend fun generatePlan(profileId: String): Resource<PlanRecord> = runCatching {
        withContext(Dispatchers.IO) {
            val planJson = buildLocalPlanJson()
            if (!SupabaseClient.isConfigured) {
                return@withContext demoPlan(profileId, planJson)
            }
            val payload = PlanInsertRequest(
                profileId = profileId,
                weekStart = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                planJson = planJson
            )
            api.insertPlan(payload).firstOrNull() ?: demoPlan(profileId, planJson)
        }
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error("Failed to generate plan", it) }
    )

    override suspend fun fetchLatestPlan(profileId: String): Resource<PlanRecord?> = runCatching {
        withContext(Dispatchers.IO) {
            if (!SupabaseClient.isConfigured) return@withContext demoPlan(profileId, buildLocalPlanJson())
            api.latestPlan(profileIdFilter = "eq.$profileId").firstOrNull()
        }
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error("Failed to fetch plan", it) }
    )

    override suspend fun memberLogin(email: String): Resource<MemberRecord> = runCatching {
        withContext(Dispatchers.IO) {
            if (!SupabaseClient.isConfigured) return@withContext demoMember()
            api.findMember(emailFilter = "eq.$email").firstOrNull() ?: throw IllegalStateException("Member not found")
        }
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error("Member login failed", it) }
    )

    override suspend fun memberRegister(request: MemberRequest): Resource<MemberRecord> = runCatching {
        withContext(Dispatchers.IO) {
            if (!SupabaseClient.isConfigured) return@withContext demoMember().copy(email = request.email, profileId = request.profileId)
            api.upsertMember(request).firstOrNull() ?: throw IllegalStateException("Upsert failed")
        }
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error("Member registration failed", it) }
    )

    override suspend fun memberPlans(memberId: String, profileId: String?): Resource<MemberPlansResponse> = runCatching {
        withContext(Dispatchers.IO) {
            if (!SupabaseClient.isConfigured) {
                val member = demoMember().copy(id = memberId, profileId = profileId)
                return@withContext MemberPlansResponse(member, listOf(demoPlan(profileId ?: member.profileId.orEmpty(), buildLocalPlanJson())))
            }
            val member = api.findMemberById(idFilter = "eq.$memberId").firstOrNull()
                ?: throw IllegalStateException("Member not found")
            val targetProfileId = profileId ?: member.profileId
            val plans = targetProfileId?.let { id ->
                api.latestPlan(profileIdFilter = "eq.$id", limit = 25, order = "created_at.desc")
            } ?: emptyList()
            MemberPlansResponse(member, plans)
        }
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error("Failed to load member plans", it) }
    )

    override suspend fun trainerLogin(request: TrainerLoginRequest): Resource<TrainerRecord> = runCatching {
        withContext(Dispatchers.IO) {
            if (!SupabaseClient.isConfigured) return@withContext demoTrainer().copy(email = request.email)
            api.trainerLogin(emailFilter = "eq.${request.email}", credentialFilter = "eq.${request.credential}")
                .firstOrNull() ?: throw IllegalStateException("Trainer not found")
        }
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error("Trainer login failed", it) }
    )

    override suspend fun trainerClients(trainerId: String): Resource<List<TrainerClientRecord>> = runCatching {
        withContext(Dispatchers.IO) {
            if (!SupabaseClient.isConfigured) return@withContext demoTrainerClients()
            api.trainerClients(trainerIdFilter = "eq.$trainerId")
        }
    }.fold(
        onSuccess = { Resource.Success(it) },
        onFailure = { Resource.Error("Failed to load trainer clients", it) }
    )

    private fun demoProfile(): ProfileRecord = ProfileRecord(id = UUID.randomUUID().toString())

    private fun demoPlan(profileId: String, planJson: kotlinx.serialization.json.JsonObject): PlanRecord = PlanRecord(
        id = UUID.randomUUID().toString(),
        profileId = profileId,
        weekStart = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
        planJson = planJson,
        createdAt = LocalDate.now().toString()
    )

    private fun demoMember(): MemberRecord = MemberRecord(
        id = UUID.randomUUID().toString(),
        email = "demo.member@robusthealth.app",
        phone = "+1-555-0100",
        profileId = UUID.randomUUID().toString()
    )

    private fun demoTrainer(): TrainerRecord = TrainerRecord(
        id = UUID.randomUUID().toString(),
        email = "trainer@robusthealth.app",
        fullName = "Demo Trainer",
        credential = "TRAINER-ACCESS-101",
        photoUrl = null
    )

    private fun demoTrainerClients(): List<TrainerClientRecord> = listOf(
        TrainerClientRecord(
            memberId = UUID.randomUUID().toString(),
            trainerId = UUID.randomUUID().toString(),
            supportArea = "Both",
            packageTier = "basic",
            email = "liam.member@afp.com",
            phone = "+1-555-0101",
            goal = "fat_loss",
            daysPerWeek = 4,
            equipment = "gym"
        ),
        TrainerClientRecord(
            memberId = UUID.randomUUID().toString(),
            trainerId = UUID.randomUUID().toString(),
            supportArea = "Workout",
            packageTier = "pro",
            email = "mia.member@afp.com",
            phone = "+1-555-0102",
            goal = "strength",
            daysPerWeek = 5,
            equipment = "dumbbells"
        )
    )

    private fun buildLocalPlanJson() = buildJsonObject {
        put("phase", "accumulation")
        put("weekly_volume", "16 sets per major muscle group")
        put("workout", buildJsonArray {
            add(day("Push", listOf("Bench Press 4x8", "Overhead Press 3x10", "Triceps Dips 3x12")))
            add(day("Pull", listOf("Deadlift 4x6", "Pull Ups 4x8", "Face Pulls 3x15")))
            add(day("Legs", listOf("Squat 4x8", "Romanian Deadlift 3x10", "Split Squat 3x12")))
        })
        put("nutrition", buildJsonObject {
            put("calories", 2400)
            put("protein_g", 170)
            put("carbs_g", 240)
            put("fat_g", 70)
            put("water_l", 3)
        })
        put("sleep", buildJsonObject {
            put("target_hours", 8)
            put("wind_down", "30 min pre-bed stretch + screens off")
        })
    }

    private fun day(name: String, lifts: List<String>) = buildJsonObject {
        put("day", name)
        put("exercises", JsonArray(lifts.map { JsonPrimitive(it) }))
    }
}
