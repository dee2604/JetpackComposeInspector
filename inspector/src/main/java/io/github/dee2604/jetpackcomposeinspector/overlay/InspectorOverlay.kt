package io.github.dee2604.jetpackcomposeinspector.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun InspectorOverlay() {
    var isVisible by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(InspectorTab.STATE) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Floating activation button
        if (!isVisible) {
            FloatingActionButton(
                onClick = { isVisible = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(24.dp),
                backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.85f),
                contentColor = MaterialTheme.colors.onPrimary
            ) {
                Icon(Icons.Default.Search, contentDescription = "Open Inspector")
            }
        }

        // Main inspector overlay as a dialog
        if (isVisible) {
            Dialog(onDismissRequest = { isVisible = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                ) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(0.95f)
                            .fillMaxHeight(0.85f),
                        elevation = 16.dp,
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Inspector",
                                    style = MaterialTheme.typography.h6,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { isVisible = false }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close Inspector")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            TabRow(
                                selectedTabIndex = currentTab.ordinal,
                                backgroundColor = MaterialTheme.colors.surface,
                                contentColor = MaterialTheme.colors.primary
                            ) {
                                InspectorTab.values().forEachIndexed { index, tab ->
                                    Tab(
                                        selected = currentTab.ordinal == index,
                                        onClick = { currentTab = tab },
                                        text = { Text(tab.title) }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                when (currentTab) {
                                    InspectorTab.STATE -> StateTreeContent()
                                    InspectorTab.NAVIGATION -> NavigationContent()
                                    InspectorTab.CUSTOM -> CustomPanelsContent()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StateTreeContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            text = "State Tree",
            style = MaterialTheme.typography.h6
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Live Compose state inspection coming soon!\n\nWant to contribute? See the README for how to help implement this panel.",
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
private fun NavigationContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            text = "Navigation",
            style = MaterialTheme.typography.h6
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Live navigation stack inspection coming soon!\n\nWant to contribute? See the README for how to help implement this panel.",
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
private fun CustomPanelsContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            text = "Custom Panels",
            style = MaterialTheme.typography.h6
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Register your own debug panels! See the README for how to add custom panels to the inspector.",
            style = MaterialTheme.typography.body2
        )
    }
}

enum class InspectorTab(val title: String) {
    STATE("State"),
    NAVIGATION("Navigation"),
    CUSTOM("Custom")
} 