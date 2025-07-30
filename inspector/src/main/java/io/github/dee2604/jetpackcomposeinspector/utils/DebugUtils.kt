package io.github.dee2604.jetpackcomposeinspector.utils

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.reflect.KClass

/**
 * Utility functions for debugging Jetpack Compose applications.
 * Provides state inspection, performance monitoring, and debugging helpers.
 */
object DebugUtils {
    
    private const val TAG = "ComposeInspector"
    
    /**
     * Logs debug information with the inspector tag.
     */
    fun log(message: String) {
        Log.d(TAG, message)
    }
    
    /**
     * Logs error information with the inspector tag.
     */
    fun logError(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
    }
    
    /**
     * Checks if the application is running in debug mode.
     */
    fun isDebugMode(context: Context): Boolean {
        return context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0
    }
    
    /**
     * Safely gets a field value from an object using reflection.
     */
    fun <T> getFieldValue(obj: Any, fieldName: String, defaultValue: T): T {
        return try {
            val field = obj.javaClass.getDeclaredField(fieldName)
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            field.get(obj) as? T ?: defaultValue
        } catch (e: Exception) {
            logError("Failed to get field $fieldName from ${obj.javaClass.simpleName}", e)
            defaultValue
        }
    }
    
    /**
     * Safely sets a field value on an object using reflection.
     */
    fun <T> setFieldValue(obj: Any, fieldName: String, value: T): Boolean {
        return try {
            val field = obj.javaClass.getDeclaredField(fieldName)
            field.isAccessible = true
            field.set(obj, value)
            true
        } catch (e: Exception) {
            logError("Failed to set field $fieldName on ${obj.javaClass.simpleName}", e)
            false
        }
    }
    
    /**
     * Gets all declared fields of an object.
     */
    fun getObjectFields(obj: Any): List<Pair<String, Any?>> {
        return try {
            obj.javaClass.declaredFields.map { field ->
                field.isAccessible = true
                field.name to field.get(obj)
            }
        } catch (e: Exception) {
            logError("Failed to get fields from ${obj.javaClass.simpleName}", e)
            emptyList()
        }
    }
    
    /**
     * Extracts state information from a ViewModel.
     */
    fun extractViewModelState(viewModel: ViewModel): Map<String, Any?> {
        val state = mutableMapOf<String, Any?>()
        
        try {
            // Get all declared fields
            val fields = viewModel.javaClass.declaredFields
            
            for (field in fields) {
                field.isAccessible = true
                val value = field.get(viewModel)
                
                when (value) {
                    is StateFlow<*> -> {
                        state[field.name] = "StateFlow(${value.value})"
                    }
                    is MutableStateFlow<*> -> {
                        state[field.name] = "MutableStateFlow(${value.value})"
                    }
                    else -> {
                        state[field.name] = value
                    }
                }
            }
        } catch (e: Exception) {
            logError("Failed to extract state from ViewModel", e)
        }
        
        return state
    }
    
    /**
     * Gets the current ViewModel store from a ViewModelStoreOwner.
     */
    fun getViewModelStore(owner: ViewModelStoreOwner): ViewModelStore? {
        return try {
            val field = owner.javaClass.getDeclaredField("mViewModelStore")
            field.isAccessible = true
            field.get(owner) as? ViewModelStore
        } catch (e: Exception) {
            logError("Failed to get ViewModelStore from $owner", e)
            null
        }
    }
    
    /**
     * Formats an object for display in the inspector.
     */
    fun formatForDisplay(obj: Any?): String {
        return when (obj) {
            null -> "null"
            is String -> "\"$obj\""
            is Number -> obj.toString()
            is Boolean -> obj.toString()
            is Collection<*> -> {
                val items = obj.take(10).joinToString(", ") { formatForDisplay(it) }
                val suffix = if (obj.size > 10) "..." else ""
                "[$items$suffix]"
            }
            is Map<*, *> -> {
                val entries = obj.entries.take(5).joinToString(", ") { (k, v) ->
                    "${formatForDisplay(k)}: ${formatForDisplay(v)}"
                }
                val suffix = if (obj.size > 5) "..." else ""
                "{$entries$suffix}"
            }
            else -> {
                val className = obj.javaClass.simpleName
                val fields = getObjectFields(obj).take(3)
                if (fields.isEmpty()) {
                    className
                } else {
                    val fieldStr = fields.joinToString(", ") { (name, value) ->
                        "$name=${formatForDisplay(value)}"
                    }
                    "$className($fieldStr)"
                }
            }
        }
    }
    
    /**
     * Measures the execution time of a block of code.
     */
    fun <T> measureTime(description: String, block: () -> T): T {
        val startTime = System.currentTimeMillis()
        val result = block()
        val endTime = System.currentTimeMillis()
        log("$description took ${endTime - startTime}ms")
        return result
    }
    
    /**
     * Creates a debug composable that logs recomposition events.
     */
    @Composable
    fun DebugRecomposition(
        key: String,
        content: @Composable () -> Unit
    ) {
        val context = LocalContext.current
        val isDebug = remember { isDebugMode(context) }
        
        if (isDebug) {
            DisposableEffect(key) {
                log("Composable '$key' entered composition")
                onDispose {
                    log("Composable '$key' left composition")
                }
            }
        }
        
        content()
    }
    
    /**
     * Tracks recomposition count for a composable.
     */
    @Composable
    fun TrackRecompositions(
        key: String,
        content: @Composable () -> Unit
    ) {
        val recompositionCount = remember { mutableStateOf(0) }
        val context = LocalContext.current
        val isDebug = remember { isDebugMode(context) }
        
        DisposableEffect(key) {
            recompositionCount.value++
            if (isDebug) {
                log("Composable '$key' recomposed ${recompositionCount.value} times")
            }
            onDispose { }
        }
        
        content()
    }
    
    /**
     * Validates that a composable is only called in debug builds.
     */
    @Composable
    fun DebugOnly(
        content: @Composable () -> Unit
    ) {
        val context = LocalContext.current
        val isDebug = remember { isDebugMode(context) }
        
        if (isDebug) {
            content()
        }
    }
    
    /**
     * Gets memory usage information for debugging.
     */
    fun getMemoryInfo(): String {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemory = totalMemory - freeMemory
        val maxMemory = runtime.maxMemory()
        
        return """
            Memory Usage:
            - Used: ${formatBytes(usedMemory)}
            - Free: ${formatBytes(freeMemory)}
            - Total: ${formatBytes(totalMemory)}
            - Max: ${formatBytes(maxMemory)}
        """.trimIndent()
    }
    
    private fun formatBytes(bytes: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB")
        var size = bytes.toDouble()
        var unitIndex = 0
        
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }
        
        return "%.1f %s".format(size, units[unitIndex])
    }
} 