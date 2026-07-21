package com.mustafa.melody.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.mustafa.melody.R
import com.mustafa.melody.core.designsystem.theme.AppDimens
import com.mustafa.melody.domain.model.AppFontScale
import com.mustafa.melody.domain.model.AppLanguage
import com.mustafa.melody.domain.model.ThemeMode
import com.mustafa.melody.presentation.app.AppIntent
import com.mustafa.melody.presentation.app.AppUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: AppUiState,
    onIntent: (AppIntent) -> Unit,
    onBackClick: () -> Unit,
    isSignedIn: Boolean,
    onOpenAccount: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            item {
                SettingsSectionTitle(stringResource(R.string.theme))
                SettingsChoice(
                    title = stringResource(R.string.system_theme),
                    selected = uiState.themeMode == ThemeMode.SYSTEM,
                    onClick = { onIntent(AppIntent.ChangeTheme(ThemeMode.SYSTEM)) },
                )
                SettingsChoice(
                    title = stringResource(R.string.light_theme),
                    selected = uiState.themeMode == ThemeMode.LIGHT,
                    onClick = { onIntent(AppIntent.ChangeTheme(ThemeMode.LIGHT)) },
                )
                SettingsChoice(
                    title = stringResource(R.string.dark_theme),
                    selected = uiState.themeMode == ThemeMode.DARK,
                    onClick = { onIntent(AppIntent.ChangeTheme(ThemeMode.DARK)) },
                )
                HorizontalDivider()
            }

            item {
                SettingsSectionTitle(stringResource(R.string.language))
                SettingsChoice(
                    title = stringResource(R.string.system_language),
                    selected = uiState.language == AppLanguage.SYSTEM,
                    onClick = { onIntent(AppIntent.ChangeLanguage(AppLanguage.SYSTEM)) },
                )
                SettingsChoice(
                    title = stringResource(R.string.english),
                    selected = uiState.language == AppLanguage.ENGLISH,
                    onClick = { onIntent(AppIntent.ChangeLanguage(AppLanguage.ENGLISH)) },
                )
                SettingsChoice(
                    title = stringResource(R.string.persian),
                    selected = uiState.language == AppLanguage.PERSIAN,
                    onClick = { onIntent(AppIntent.ChangeLanguage(AppLanguage.PERSIAN)) },
                )
                HorizontalDivider()
            }

            item {
                SettingsSectionTitle(stringResource(R.string.font_size))
                AppFontScale.entries.forEach { fontScale ->
                    SettingsChoice(
                        title = stringResource(
                            when (fontScale) {
                                AppFontScale.SMALL -> R.string.font_small
                                AppFontScale.DEFAULT -> R.string.font_default
                                AppFontScale.LARGE -> R.string.font_large
                                AppFontScale.EXTRA_LARGE -> R.string.font_extra_large
                            }
                        ),
                        selected = uiState.fontScale == fontScale,
                        onClick = { onIntent(AppIntent.ChangeFontScale(fontScale)) },
                    )
                }
                HorizontalDivider()
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onIntent(AppIntent.ChangePremiumStatus(!uiState.isPremium))
                        }
                        .padding(AppDimens.spacingMedium),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.premium_status),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = stringResource(R.string.premium_demo_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = uiState.isPremium,
                        onCheckedChange = {
                            onIntent(AppIntent.ChangePremiumStatus(it))
                        },
                    )
                }
            }

            item {
                HorizontalDivider()
                SettingsSectionTitle(stringResource(R.string.account))
                if (isSignedIn) {
                    OutlinedButton(
                        onClick = onSignOut,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = AppDimens.spacingMedium),
                    ) { Text(stringResource(R.string.sign_out)) }
                } else {
                    Button(
                        onClick = onOpenAccount,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = AppDimens.spacingMedium),
                    ) { Text(stringResource(R.string.sign_in)) }
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(
            horizontal = AppDimens.spacingMedium,
            vertical = AppDimens.spacingSmall,
        ),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun SettingsChoice(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = AppDimens.spacingMedium,
                vertical = AppDimens.spacingSmall,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(modifier = Modifier.width(AppDimens.spacingSmall))
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
    }
}
