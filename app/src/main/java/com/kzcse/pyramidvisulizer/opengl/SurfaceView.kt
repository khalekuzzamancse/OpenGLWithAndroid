package com.kzcse.pyramidvisulizer.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.kzcse.pyramidvisulizer.opengl.renderer.Renderer

class SurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: com.kzcse.pyramidvisulizer.opengl.renderer.Renderer

    init {
        setEGLContextClientVersion(3)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        renderer = Renderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
    //private final float TOUCH_SCALE_FACTOR = 180.0f / 320;

    val TOUCH_SCALE_FACTOR: Float = 0.015f
    private var mPreviousX = 0f
    private var mPreviousY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x: Float = event.getX()
        val y: Float = event.getY()
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx = x - mPreviousX
                //subtract, so the cube moves the same direction as your finger with plus it moves the opposite direction.
                renderer.setX(renderer.getX() - (dx * TOUCH_SCALE_FACTOR))
                val dy = y - mPreviousY
                renderer.setY(renderer.getY() - (dy *TOUCH_SCALE_FACTOR))
            }
        }
        mPreviousX = x
        mPreviousY = y
        return true

    }



    fun setTranslation(x: Float, y: Float) = renderer.setTranslation(x, y)
    fun setScale(scaleFactor: Float) = renderer.setScale(scaleFactor)
    fun rotateX() = renderer.rotateX()
    fun rotateY() = renderer.rotateY()
    fun rotateZ() = renderer.rotateZ()


}
