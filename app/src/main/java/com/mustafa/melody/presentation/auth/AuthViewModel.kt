package com.mustafa.melody.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.melody.data.remote.SupabaseProvider
import com.mustafa.melody.R
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import android.net.Uri
import com.mustafa.melody.data.remote.dto.ProfileDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val isSignedIn: Boolean = false,
    val isLoading: Boolean = false,
    val errorResId: Int? = null,
    val confirmationSent: Boolean = false,
    val isCloudConfigured: Boolean = false,
    val avatarUrl: String? = null,
    val profileSaved: Boolean = false,
)

@HiltViewModel
class AuthViewModel @Inject constructor(private val provider: SupabaseProvider) : ViewModel() {
    private val mutableState = MutableStateFlow(AuthUiState(isCloudConfigured = provider.isConfigured))
    val state = mutableState.asStateFlow()

    init {
        provider.client?.let { client ->
            viewModelScope.launch {
                client.auth.sessionStatus.collectLatest {
                    val user = client.auth.currentUserOrNull()
                    update {
                        copy(
                            isSignedIn = user != null,
                            displayName = user?.email?.substringBefore('@').orEmpty(),
                            isLoading = false,
                        )
                    }
                    if (user != null) loadProfile(user.id)
                }
            }
        }
    }

    fun email(value: String) = update { copy(email = value, errorResId = null) }
    fun password(value: String) = update { copy(password = value, errorResId = null) }
    fun displayName(value: String) = update { copy(displayName = value, errorResId = null, profileSaved = false) }

    fun signIn() = submit(signUp = false)
    fun signUp() = submit(signUp = true)

    private fun submit(signUp: Boolean) {
        val snapshot = mutableState.value
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(snapshot.email).matches() || snapshot.password.length < 6) {
            update { copy(errorResId = R.string.invalid_account_fields) }
            return
        }
        viewModelScope.launch {
            update { copy(isLoading = true, errorResId = null) }
            val client = provider.client
            if (client == null) {
                update { copy(isLoading = false, isSignedIn = true, displayName = email.substringBefore('@')) }
                return@launch
            }
            runCatching {
                if (signUp) client.auth.signUpWith(Email) { email = snapshot.email; password = snapshot.password }
                else client.auth.signInWith(Email) { email = snapshot.email; password = snapshot.password }
            }.onSuccess {
                val user = client.auth.currentUserOrNull()
                update {
                    copy(
                        isLoading = false,
                        isSignedIn = user != null,
                        confirmationSent = signUp && user == null,
                        displayName = snapshot.email.substringBefore('@'),
                    )
                }
            }.onFailure { update { copy(isLoading = false, errorResId = R.string.authentication_failed) } }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            provider.client?.let { runCatching { it.auth.signOut() } }
            mutableState.value = AuthUiState(isCloudConfigured = provider.isConfigured)
        }
    }

    fun uploadAvatar(context: Context, uri: Uri) {
        viewModelScope.launch {
            val client = provider.client ?: return@launch
            val userId = client.auth.currentUserOrNull()?.id ?: return@launch
            update { copy(isLoading = true, errorResId = null) }
            runCatching {
                val bytes = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                        ?: error("Could not read the selected image")
                }
                val path = "$userId/avatar.jpg"
                client.storage.from("avatars").upload(path, bytes) { upsert = true }
                val publicUrl = client.storage.from("avatars").publicUrl(path)
                client.from("profiles").update({ set("avatar_url", publicUrl) }) {
                    filter { eq("id", userId) }
                }
                publicUrl
            }.onSuccess { url -> update { copy(isLoading = false, avatarUrl = url) } }
                .onFailure { update { copy(isLoading = false, errorResId = R.string.avatar_upload_failed) } }
        }
    }

    fun saveProfile() {
        val name = mutableState.value.displayName.trim()
        if (name.isBlank()) return
        viewModelScope.launch {
            update { copy(isLoading = true, profileSaved = false, errorResId = null) }
            val client = provider.client
            val userId = client?.auth?.currentUserOrNull()?.id
            val result = runCatching {
                if (client != null && userId != null) {
                    client.from("profiles").update({ set("display_name", name) }) {
                        filter { eq("id", userId) }
                    }
                }
            }
            update {
                if (result.isSuccess) copy(isLoading = false, displayName = name, profileSaved = true)
                else copy(isLoading = false, errorResId = R.string.authentication_failed)
            }
        }
    }

    private fun loadProfile(userId: String) {
        viewModelScope.launch {
            runCatching {
                provider.client?.from("profiles")?.select {
                    filter { eq("id", userId) }
                    limit(1)
                }?.decodeSingleOrNull<ProfileDto>()
            }.getOrNull()?.let { profile ->
                update { copy(displayName = profile.displayName, avatarUrl = profile.avatarUrl) }
            }
        }
    }

    private fun update(transform: AuthUiState.() -> AuthUiState) {
        mutableState.value = mutableState.value.transform()
    }
}
