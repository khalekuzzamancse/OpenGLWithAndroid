package com.kzcse.pyramidvisulizer.opengl.renderer

import android.opengl.GLES30
import android.opengl.Matrix

/**
 * Helper class that encapsulates common OpenGL rendering setup tasks, specifically focusing
 * on scene preparation such as buffer clearing, view matrix configuration, and view-projection
 * matrix creation.
 * 
 * This class abstracts away low-level OpenGL setup details and is responsible for:
 * 
 * - Clearing color and depth buffers to prepare for rendering each frame.
 * - Enabling depth testing for correct rendering of objects with depth ordering.
 * - Configuring the view matrix (camera position and orientation).
 * - Creating the view-projection (VP) matrix that combines the view and projection matrices
 *   to transform 3D world coordinates into 2D screen coordinates.
 * 
 * This class is intended to be used as part of the OpenGL rendering pipeline, simplifying
 * scene setup for rendering operations.
 */
class RenderSetupHelper {

    /**
     * Clears the color and depth buffers, ensuring that the frame starts with a clean rendering surface.
     * 
     * This is a critical step in the rendering pipeline, as any previously rendered content must be cleared
     * to prevent artifacts from appearing in the current frame. Depth testing is also enabled to ensure proper
     * depth ordering of objects in the scene.
     */
    fun clearBuffersAndEnableDepthTest() {
        // Clear the color and depth buffers
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        // Enable depth testing to ensure correct depth ordering of objects
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
    }

    /**
     * Configures the view matrix, which defines the camera's position, orientation, and the point
     * it is looking at within the scene.
     * 
     * In this configuration, the camera is positioned at (0, 1.5, 5), looking at the origin (0, 0, 0),
     * with the y-axis as the up vector (0, 1, 0). This setup determines how the scene is viewed from the camera's
     * perspective and is a crucial step in scene rendering.
     * 
     * @param mViewMatrix A 4x4 matrix (float array) that will be filled with the calculated view matrix data.
     */
    fun setupViewMatrix(mViewMatrix: FloatArray) {
        Matrix.setLookAtM(
            mViewMatrix, 0,
            0f, 1.5f, 5f,  // Camera position (eye point)
            0f, 0f, 0f,    // Look-at point (center point)
            0f, 1.0f, 0.0f // Up vector (camera orientation)
        )
    }

    /**
     * Creates and returns the view-projection (VP) matrix by multiplying the projection matrix
     * and the view matrix. This matrix transforms 3D world coordinates into 2D screen coordinates,
     * and is used in rendering objects from world space to screen space.
     * 
     * @param mProjectionMatrix A 4x4 projection matrix that defines the perspective projection.
     * @param mViewMatrix A 4x4 view matrix that defines the camera's position and orientation.
     * @return A 4x4 view-projection matrix used for transforming objects into screen space.
     */
    fun createVPMatrix(mProjectionMatrix: FloatArray, mViewMatrix: FloatArray): FloatArray {
        val vpMatrix = FloatArray(16)
        Matrix.multiplyMM(vpMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)
        return vpMatrix
    }
}
