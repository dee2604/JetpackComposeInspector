package io.github.dee2604.jetpackcomposeinspector

import android.content.Context
import androidx.compose.runtime.Composable
import io.github.dee2604.jetpackcomposeinspector.overlay.InspectorOverlay
import io.github.dee2604.jetpackcomposeinspector.state.StateTreeCollector

/**
 * Main entry point for the Jetpack Compose Inspector.
 * 
 * Usage:
 * ```kotlin
 * // In your Application class or composable root (debug build only)
 * if (BuildConfig.DEBUG) {
 *     JetpackComposeInspector.attach(context)
 * }
 * ```
 */
object JetpackComposeInspector {
    
    @Volatile
    private var isAttached = false
    
    /**
     * Attaches the inspector overlay to the application context.
     * This should only be called in debug builds.
     */
    fun attach(context: Context) {
        if (isAttached) return
        
        synchronized(this) {
            if (isAttached) return
            isAttached = true
        }
        
        // Initialize inspector components
        StateTreeCollector.initialize()
        
        // TODO: Set up global gesture listener for overlay activation
    }
    
    /**
     * Detaches the inspector overlay.
     */
    fun detach() {
        if (!isAttached) return
        
        synchronized(this) {
            if (!isAttached) return
            isAttached = false
        }
        
        // Clean up inspector components
        StateTreeCollector.cleanup()
    }
    
    /**
     * Composable function to include the inspector overlay in your app.
     * This should be called at the root of your Compose UI.
     */
    @Composable
    fun InspectorOverlayComposable() {
        // Always show the overlay in debug builds
        // The debug check is handled at the application level
       InspectorOverlay()
    }
} 