package io.github.dee2604.jetpackcomposeinspector.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Visualizes navigation state and provides navigation debugging capabilities.
 * Hooks into NavController state to display destinations as a tree/list.
 */
object NavigationVisualizer {
    
    private var isInitialized = false
    private val _navigationData = MutableStateFlow<NavigationData?>(null)
    val navigationData: StateFlow<NavigationData?> = _navigationData
    
    private val registeredNavControllers = mutableSetOf<NavController>()
    
    /**
     * Initialize the navigation visualizer.
     */
    fun initialize() {
        if (isInitialized) return
        isInitialized = true
    }
    
    /**
     * Clean up resources.
     */
    fun cleanup() {
        if (!isInitialized) return
        isInitialized = false
        registeredNavControllers.clear()
        _navigationData.value = null
    }
    
    /**
     * Register a NavController for visualization.
     */
    fun registerNavController(navController: NavController) {
        if (!isInitialized) return
        
        if (registeredNavControllers.add(navController)) {
            // TODO: Set up navigation state observation
            updateNavigationData()
        }
    }
    
    /**
     * Unregister a NavController from visualization.
     */
    fun unregisterNavController(navController: NavController) {
        if (registeredNavControllers.remove(navController)) {
            updateNavigationData()
        }
    }
    
    /**
     * Update the navigation data based on current NavControllers.
     */
    private fun updateNavigationData() {
        if (!isInitialized) return
        
        val destinations = mutableListOf<DestinationInfo>()
        val backStack = mutableListOf<BackStackEntryInfo>()
        
        registeredNavControllers.forEach { navController ->
            // Collect current destinations
            navController.currentBackStackEntry?.let { entry ->
                backStack.add(
                    BackStackEntryInfo(
                        route = entry.destination.route ?: "unknown",
                        arguments = entry.arguments?.keySet()?.toList() ?: emptyList()
                    )
                )
            }
            
            // Collect all available destinations
            collectDestinations(navController.graph, destinations)
        }
        
        _navigationData.value = NavigationData(
            destinations = destinations,
            backStack = backStack,
            currentRoute = registeredNavControllers.firstOrNull()?.currentBackStackEntry?.destination?.route
        )
    }
    
    /**
     * Recursively collect all destinations from a NavGraph.
     */
    private fun collectDestinations(graph: NavGraph, destinations: MutableList<DestinationInfo>) {
        graph.forEach { destination ->
            destinations.add(
                DestinationInfo(
                    route = destination.route ?: "unknown",
                    label = destination.label?.toString() ?: destination.route ?: "unknown",
                    arguments = destination.arguments?.keys?.toList() ?: emptyList()
                )
            )
            
            // Recursively process nested graphs
            if (destination is NavGraph) {
                collectDestinations(destination, destinations)
            }
        }
    }
    
    /**
     * Get the current navigation state.
     */
    fun getCurrentNavigationState(): NavigationData? {
        return _navigationData.value
    }
}

/**
 * Data class representing navigation information.
 */
data class NavigationData(
    val destinations: List<DestinationInfo>,
    val backStack: List<BackStackEntryInfo>,
    val currentRoute: String?
)

/**
 * Information about a navigation destination.
 */
data class DestinationInfo(
    val route: String,
    val label: String,
    val arguments: List<String>
)

/**
 * Information about a back stack entry.
 */
data class BackStackEntryInfo(
    val route: String,
    val arguments: List<String>
) 