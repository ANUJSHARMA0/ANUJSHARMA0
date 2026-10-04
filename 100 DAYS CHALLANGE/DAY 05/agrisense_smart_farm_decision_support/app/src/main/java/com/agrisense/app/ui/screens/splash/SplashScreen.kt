package com.agrisense.app.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agrisense.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.5f,
        animationSpec = tween(800, easing = FastOutSlowInEasing), label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800), label = "alpha"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(2200)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scale)
                .alpha(alpha)
        ) {
            // Logo placeholder — sprout icon
            Surface(
                modifier = Modifier.size(100.dp),
                shape = MaterialTheme.shapes.large,
                color = Primary.copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🌱", fontSize = 48.sp)
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = "AgriSense",
                style = MaterialTheme.typography.displayMedium,
                color = OnPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "कृषि-सेंस",
                style = MaterialTheme.typography.headlineSmall,
                color = PrimaryFixedDim
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Smart Farm Decision Support",
                style = MaterialTheme.typography.bodyLarge,
                color = OnPrimary.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(48.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = SecondaryContainer,
                strokeWidth = 2.dp
            )
        }
    }
}
