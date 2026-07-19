# Implementation Plan - English and Persian Localization

Implement the localization foundation for English and Persian, ensuring automatic RTL/LTR support and removal of hardcoded strings.

## User Review Required

> [!IMPORTANT]
> The application will use English as the default language (`res/values/strings.xml`) and Persian as the localized language (`res/values-fa/strings.xml`).
> Android 13+ per-app language settings will be enabled via `locales_config.xml`.

## Proposed Changes

### Resources Configuration

#### [MODIFY] [strings.xml](file:///D:/projects/melody/app/src/main/res/values/strings.xml)
- Add baseline English string resources (Home, Search, Settings, etc.).
- Update `app_name` to "Melody".

#### [NEW] [strings.xml](file:///D:/projects/melody/app/src/main/res/values-fa/strings.xml)
- Create Persian translations for all keys in the default `strings.xml`.

#### [NEW] [locales_config.xml](file:///D:/projects/melody/app/src/main/res/xml/locales_config.xml)
- Define supported locales: `en` and `fa`.

### Manifest and App Configuration

#### [MODIFY] [AndroidManifest.xml](file:///D:/projects/melody/app/src/main/AndroidManifest.xml)
- Add `android:localeConfig="@xml/locales_config"` to the `<application>` tag.
- Ensure `android:supportsRtl="true"` is present.

### UI and Source Code

#### [MODIFY] [MainActivity.kt](file:///D:/projects/melody/app/src/main/java/com/mustafa/melody/MainActivity.kt)
- Replace hardcoded "Hello $name!" with a string resource.
- Update `Greeting` preview to use resources if applicable, or keep technical strings like "Android" if appropriate (though user-facing greetings should be localized).

## Verification Plan

### Automated Tests
- `.\gradlew.bat :app:assembleDebug`: Verify compilation.
- `.\gradlew.bat :app:testDebugUnitTest`: Verify unit tests.
- `.\gradlew.bat :app:lintDebug`: Check for missing translations or lint issues.

### Manual Verification
- Verify layout direction switches between LTR (English) and RTL (Persian) based on system/app locale.
- Confirm string resources are correctly loaded in both languages.
