package com.kzcse.pyramidvisulizer.opengl

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ConfigurationInfo
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity

class OpenGLHelper(private val context: Context) {

    private fun isOpenGLES30Supported(): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo: ConfigurationInfo = activityManager.deviceConfigurationInfo
        return configurationInfo.reqGlEsVersion >= 0x30000
    }

    fun createGLSurfaceView(): SurfaceView? {
        return if (isOpenGLES30Supported()) {
            SurfaceView(context)
        } else {
            Log.e("openglcube", "OpenGL ES 3.0 not supported on device. Exiting...")
            null
        }
    }

    fun setImmersiveMode(activity: ComponentActivity) {
        activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
}
