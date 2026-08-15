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
**License:** https://52.77.216.225/psychology/cbt/three-column-notebook/license/

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
- **Journal** — a single-topic notebook dedicated to *"Why is living in fear
  of opposition and criticism irrational and unnecessary?"* Add a new dated
  page any time a fresh thought about it occurs to you; pages list like a
  running notebook. Pin any page to keep it at the top regardless of sort
  order, and toggle Newest first / Oldest first from the list. A share icon
  on each page sends its text to any app via the system share sheet.
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
  a file yourself.

## Tech stack

- Kotlin, Jetpack Compose (Material 3), Navigation Compose
- Room for local persistence
- MVVM: one `ViewModel` per feature, backed by a small repository over a Room DAO
- AndroidX per-app language + day/night APIs (`AppCompatDelegate`) for the
  in-app language/theme switchers

## Project layout

```
app/src/main/java/com/threecolumn/cbt/
  data/                  Room entities, DAOs, database, repositories, JSON import/export
  ui/thoughts/           Thought record list, read-only detail, and edit screens, ViewModel
  ui/journal/            Single-topic journal list + entry screens, ViewModel
  ui/about/              About page: technique/journal blurbs, theme + language pickers,
                          data export/import, author/license
  ui/theme/              App-wide notebook palette (light + dark), typography, margin-rule modifier
  ui/CbtNavHost.kt       Bottom-nav navigation graph
  MainActivity.kt
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
    { "body": "Free-form text for a page", "pinned": false }
  ]
}
```

`createdAt` (milliseconds since epoch) is optional on every entry and
defaults to the import time if omitted. `pinned` on journal entries is
optional and defaults to `false`.

## Building

Requires Android Studio (Koala or newer) or the command line with an Android
SDK installed (`compileSdk 34`, `minSdk 26`).

```
./gradlew assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/`. Install it on a
connected device/emulator with `./gradlew installDebug`, or open the project
in Android Studio and click Run.

> Note: this project was scaffolded in an environment without the Android
> SDK installed, so the Gradle build itself could not be executed here.
> Open it in Android Studio (which will prompt to install any missing SDK
> components) to build and run it.
