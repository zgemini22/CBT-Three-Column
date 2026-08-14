# Three Column Method

A small, private, offline Android app for practicing David Burns' three-column
technique from *Feeling Good: The New Mood Therapy* — write down an automatic
thought, name the cognitive distortion(s) in it, and answer it with a rational
response. The whole app is themed to look and feel like a physical notebook:
cream paper, ruled lines, serif type, everywhere — not just one screen.

This is an independent, unofficial tool. It is not affiliated with or
endorsed by the book's author or publisher, and it is not a substitute for
professional care.

## Features

- **Thought Records** — three-column entries (automatic thought → distortion →
  rational response) with before/after belief-strength sliders (0–100%) and
  an optional situation note, laid out on ruled notebook paper. All ten of
  Burns' cognitive distortions are available as tappable chips, each with a
  short description. Tapping a record opens a read-only detail page first;
  an edit icon there opens the editable form.
- **English / 简体中文** — every string in the app is localized, and a
  Language section on the About page lets you override the display language
  independent of the device's system setting.
- **Journal** — a single-topic notebook dedicated to *"Why is living in fear
  of opposition and criticism irrational and unnecessary?"* Add a new dated
  page any time a fresh thought about it occurs to you; pages list like a
  running notebook.
- **Notebook-styled throughout** — every screen (including the shell, forms,
  and About page) shares one paper/ink palette and serif typography, not just
  the Journal.
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
  ui/journal/            Single-topic journal list + entry screens, ViewModel
  ui/about/              In-app explanation of the technique
  ui/theme/              App-wide notebook palette, typography, ruled-paper modifier
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
