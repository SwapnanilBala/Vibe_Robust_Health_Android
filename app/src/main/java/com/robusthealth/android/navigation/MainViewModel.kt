package com.robusthealth.android.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robusthealth.android.data.model.MemberPlansResponse
import com.robusthealth.android.data.model.MemberRecord
import com.robusthealth.android.data.model.MemberRequest
import com.robusthealth.android.data.model.PlanRecord
import com.robusthealth.android.data.model.ProfilePayload
import com.robusthealth.android.data.model.Resource
import com.robusthealth.android.data.model.TrainerClientRecord
import com.robusthealth.android.data.model.TrainerLoginRequest
import com.robusthealth.android.data.model.TrainerRecord
import com.robusthealth.android.data.repository.HealthRepository
import com.robusthealth.android.data.repository.SupabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: HealthRepository = SupabaseRepository()
) : ViewModel() {

    private val _planState = MutableStateFlow<Resource<PlanRecord?>>(Resource.Success(null))
    val planState: StateFlow<Resource<PlanRecord?>> = _planState

    private val _profileId = MutableStateFlow<String?>(null)
    val profileId: StateFlow<String?> = _profileId

    private val _memberState = MutableStateFlow<Resource<MemberPlansResponse>>(Resource.Success(MemberPlansResponse(member = MemberRecord(id = "", email = ""), plans = emptyList())))
    val memberState: StateFlow<Resource<MemberPlansResponse>> = _memberState

    private val _trainerState = MutableStateFlow<Resource<List<TrainerClientRecord>>>(Resource.Success(emptyList()))
    val trainerState: StateFlow<Resource<List<TrainerClientRecord>>> = _trainerState

    private var trainer: TrainerRecord? = null
    private var member: MemberRecord? = null

    fun submitProfile(payload: ProfilePayload) {
        viewModelScope.launch {
            _planState.value = Resource.Loading
            val profileResult = repository.createProfile(payload)
            if (profileResult is Resource.Success) {
                _profileId.value = profileResult.data.id
                generatePlan(profileResult.data.id)
            } else if (profileResult is Resource.Error) {
                _planState.value = profileResult
            }
        }
    }

    fun generatePlan(profileId: String) {
        viewModelScope.launch {
            _planState.value = Resource.Loading
            val result = repository.generatePlan(profileId)
            _planState.value = result
        }
    }

    fun fetchExistingPlan(profileId: String) {
        viewModelScope.launch {
            _planState.value = Resource.Loading
            val result = repository.fetchLatestPlan(profileId)
            _planState.value = result
        }
    }

    fun memberLogin(email: String) {
        viewModelScope.launch {
            _memberState.value = Resource.Loading
            when (val result = repository.memberLogin(email)) {
                is Resource.Success -> {
                    member = result.data
                    val plans = repository.memberPlans(memberId = result.data.id, profileId = result.data.profileId)
                    _memberState.value = plans
                }
                is Resource.Error -> _memberState.value = result
                Resource.Loading -> _memberState.value = Resource.Error("Unexpected state")
            }
        }
    }

    fun memberRegister(email: String, phone: String?, profileId: String?) {
        viewModelScope.launch {
            _memberState.value = Resource.Loading
            val request = MemberRequest(email = email, phone = phone, profileId = profileId)
            _memberState.value = repository.memberRegister(request).let { res ->
                when (res) {
                    is Resource.Success -> {
                        member = res.data
                        repository.memberPlans(memberId = res.data.id, profileId = res.data.profileId)
                    }
                    is Resource.Error -> res
                    Resource.Loading -> Resource.Error("Unexpected state")
                }
            }
        }
    }

    fun trainerLogin(email: String, credential: String) {
        viewModelScope.launch {
            _trainerState.value = Resource.Loading
            when (val result = repository.trainerLogin(TrainerLoginRequest(email, credential))) {
                is Resource.Success -> {
                    trainer = result.data
                    _trainerState.value = repository.trainerClients(result.data.id)
                }
                is Resource.Error -> _trainerState.value = result
                Resource.Loading -> _trainerState.value = Resource.Error("Unexpected state")
            }
        }
    }
}
