# Tabletop Motion Alarm

Native Android utility (Kotlin + Jetpack Compose) that arms a motion alarm while the phone is resting on a table.

- Package: `com.riccardopinato.tabletopalarm`
- Current version: `1.0.0` (`versionCode 4` for the first persistent INTERNAL signing lane)
- Android: minSdk 26 / targetSdk 37
- Premium paid app, local-first, no ads, no subscription, no account, no backend.

## Signing

The repository never stores private signing material. GitHub Actions uses the four repository secrets:
`ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD`.

## UI

Custom utility design system inspired by cleaner-style Android interfaces; Material 3 remains an implementation layer, not the visual identity.


## Commercial model

- Distribution: paid app on Google Play
- Reference price: EUR 4.99
- Periodic promotional price: EUR 3.99
- All features are included after purchase
- No in-app feature split and no Play Billing paywall inside the app
