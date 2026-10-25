# Tabletop Motion Alarm — Product Roadmap

## Commercial model — locked for v1

- Google Play paid app.
- Reference price: EUR 4.99.
- Promotional price: EUR 3.99 when useful.
- Full product after Store purchase.
- No FREE/PRO split inside the app.
- No Play Billing entitlement layer.
- No ads.
- No subscription.
- No proprietary account/login.
- Offline/local-first positioning.

Core commercial message:
**one purchase, full utility, no ads, no subscription, no account.**

## Release roadmap

### Step 6A — Reliability
- [x] Foreground-service sticky recovery enters foreground before suspend work.
- [x] Persist calibrated accelerometer baseline.
- [x] Same-boot recovery can restore the previous baseline without a full 1.5 s recalibration.
- [x] Reboot/stale runtime sessions fail closed.
- [x] Critical session state uses synchronous persistence.
- [x] Event history writes use AtomicFile.
- [x] Add recovery and motion-threshold unit tests.

### Step 6B — Lean build
- [x] Remove Google Sign-In and profile persistence.
- [x] Remove Credential Manager / Google ID dependencies.
- [x] Remove material-icons-extended.
- [x] Remove obsolete broad Google R8 keep rules.
- [x] Remove obsolete Kotlin metadata compatibility workaround.
- [x] Explicit non-legacy native library packaging.
- [x] Fix custom touch targets / semantic roles.
- [x] versionCode 4.

### Step 7 — Localization
- [ ] Centralize all user-visible strings.
- [ ] Italian + English first.
- [ ] Spanish, German, French, Portuguese.
- [ ] Localize notifications, onboarding, errors and privacy copy.
- [ ] Prepare localized Play Store listing.

### Step 8 — Premium feature expansion
All features remain part of the paid app; no internal premium tier.
Candidates:
- [ ] Charger Guard.
- [ ] Pocket Guard.
- [ ] Saved protection profiles.
- [ ] More alarm sound options.
- [ ] Optional auto-arm workflows where Android policy/reliability permits.

### Step 9 — Store / conversion
- [ ] Final app icon / feature graphic / screenshots.
- [ ] Privacy-policy public URL + contact.
- [ ] Foreground-service specialUse declaration video.
- [ ] Final Data Safety.
- [ ] ASO title/short description/localized listing.
- [ ] Store listing experiments after enough traffic.
- [ ] Price promotions through Play Console.

### Step 10 — Release QA
- [ ] Pixel / Samsung / Xiaomi-HyperOS physical-device matrix.
- [ ] Screen-off long-duration alarm test.
- [ ] Process-kill recovery test.
- [ ] OEM battery-kill test.
- [ ] False-positive / sensitivity matrix.
- [ ] Vibration / flash / siren matrix.
- [ ] Play-installed build test.
- [ ] Fresh installed-size measurement.
