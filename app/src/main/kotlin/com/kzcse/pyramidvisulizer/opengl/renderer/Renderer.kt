@file:Suppress("unused")

package com.kzcse.pyramidvisulizer.opengl.renderer


import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Renderer(context: Context) : GLSurfaceView.Renderer {
    private val transformationManager = TransformationManager()
    private val sceneManager = SceneManager()
    private val setupHelper = RenderSetupHelper()
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)

    companion object {
        private const val Z_NEAR = 1f
        private const val Z_FAR = 40f
        fun loadShader(type: Int, shaderSrc: String) = ShaderUtils.loadShader(type, shaderSrc)
        fun checkGlError(glOperation: String) = ShaderUtils.checkGlError(glOperation)
    }

    override fun onSurfaceCreated(glUnused: GL10?, config: EGLConfig?) {
        setBackground()
        sceneManager.initializeScene()
    }

    override fun onSurfaceChanged(glUnused: GL10?, width: Int, height: Int) {
        configureViewport(width, height)
        setupProjectionMatrix(width, height)
    }


    override fun onDrawFrame(glUnused: GL10?) {
        setupHelper.clearBuffersAndEnableDepthTest()
        setupHelper.setupViewMatrix(mViewMatrix)
        val globalTransformMatrix = transformationManager.applyGlobalTransformations()
        val vpMatrix = setupHelper.createVPMatrix(mProjectionMatrix, mViewMatrix)

        sceneManager.drawSceneObjects(vpMatrix, globalTransformMatrix)
    }


    //Delegation Pattern
    fun setScale(scaleFactor: Float) = transformationManager.setScale(scaleFactor)
    fun rotateX() = transformationManager.rotateX()
    fun rotateY() = transformationManager.rotateY()
    fun rotateZ() = transformationManager.rotateZ()
    fun setTranslation(x: Float, y: Float) = transformationManager.setTranslation(x, y)


    var x: Float
        get() = transformationManager.getTranslationX()
        set(value) {
            transformationManager.setTranslation(value, transformationManager.getTranslationY())
        }

    var y: Float
        get() = transformationManager.getTranslationY()
        set(value) {
            transformationManager.setTranslation(transformationManager.getTranslationX(), value)
        }









    //TODO:Helper methods---------TODO:Helper methods
    //TODO:Helper methods---------TODO:Helper methods
    //TODO:Helper methods---------TODO:Helper methods
    /**
     * Sets the OpenGL clear color, which defines the background color of the rendering surface.
     *
     */
    private fun setBackground() {
        GLES30.glClearColor(0.96f, 0.87f, 0.70f, 1.0f)
    }

    /**
     * Initializes the scene objects and other components required for rendering.
     * This typically includes setting up the 3D objects, textures, and shaders that will be rendered.
     */
    private fun initializeSceneObjects() {
        sceneManager.initializeScene()
    }

    /**
     * Configures the OpenGL viewport to match the current surface dimensions. The viewport defines the area
     * of the window where rendering will take place.
     *
     * @param width The current width of the rendering surface.
     * @param height The current height of the rendering surface.
     */
    private fun configureViewport(width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    /**
     * Sets up the projection matrix using a perspective projection. This matrix defines how 3D objects
     * are projected onto the 2D screen, taking into account the width, height, and the near/far clipping planes.
     *
     * @param width The width of the surface.
     * @param height The height of the surface.
     */
    private fun setupProjectionMatrix(width: Int, height: Int) {
        val aspect = width.toFloat() / height
        Matrix.perspectiveM(mProjectionMatrix, 0, 53.13f, aspect, Z_NEAR, Z_FAR)
    }


}
