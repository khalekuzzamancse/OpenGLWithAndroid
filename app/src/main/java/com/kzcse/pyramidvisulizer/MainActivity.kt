package com.kzcse.pyramidvisulizer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
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

        //  enableEdgeToEdge()
        setContent {
            PyramidVisulizerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        ControlPanel(
                            onRotateX = {
                                glSurfaceView?.rotateX()
                            },
                            onRotateY = {
                                glSurfaceView?.rotateY()
                            },
                            onRotateZ = {
                                glSurfaceView?.rotateZ()
                            },
                            onTranslateChange = { translationValue ->
                                glSurfaceView?.setTranslation(translationValue, translationValue)
                            },
                            onScaleChange = { scaleFactor ->
                                glSurfaceView?.setScale(scaleFactor)
                            }
                        )

                        if (glSurfaceView != null) {
                            AndroidView(
                                factory = { glSurfaceView },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            )
                        }


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
