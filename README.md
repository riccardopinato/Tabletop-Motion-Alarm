# Tabletop Motion Alarm

Native Android utility (Kotlin + Jetpack Compose) that arms a motion alarm while the phone is resting on a table.

- Package: `com.riccardopinato.tabletopalarm`
- Current version: `1.0.0` (`versionCode 3` for the first persistent INTERNAL signing lane)
- Android: minSdk 26 / targetSdk 37
- Local-first, no ads, no backend.

## Signing

The repository never stores private signing material. GitHub Actions uses the four repository secrets:
`ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD`.

## UI

Custom utility design system inspired by cleaner-style Android interfaces; Material 3 remains an implementation layer, not the visual identity.
