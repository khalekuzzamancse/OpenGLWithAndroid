package com.kzcse.pyramidvisulizer.opengl.renderer

import android.content.ContentValues.TAG
import android.opengl.GLES30
import android.util.Log

object ShaderUtils {

    fun loadShader(type: Int, shaderSrc: String): Int {
        val shader = GLES30.glCreateShader(type)
        if (shader == 0) return 0

        GLES30.glShaderSource(shader, shaderSrc)
        GLES30.glCompileShader(shader)

        val compiled = IntArray(1)
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0)

        if (compiled[0] == 0) {
            Log.e(TAG, "Error!!!!")
            Log.e(TAG, GLES30.glGetShaderInfoLog(shader))
            GLES30.glDeleteShader(shader)
            return 0
        }

        return shader
    }

    fun checkGlError(glOperation: String) {
        var error: Int
        while (GLES30.glGetError().also { error = it } != GLES30.GL_NO_ERROR) {
            Log.e(TAG, "$glOperation: glError $error")
            throw RuntimeException("$glOperation: glError $error")
        }
    }
}
