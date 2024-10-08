package com.kzcse.pyramidvisulizer.opengl.renderer

import android.opengl.Matrix
class TransformationManager {
    private var transform = Transform()

    fun applyGlobalTransformations(): FloatArray {
        val globalTransformMatrix = FloatArray(16)
        Matrix.setIdentityM(globalTransformMatrix, 0)

        // Apply global transformations from 'transform'
        Matrix.translateM(globalTransformMatrix, 0, transform.transX, transform.transY, 0f)
        Matrix.scaleM(globalTransformMatrix, 0, transform.scale, transform.scale, transform.scale)
        Matrix.rotateM(globalTransformMatrix, 0, transform.angleX, 1.0f, 0.0f, 0.0f)
        Matrix.rotateM(globalTransformMatrix, 0, transform.angleY, 0.0f, 1.0f, 0.0f)
        Matrix.rotateM(globalTransformMatrix, 0, transform.angleZ, 0.0f, 0.0f, 1.0f)

        return globalTransformMatrix
    }

    // Scaling
    fun setScale(scaleFactor: Float) {
        transform.scale = scaleFactor
    }

    // Rotations
    fun rotateX() = transform.rotateX(10.0f)
    fun rotateY() = transform.rotateY(10.0f)
    fun rotateZ() = transform.rotateZ(10.0f)

    // Translation management (X and Y)
    fun setTranslation(x: Float, y: Float) {
        transform.transX = x
        transform.transY = y
    }

    fun getTranslationX(): Float {
        return transform.transX
    }

    fun getTranslationY(): Float {
        return transform.transY
    }
}
