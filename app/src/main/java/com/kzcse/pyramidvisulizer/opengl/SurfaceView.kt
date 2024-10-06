package com.kzcse.pyramidvisulizer.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

class SurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer:com.kzcse.pyramidvisulizer.opengl.Renderer

    init {
        setEGLContextClientVersion(3)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        renderer = Renderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchPointX = event.x
        val touchPointY = event.y

        return true
    }



    fun setTranslation(x: Float, y: Float) = renderer.setTranslation(x, y)
    fun setScale(scaleFactor: Float) = renderer.setScale(scaleFactor)
    fun rotateX() = renderer.rotateX()
    fun rotateY() = renderer.rotateY()
    fun rotateZ() = renderer.rotateZ()


}
