package io.github.dee2604.jetpackcomposeinspector.extensions

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Provides API to register custom debug panels/tabs for project-specific needs.
 * Allows developers to extend the inspector with their own debugging panels.
 */
object CustomPanelRegistrar {
    
    private val _customPanels = MutableStateFlow<List<CustomPanel>>(emptyList())
    val customPanels: StateFlow<List<CustomPanel>> = _customPanels
    
    /**
     * Register a custom debug panel.
     * 
     * @param panel The custom panel to register
     */
    fun registerPanel(panel: CustomPanel) {
        val currentPanels = _customPanels.value.toMutableList()
        currentPanels.add(panel)
        _customPanels.value = currentPanels
    }
    
    /**
     * Unregister a custom debug panel.
     * 
     * @param panelId The ID of the panel to unregister
     */
    fun unregisterPanel(panelId: String) {
        val currentPanels = _customPanels.value.toMutableList()
        currentPanels.removeAll { it.id == panelId }
        _customPanels.value = currentPanels
    }
    
    /**
     * Get all registered custom panels.
     */
    fun getRegisteredPanels(): List<CustomPanel> {
        return _customPanels.value
    }
    
    /**
     * Clear all registered custom panels.
     */
    fun clearAllPanels() {
        _customPanels.value = emptyList()
    }
}

/**
 * Represents a custom debug panel that can be registered with the inspector.
 */
data class CustomPanel(
    val id: String,
    val title: String,
    val icon: String? = null,
    val content: @Composable () -> Unit,
    val priority: Int = 0
) {
    init {
        require(id.isNotBlank()) { "Panel ID cannot be blank" }
        require(title.isNotBlank()) { "Panel title cannot be blank" }
    }
}

/**
 * Builder class for creating custom panels with a fluent API.
 */
class CustomPanelBuilder {
    private var id: String = ""
    private var title: String = ""
    private var icon: String? = null
    private var content: (@Composable () -> Unit)? = null
    private var priority: Int = 0
    
    fun id(id: String): CustomPanelBuilder {
        this.id = id
        return this
    }
    
    fun title(title: String): CustomPanelBuilder {
        this.title = title
        return this
    }
    
    fun icon(icon: String): CustomPanelBuilder {
        this.icon = icon
        return this
    }
    
    fun content(content: @Composable () -> Unit): CustomPanelBuilder {
        this.content = content
        return this
    }
    
    fun priority(priority: Int): CustomPanelBuilder {
        this.priority = priority
        return this
    }
    
    fun build(): CustomPanel {
        require(id.isNotBlank()) { "Panel ID is required" }
        require(title.isNotBlank()) { "Panel title is required" }
        require(content != null) { "Panel content is required" }
        
        return CustomPanel(
            id = id,
            title = title,
            icon = icon,
            content = content!!,
            priority = priority
        )
    }
}

/**
 * Extension function to create a custom panel using the builder pattern.
 */
fun customPanel(init: CustomPanelBuilder.() -> Unit): CustomPanel {
    return CustomPanelBuilder().apply(init).build()
}

/**
 * Example usage:
 * ```kotlin
 * CustomPanelRegistrar.registerPanel(
 *     customPanel {
 *         id("network_debug")
 *         title("Network Debug")
 *         icon("🌐")
 *         priority(1)
 *         content {
 *             // Your custom debug UI here
 *             Text("Network Debug Panel")
 *         }
 *     }
 * )
 * ```
 */ 