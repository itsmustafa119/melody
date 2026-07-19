package com.mustafa.melody.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mustafa.melody.R
import com.mustafa.melody.core.designsystem.component.ErrorState
import com.mustafa.melody.core.designsystem.component.MelodyTopAppBar
import com.mustafa.melody.core.designsystem.component.PremiumBadge
import com.mustafa.melody.core.designsystem.component.ProfileAvatar
import com.mustafa.melody.core.designsystem.theme.AppDimens
import com.mustafa.melody.core.designsystem.theme.MelodyTheme

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onIntent: (ProfileIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MelodyTopAppBar(
                onProfileClick = {}, // Already on profile
                onNotificationsClick = { onIntent(ProfileIntent.NotificationsClicked) },
                onSettingsClick = { onIntent(ProfileIntent.SettingsClicked) }
            )
        }
    ) { innerPadding ->
        if (uiState.errorMessageResId != null) {
            ErrorState(
                title = stringResource(R.string.something_went_wrong),
                description = stringResource(uiState.errorMessageResId),
                onRetry = { onIntent(ProfileIntent.Retry) },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(AppDimens.spacingLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileAvatar(
                    displayName = uiState.displayName,
                    modifier = Modifier.size(120.dp), // profileImageSizeLarge
                    onClick = { onIntent(ProfileIntent.ChangeAvatarClicked) }
                )

                Spacer(modifier = Modifier.height(AppDimens.spacingMedium))

                Text(
                    text = uiState.displayName.ifEmpty { stringResource(R.string.guest_user) },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(AppDimens.spacingExtraSmall))

                if (uiState.isPremium) {
                    PremiumBadge()
                } else {
                    Text(
                        text = stringResource(R.string.normal_account),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(AppDimens.spacingExtraLarge))

                if (uiState.isPremium) {
                    OutlinedButton(
                        onClick = { onIntent(ProfileIntent.RenewSubscriptionClicked) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.renew_subscription))
                    }
                } else {
                    Button(
                        onClick = { onIntent(ProfileIntent.UpgradeClicked) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.upgrade_to_premium))
                    }
                }

                Spacer(modifier = Modifier.height(AppDimens.sectionSpacing))

                HorizontalDivider(thickness = AppDimens.dividerThickness)

                Surface(
                    onClick = { onIntent(ProfileIntent.SettingsClicked) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier
                            .padding(vertical = AppDimens.spacingMedium),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Settings, contentDescription = null)
                            Spacer(modifier = Modifier.width(AppDimens.spacingMedium))
                            Text(text = stringResource(R.string.account_settings))
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MelodyTheme {
        ProfileScreen(
            uiState = ProfileUiState(displayName = "Mustafa"),
            onIntent = {}
        )
    }
}
