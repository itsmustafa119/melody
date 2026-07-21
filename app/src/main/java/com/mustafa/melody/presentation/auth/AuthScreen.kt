package com.mustafa.melody.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.mustafa.melody.R
import com.mustafa.melody.core.designsystem.theme.AppDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    state: AuthUiState,
    onEmail: (String) -> Unit,
    onPassword: (String) -> Unit,
    onDisplayName: (String) -> Unit,
    onSaveProfile: () -> Unit,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    onSignOut: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(stringResource(R.string.account)) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } },
        )
    }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(AppDimens.spacingLarge),
            verticalArrangement = Arrangement.spacedBy(AppDimens.spacingMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (state.isSignedIn) {
                Text(stringResource(R.string.signed_in_as, state.displayName))
                OutlinedTextField(
                    value = state.displayName,
                    onValueChange = onDisplayName,
                    label = { Text(stringResource(R.string.display_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (state.profileSaved) Text(stringResource(R.string.profile_saved))
                if (state.isLoading) CircularProgressIndicator()
                else Button(
                    onClick = onSaveProfile,
                    enabled = state.displayName.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(stringResource(R.string.save_profile)) }
                Button(onClick = onSignOut, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.sign_out)) }
            } else {
                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmail,
                    label = { Text(stringResource(R.string.email)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPassword,
                    label = { Text(stringResource(R.string.password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                state.errorResId?.let { Text(stringResource(it), color = androidx.compose.material3.MaterialTheme.colorScheme.error) }
                if (!state.isCloudConfigured) Text(stringResource(R.string.demo_account_hint))
                if (state.confirmationSent) Text(stringResource(R.string.confirmation_sent))
                if (state.isLoading) CircularProgressIndicator()
                else {
                    Button(onClick = onSignIn, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.sign_in)) }
                    OutlinedButton(onClick = onSignUp, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.create_account)) }
                }
            }
        }
    }
}
