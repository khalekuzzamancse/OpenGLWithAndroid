@file:Suppress("unused")
package com.kzcse.pyramidvisulizer.opengl


import android.content.ContentValues.TAG
import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Renderer(context: Context) : GLSurfaceView.Renderer {
    private lateinit var pyramid: Pyramid
    private lateinit var sun: Sun
    private lateinit var sandDunes: SandDunes
    private lateinit var cactus: Cactus

    companion object {
        private const val Z_NEAR = 1f
        private const val Z_FAR = 40f

        // Create a shader object, load the shader source, and compile the shader.
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

        // Utility method for debugging OpenGL calls
        fun checkGlError(glOperation: String) {
            var error: Int
            while (GLES30.glGetError().also { error = it } != GLES30.GL_NO_ERROR) {
                Log.e(TAG, "$glOperation: glError $error")
                throw RuntimeException("$glOperation: glError $error")
            }
        }
    }

    private val mMVPMatrix = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mRotationMatrix = FloatArray(16)

    private var transform = Transform()


    override fun onSurfaceCreated(glUnused: GL10?, config: EGLConfig?) {

        GLES30.glClearColor(0.96f, 0.87f, 0.70f, 1.0f) // background color
        pyramid = Pyramid()
        sun= Sun()
        sandDunes = SandDunes()
        cactus = Cactus()

    }

    override fun onSurfaceChanged(glUnused: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        val aspect = width.toFloat() / height
        Matrix.perspectiveM(mProjectionMatrix, 0, 53.13f, aspect, Z_NEAR, Z_FAR)
    }

    override fun onDrawFrame(glUnused: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        // Set up the view matrix
        Matrix.setLookAtM(
            mViewMatrix, 0,
            0f, 1.5f, 5f,   // Eye position
            0f, 0f, 0f,     // Look-at point
            0f, 1.0f, 0.0f  // Up vector
        )

        // Apply any global transformations if needed (e.g., user interactions)
        val globalTransformMatrix = FloatArray(16)
        Matrix.setIdentityM(globalTransformMatrix, 0)
        // Apply global transformations from 'transform' if necessary
        Matrix.translateM(globalTransformMatrix, 0, transform.transX, transform.transY, 0f)
        Matrix.scaleM(globalTransformMatrix, 0, transform.scale, transform.scale, transform.scale)
        Matrix.rotateM(globalTransformMatrix, 0, transform.angleX, 1.0f, 0.0f, 0.0f)
        Matrix.rotateM(globalTransformMatrix, 0, transform.angleY, 0.0f, 1.0f, 0.0f)
        Matrix.rotateM(globalTransformMatrix, 0, transform.angleZ, 0.0f, 0.0f, 1.0f)

        // Combine the view and projection matrices once
        val vpMatrix = FloatArray(16)
        Matrix.multiplyMM(vpMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)

        // Now draw each object with its own model matrix
        // 1. Draw the sand dunes
        val sandDunesModelMatrix = FloatArray(16)
        Matrix.setIdentityM(sandDunesModelMatrix, 0)
        Matrix.scaleM(sandDunesModelMatrix, 0, 10f, 1f, 10f) // Scale to cover a larger area
        // Combine global transformations if needed
        Matrix.multiplyMM(sandDunesModelMatrix, 0, globalTransformMatrix, 0, sandDunesModelMatrix, 0)
        // Compute MVP matrix
        val sandDunesMVPMatrix = FloatArray(16)
        Matrix.multiplyMM(sandDunesMVPMatrix, 0, vpMatrix, 0, sandDunesModelMatrix, 0)
        sandDunes.draw(sandDunesMVPMatrix)

        // 2. Draw the sun
        val sunModelMatrix = FloatArray(16)
        Matrix.setIdentityM(sunModelMatrix, 0)
        Matrix.translateM(sunModelMatrix, 0, 0f, 3f, -5f) // Position the sun in the sky
        Matrix.scaleM(sunModelMatrix, 0, 0.5f, 0.5f, 0.5f) // Adjust the size
        Matrix.multiplyMM(sunModelMatrix, 0, globalTransformMatrix, 0, sunModelMatrix, 0)
        val sunMVPMatrix = FloatArray(16)
        Matrix.multiplyMM(sunMVPMatrix, 0, vpMatrix, 0, sunModelMatrix, 0)
        sun.draw(sunMVPMatrix)

        // 3. Draw the cactus
        val cactusModelMatrix = FloatArray(16)
        Matrix.setIdentityM(cactusModelMatrix, 0)
        Matrix.translateM(cactusModelMatrix, 0, 2f, 0f, 0f) // Position the cactus
        Matrix.scaleM(cactusModelMatrix, 0, 0.7f, 0.7f, 0.7f) // Adjust the size
        Matrix.multiplyMM(cactusModelMatrix, 0, globalTransformMatrix, 0, cactusModelMatrix, 0)
        val cactusMVPMatrix = FloatArray(16)
        Matrix.multiplyMM(cactusMVPMatrix, 0, vpMatrix, 0, cactusModelMatrix, 0)
        cactus.draw(cactusMVPMatrix)

        // 4. Draw the pyramid
        val pyramidModelMatrix = FloatArray(16)
        Matrix.setIdentityM(pyramidModelMatrix, 0)
        Matrix.translateM(pyramidModelMatrix, 0, -2f, 0f, 0f) // Position the pyramid
        Matrix.scaleM(pyramidModelMatrix, 0, 2.0f, 2.0f, 2.0f) // Double the size// Adjust the size
        Matrix.multiplyMM(pyramidModelMatrix, 0, globalTransformMatrix, 0, pyramidModelMatrix, 0)
        val pyramidMVPMatrix = FloatArray(16)
        Matrix.multiplyMM(pyramidMVPMatrix, 0, vpMatrix, 0, pyramidModelMatrix, 0)
        pyramid.draw(pyramidMVPMatrix)
    }

    fun setScale(scaleFactor: Float) {
        transform.scale = scaleFactor
    }

    fun rotateX() = transform.rotateX(10.0f)


    fun rotateY() = transform.rotateY(10.0f)


    fun rotateZ() = transform.rotateZ(10.0f)
    fun setTranslation(x: Float, y: Float) {
        transform.transX = x
        transform.transX = y
    }
}
