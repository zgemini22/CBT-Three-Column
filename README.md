# Three Column CBT

A small, private, offline Android app for practicing David Burns' three-column
technique from *Feeling Good: The New Mood Therapy* — write down an automatic
thought, name the cognitive distortion(s) in it, and answer it with a rational
response. It also includes a diary-style Journal for free-form reflection —
on topics the book raises (like the pull of seeking everyone's approval), on
hobby ideas you don't need anyone's sign-off to try, or on anything else.

This is an independent, unofficial tool. It is not affiliated with or
endorsed by the book's author or publisher, and it is not a substitute for
professional care.

## Features

- **Thought Records** — three-column entries (automatic thought → distortion →
  rational response) with before/after belief-strength sliders (0–100%) and
  an optional situation note. All ten of Burns' cognitive distortions are
  available as tappable chips, each with a short description.
- **Journal** — a diary-styled free-write space (paper background, ruled
  lines, serif type). Start from a suggested reflection prompt or write
  blank; entries are dated and listed like diary pages.
- **Fully offline** — everything is stored locally on-device with Room
  (SQLite); nothing is sent anywhere.

## Tech stack

- Kotlin, Jetpack Compose (Material 3), Navigation Compose
- Room for local persistence
- MVVM: one `ViewModel` per feature, backed by a small repository over a Room DAO

## Project layout

```
app/src/main/java/com/threecolumn/cbt/
  data/                  Room entities, DAOs, database, repositories
  ui/thoughts/           Thought record list + edit screens, ViewModel
  ui/journal/            Diary-style journal list + entry screens, ViewModel
  ui/about/              In-app explanation of the technique
  ui/theme/              Material 3 theme
  ui/CbtNavHost.kt       Bottom-nav navigation graph
  MainActivity.kt
  CbtApplication.kt      Wires repositories to the Room database
```

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
