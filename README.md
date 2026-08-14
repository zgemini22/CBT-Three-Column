# Three Column CBT

A small, private, offline Android app for practicing David Burns' three-column
technique from *Feeling Good: The New Mood Therapy* — write down an automatic
thought, name the cognitive distortion(s) in it, and answer it with a rational
response. It also includes a "Hobbies" list for the book's approval-seeking
chapter: a running list of activities you'd like to try, added the moment the
idea occurs to you, independent of anyone else's approval.

This is an independent, unofficial tool. It is not affiliated with or
endorsed by the book's author or publisher, and it is not a substitute for
professional care.

## Features

- **Thought Records** — three-column entries (automatic thought → distortion →
  rational response) with before/after belief-strength sliders (0–100%) and
  an optional situation note. All ten of Burns' cognitive distortions are
  available as tappable chips, each with a short description.
- **Hobbies** — a simple, always-open idea list: add an activity, check it off
  once tried, delete it if it no longer interests you.
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
  ui/hobbies/            Hobby idea list screen, ViewModel
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
