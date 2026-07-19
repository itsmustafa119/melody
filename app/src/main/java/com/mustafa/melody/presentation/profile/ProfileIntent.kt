package com.mustafa.melody.presentation.profile

sealed interface ProfileIntent {
    data object ChangeAvatarClicked : ProfileIntent
    data object UpgradeClicked : ProfileIntent
    data object RenewSubscriptionClicked : ProfileIntent
    data object SettingsClicked : ProfileIntent
    data object NotificationsClicked : ProfileIntent
    data object Retry : ProfileIntent
}
