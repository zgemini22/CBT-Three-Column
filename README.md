# Three Column Method

A small, private, offline Android app for practicing David Burns' three-column
technique from *Feeling Good: The New Mood Therapy* — write down an automatic
thought, name the cognitive distortion(s) in it, and answer it with a rational
response. The whole app is themed to look and feel like a physical notebook:
cream paper, a margin rule, serif type, everywhere — not just one screen.

This is an independent, unofficial tool. It is not affiliated with or
endorsed by the book's author or publisher, and it is not a substitute for
professional care.

**Author:** Shengxing Zhang
**License:** https://ztimelightspacestar.com/psychology/cbt/three-column-notebook/license/

## Features

- **Thought Records** — three-column entries (automatic thought → distortion →
  rational response) with before/after belief-strength sliders (0–100%) and
  an optional situation note. Burns' cognitive distortions are available as
  tappable chips, each with a short description; "Jumping to Conclusions" is
  split into its two named forms (Mind Reading and Fortune Telling) so they
  can be chosen separately. Tapping a record opens a read-only detail page
  first (distortions shown by name only, no descriptions); an edit icon
  there opens the editable form. A share icon sends a formatted text version
  of the record to any app via the system share sheet.
- **Responsive layout** — on a tablet or a landscape phone wide enough for
  it, the detail and edit screens show all three sections as real
  side-by-side columns. On a narrower phone the same three sections become
  independent pages you swipe between, or jump to directly by tapping their
  title in the tab row above them. Situation, the section titles, and the
  before/after belief numbers are tucked behind a small ⓘ icon in the top
  bar (next to share/delete/edit, or save) instead of taking up space by
  default.
- **Search** — a search field above each list filters live as you type:
  thought records match on situation, automatic thought, rational response,
  or distortion name; journal entries match on their text. Both are
  case-insensitive substring matches.
- **Grouped by recency** — the thought record list is bucketed into Today /
  Yesterday / This Week / This Month / older-by-month, newest first within
  each group.
- **Optional app lock** — turn on biometric/device-credential unlock
  (fingerprint, face, or PIN/pattern via `BiometricPrompt`) from About →
  Privacy. While locked, the app also hides its content from the
  recents/app-switcher thumbnail and blocks screenshots.
- **Journal** — a single-topic notebook dedicated to *"Why is living in fear
  of opposition and criticism irrational and unnecessary?"* Add a new dated
  page any time a fresh thought about it occurs to you; pages list like a
  running notebook. Long-press-and-drag the handle on any page to manually
  reorder the list. A share icon on each page sends its text to any app via
  the system share sheet.
- **English / 简体中文** — every string in the app is localized, and a
  Language section on the About page lets you override the display language
  independent of the device's system setting.
- **Theme** — light, dark, or follows the system setting, chosen from the
  About page.
- **Export / import / batch add** — export all data to a `.json` file, or
  import one to restore it or add many records at once. The About page
  shows the exact expected format before you pick a file, so you can
  hand-author a batch import yourself.
- **Notebook-styled throughout** — every screen shares one paper/ink palette
  and serif typography, with a single vertical margin rule as the notebook
  accent (no horizontal ruling — at variable text sizes those can't stay
  aligned to real line baselines and end up cutting through words instead of
  sitting under them).
- **Fully offline** — everything is stored locally on-device with Room
  (SQLite); nothing is sent anywhere except when you explicitly export/import
  a file yourself. Android's automatic cloud backup is disabled
  (`allowBackup="false"`), so your data is never copied to Google's backup
  service either.

## Tech stack

- Kotlin, Jetpack Compose (Material 3), Navigation Compose
- Room for local persistence
- MVVM: one `ViewModel` per feature, backed by a small repository over a Room DAO
- AndroidX per-app language + day/night APIs (`AppCompatDelegate`) for the
  in-app language/theme switchers
- AndroidX Biometric (`BiometricPrompt`) for the optional app lock

## Project layout

```
app/src/main/java/com/threecolumn/cbt/
  data/                  Room entities, DAOs, database, repositories, JSON import/export,
                          PrivacyPreferences (app-lock setting)
  ui/thoughts/           Thought record list (search + recency grouping), responsive
                          detail/edit screens (columns on wide screens, swipeable pages
                          on phones), ViewModel
  ui/journal/            Single-topic journal list (search) + entry screens, ViewModel
  ui/about/              About page: technique/journal blurbs, theme + language pickers,
                          data export/import, author/license
  ui/privacy/            Biometric app-lock toggle (About page section) and the lock screen
                          shown before content when it's enabled
  ui/components/         Shared widgets: SearchField, PageTabRow (the tappable page-title
                          tab row used by the phone layout)
  ui/theme/              App-wide notebook palette (light + dark), typography, margin-rule modifier
  ui/CbtNavHost.kt       Bottom-nav navigation graph
  MainActivity.kt        Also gates all content behind the lock screen when app lock is on
  CbtApplication.kt      Wires repositories to the Room database
```

## Data format for import / batch add

Import a `.json` file shaped like this (see the in-app "Import" dialog for
the authoritative, always-current version and the list of valid
`distortions` codes):

```json
{
  "thoughtRecords": [
    {
      "situation": "Optional context",
      "automaticThought": "The upsetting thought",
      "distortions": ["ALL_OR_NOTHING", "LABELING"],
      "rationalResponse": "A fairer response",
      "beliefBefore": 80,
      "beliefAfter": 30
    }
  ],
  "journalEntries": [
    { "body": "Free-form text for a page" }
  ]
}
```

`createdAt` (milliseconds since epoch) is optional on every entry and
defaults to the import time if omitted.

## Building

Requires Android Studio (Koala or newer) or the command line with an Android
SDK installed (`compileSdk 35`, `minSdk 26`, `targetSdk 34`).

```
./gradlew assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/`. Install it on a
connected device/emulator with `./gradlew installDebug`, or open the project
in Android Studio and click Run.

### Release builds / signing

`./gradlew assembleRelease` produces an **unsigned** APK unless four
environment variables are set, in which case it's signed automatically:

```
RELEASE_KEYSTORE_PATH=/path/to/your.keystore
RELEASE_KEYSTORE_PASSWORD=...
RELEASE_KEY_ALIAS=...
RELEASE_KEY_PASSWORD=...
./gradlew assembleRelease
```

Without them, `assembleRelease` still succeeds and just skips the signing
config — nothing else about the build changes.

### CI

`.github/workflows/build-apk.yml` builds both the debug and release APKs on
every push to this branch and attaches them to a GitHub Release (tagged
`apk-build-N`), since GitHub Actions artifacts aren't reachable from every
environment. It signs the release build the same way, from repo secrets
named `RELEASE_KEYSTORE_BASE64` (the keystore file, base64-encoded),
`RELEASE_KEYSTORE_PASSWORD`, `RELEASE_KEY_ALIAS`, and `RELEASE_KEY_PASSWORD`.
