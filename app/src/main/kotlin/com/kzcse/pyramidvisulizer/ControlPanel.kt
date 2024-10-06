package com.kzcse.pyramidvisulizer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ControlPanel(
    onRotateX: () -> Unit,
    onRotateY: () -> Unit,
    onRotateZ: () -> Unit,
    onTranslateChange: (Float) -> Unit,
    onScaleChange: (Float) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }  // Toggle for showing/hiding controls

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)  // Less padding for compactness
    ) {
        // Header with toggle button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },  // Toggle on click
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Controls", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Toggle Controls"
            )
        }

        if (expanded) {
            RotationControls(
                onRotateX = onRotateX,
                onRotateY = onRotateY,
                onRotateZ = onRotateZ
            )

            Spacer(modifier = Modifier.height(8.dp))

            TranslationAndScalingSliders(
                onTranslateChange = onTranslateChange,
                onScaleChange = onScaleChange
            )

        }


    }
}
@Composable
fun TranslationAndScalingSliders(
    onTranslateChange: (Float) -> Unit,
    onScaleChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Translation Slider
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "Translation", modifier = Modifier.padding(bottom = 4.dp))
            var translationValue by remember { mutableFloatStateOf(0f) }
            Slider(
                value = translationValue,
                onValueChange = {
                    translationValue = it
                    onTranslateChange(it)
                },
                valueRange = -1f..1f,
                modifier = Modifier.width(120.dp)
            )
        }

        // Scaling Slider
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "Scaling", modifier = Modifier.padding(bottom = 4.dp))
            var scaleValue by remember { mutableFloatStateOf(1f) }
            Slider(
                value = scaleValue,
                onValueChange = {
                    scaleValue = it
                    onScaleChange(it)
                },
                valueRange = 0.5f..2f,
                modifier = Modifier.width(120.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RotationControls(
    onRotateX: () -> Unit,
    onRotateY: () -> Unit,
    onRotateZ: () -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        IconButtonWithText(
            icon = Icons.AutoMirrored.Filled.RotateLeft,
            text = "Rotate X",
            onClick = onRotateX
        )
        IconButtonWithText(
            icon = Icons.AutoMirrored.Filled.RotateRight,
            text = "Rotate Y",
            onClick = onRotateY
        )
        IconButtonWithText(
            icon = Icons.Default.Rotate90DegreesCw,
            text = "Rotate Z",
            onClick = onRotateZ
        )
    }
}

@Composable
fun IconButtonWithText(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClick) {
            Icon(imageVector = icon, contentDescription = text)
        }
        Text(text = text)
    }
}
