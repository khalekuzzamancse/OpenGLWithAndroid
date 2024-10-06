package com.kzcse.pyramidvisulizer.opengl;

import android.graphics.Color

class Colors {
    companion object {
        val red = floatArrayOf(
                Color.red(Color.RED) / 255f,
                Color.green(Color.RED) / 255f,
                Color.blue(Color.RED) / 255f,
                1.0f
        )

        val green = floatArrayOf(
                Color.red(Color.GREEN) / 255f,
                Color.green(Color.GREEN) / 255f,
                Color.blue(Color.GREEN) / 255f,
                1.0f
        )

        val blue = floatArrayOf(
                Color.red(Color.BLUE) / 255f,
                Color.green(Color.BLUE) / 255f,
                Color.blue(Color.BLUE) / 255f,
                1.0f
        )

        val yellow = floatArrayOf(
                Color.red(Color.YELLOW) / 255f,
                Color.green(Color.YELLOW) / 255f,
                Color.blue(Color.YELLOW) / 255f,
                1.0f
        )

        val cyan = floatArrayOf(
                Color.red(Color.CYAN) / 255f,
                Color.green(Color.CYAN) / 255f,
                Color.blue(Color.CYAN) / 255f,
                1.0f
        )

        val gray = floatArrayOf(
                Color.red(Color.GRAY) / 255f,
                Color.green(Color.GRAY) / 255f,
                Color.blue(Color.GRAY) / 255f,
                1.0f
        )
        val magenta = floatArrayOf(1f, 0f, 1f, 1f)
        val white = floatArrayOf(1f, 1f, 1f, 1f)
    }
}
