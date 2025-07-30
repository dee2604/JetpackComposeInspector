package io.github.dee2604.sampleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.dee2604.jetpackcomposeinspector.JetpackComposeInspector
import io.github.dee2604.jetpackcomposeinspector.extensions.CustomPanelRegistrar
import io.github.dee2604.jetpackcomposeinspector.extensions.customPanel
import io.github.dee2604.sampleapp.ui.theme.SampleAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize the inspector (debug builds only)
        JetpackComposeInspector.attach(this)
        
        // Register a custom debug panel
        CustomPanelRegistrar.registerPanel(
            customPanel {
                id("sample_debug")
                title("Sample Debug")
                icon("🔧")
                priority(1)
                content {
                    SampleDebugPanel()
                }
            }
        )
        
        setContent {
            SampleAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SampleAppContent()
                    
                    // Include the inspector overlay
                    JetpackComposeInspector.InspectorOverlayComposable()
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clean up the inspector
        JetpackComposeInspector.detach()
    }
}

@Composable
fun SampleAppContent() {
    var counter by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Jetpack Compose Inspector Sample",
            style = MaterialTheme.typography.h4
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Counter: $counter",
            style = MaterialTheme.typography.h6
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { counter++ }
        ) {
            Text("Increment Counter")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Long press anywhere to open the inspector overlay",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun SampleDebugPanel() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Sample Debug Panel",
            style = MaterialTheme.typography.h6
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "This is a custom debug panel that demonstrates the extensibility of the inspector.",
            style = MaterialTheme.typography.body2
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Debug Information",
                    style = MaterialTheme.typography.subtitle1
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("• App Version: 1.0.0")
                Text("• Build Type: Debug")
                Text("• Inspector Active: Yes")
            }
        }
    }
} 