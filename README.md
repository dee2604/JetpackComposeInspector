# Jetpack Compose Inspector

JetpackComposeInspector is a powerful, extensible in-app debug overlay for Jetpack Compose applications, inspired by tools like React DevTools. It helps you debug, inspect, and visualize your app's state and navigation in real time—right inside your running app.

## Features

- **In-App Debug Overlay:** Floating debug button (activator) always available in debug builds. When activated, a full inspector panel overlays your app for interactive debugging.
- **Tabbed Inspector Panel:**
  - **State:** (Planned) Visualize the Compose state tree, including state holders and ViewModels.
  - **Navigation:** (Planned) Visualize the current navigation stack and destinations.
  - **Custom:** Extensible area for developer-registered panels (feature flags, network logs, etc).
- **Architecture & Extensibility:**
  - Modular and easy to add new panels/tabs or custom inspection tools.
  - Overlay and inspection code are included only in debug builds—safe for production.
  - Developers can plug in their own debug tools without modifying the core library.
- **Sample App Demo:** Demonstrates integration and usage in a real Jetpack Compose app.

## Project Structure

```
jetpackcomposeinspector/
├── inspector/         # Library/core module (all reusable inspector logic)
├── sample-app/        # Demo/sample usage app
├── README.md
└── build.gradle(.kts)
```

## Usage Example

Add the inspector to your app (debug builds only):

```kotlin
// In your Application class or main activity
if (BuildConfig.DEBUG) {
    JetpackComposeInspector.attach(this)
}

setContent {
    // ... your app theme and content ...
    JetpackComposeInspector.InspectorOverlayComposable()
}

// Optionally, detach in onDestroy
override fun onDestroy() {
    super.onDestroy()
    JetpackComposeInspector.detach()
}
```

### Registering a Custom Panel

```kotlin
CustomPanelRegistrar.registerPanel(
    customPanel {
        id("sample_debug")
        title("Sample Debug")
        icon("🔧")
        priority(1)
        content {
            // Your custom panel content
        }
    }
)
```

## Sample App
See `sample-app/` for a complete integration example.

## Current Status / TODOs
- **State Tree** and **Navigation** panels are currently placeholders.
- **Next Steps:** Implement live state and navigation inspection using Compose and Navigation APIs.
- **Extensibility:** Custom panels are fully supported—see the sample app for usage.

## Why JetpackComposeInspector?
- **Accelerate debugging and state inspection** for Compose apps.
- **Reduce reliance on logcat/manual checks**—make debugging visual, interactive, and modern.
- **Inspire a “React DevTools for Compose”**—an essential productivity tool for Compose developers and teams. 