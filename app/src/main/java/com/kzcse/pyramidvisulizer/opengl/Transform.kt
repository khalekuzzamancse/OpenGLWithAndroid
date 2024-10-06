package com.kzcse.pyramidvisulizer.opengl;
class Transform {
    var scale = 1.0f
    var angleX = 0.0f
    var angleY = 0.0f
    var angleZ = 0.0f
    var transX = 0.0f
    var transY = 0.0f

    fun rotateX(angle: Float) {
        angleX += angle
    }

    fun rotateY(angle: Float) {
        angleY += angle
    }

    fun rotateZ(angle: Float) {
        angleZ += angle
    }
}
