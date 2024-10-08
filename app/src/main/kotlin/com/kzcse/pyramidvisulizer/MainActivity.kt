package com.kzcse.pyramidvisulizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.kzcse.pyramidvisulizer.opengl.OpenGLHelper
import com.kzcse.pyramidvisulizer.ui.theme.PyramidVisulizerTheme

class MainActivity : ComponentActivity() {
    private lateinit var openGLHelper: OpenGLHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openGLHelper = OpenGLHelper(this)

        val glSurfaceView =
            openGLHelper.createGLSurfaceView()// Check for OpenGL ES 3.0 support and initialize the view if supported

       // enableEdgeToEdge()
        setContent {
            PyramidVisulizerTheme {
                Scaffold(
                    modifier = Modifier
                        .padding(WindowInsets.systemBars.asPaddingValues())
                        .fillMaxSize()
                ) { innerPadding ->
                        if (glSurfaceView != null) {
                            AndroidView(
                                factory = { glSurfaceView },
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .fillMaxWidth()
                            )
                            ControlPanel(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                onRotateX = {
                                    glSurfaceView.rotateX()
                                },
                                onRotateY = {
                                    glSurfaceView.rotateY()
                                },
                                onRotateZ = {
                                    glSurfaceView.rotateZ()
                                },
                                onTranslateChange = { translationValue ->
                                    glSurfaceView.setTranslation(translationValue, translationValue)
                                },
                                onScaleChange = { scaleFactor ->
                                    glSurfaceView.setScale(scaleFactor)
                                }
                            )

                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            openGLHelper.setImmersiveMode(this)
        }
    }

}
