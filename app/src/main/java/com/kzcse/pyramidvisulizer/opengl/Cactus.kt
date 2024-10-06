package com.kzcse.pyramidvisulizer.opengl

import android.opengl.GLES30
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Cactus {

    private var mProgramObject: Int = 0
    private var mMVPMatrixHandle: Int = 0
    private lateinit var mVertices: FloatBuffer
    private lateinit var mColors: FloatBuffer

    // Cylinder parameters
    private val cylinderSlices = 36
    private val cylinderHeight = 2.0f
    private val cylinderRadius = 0.2f

    // Sphere parameters
    private val sphereStacks = 18
    private val sphereSlices = 36
    private val sphereRadius = 0.2f

    // Vertex data
    private lateinit var mVerticesData: FloatArray
    private lateinit var mColorsData: FloatArray

    // Vertex shader code
    private val vShaderStr = """
        #version 300 es
        uniform mat4 uMVPMatrix;
        in vec4 vPosition;
        in vec4 vColor;
        out vec4 fColor;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
            fColor = vColor;
        }
    """.trimIndent()

    // Fragment shader code
    private val fShaderStr = """
        #version 300 es
        precision mediump float;
        in vec4 fColor;
        out vec4 fragColor;
        void main() {
            fragColor = fColor;
        }
    """.trimIndent()

    private val TAG = "Cactus"

    // Constructor
    init {
        generateCactus()
        setupBuffers()
        setupProgram()
    }

    private fun generateCactus() {
        val vertices = mutableListOf<Float>()
        val colors = mutableListOf<Float>()
        val greenColor = floatArrayOf(0.0f, 0.5f, 0.0f, 1.0f)

        // Main body (cylinder)
        val bodyVertices = generateCylinder(cylinderRadius, cylinderHeight, cylinderSlices)
        vertices.addAll(bodyVertices)
        for (i in 0 until bodyVertices.size / 3) {
            colors.addAll(greenColor.toList())
        }

        // Top sphere
        val topSphereVertices = generateSphere(sphereRadius, sphereStacks, sphereSlices, 0f, cylinderHeight / 2f, 0f)
        vertices.addAll(topSphereVertices)
        for (i in 0 until topSphereVertices.size / 3) {
            colors.addAll(greenColor.toList())
        }

        // Left arm (cylinder)
        val leftArmVertices = generateCylinder(cylinderRadius * 0.6f, cylinderHeight * 0.6f, cylinderSlices, -0.5f, 0f, 0f, 90f, 0f, 0f)
        vertices.addAll(leftArmVertices)
        for (i in 0 until leftArmVertices.size / 3) {
            colors.addAll(greenColor.toList())
        }

        // Left arm sphere
        val leftSphereVertices = generateSphere(sphereRadius * 0.6f, sphereStacks, sphereSlices, -0.5f, cylinderHeight * 0.3f, 0f)
        vertices.addAll(leftSphereVertices)
        for (i in 0 until leftSphereVertices.size / 3) {
            colors.addAll(greenColor.toList())
        }

        // Right arm (cylinder)
        val rightArmVertices = generateCylinder(cylinderRadius * 0.6f, cylinderHeight * 0.6f, cylinderSlices, 0.5f, 0f, 0f, 90f, 0f, 0f)
        vertices.addAll(rightArmVertices)
        for (i in 0 until rightArmVertices.size / 3) {
            colors.addAll(greenColor.toList())
        }

        // Right arm sphere
        val rightSphereVertices = generateSphere(sphereRadius * 0.6f, sphereStacks, sphereSlices, 0.5f, cylinderHeight * 0.3f, 0f)
        vertices.addAll(rightSphereVertices)
        for (i in 0 until rightSphereVertices.size / 3) {
            colors.addAll(greenColor.toList())
        }

        mVerticesData = vertices.toFloatArray()
        mColorsData = colors.toFloatArray()
    }

    // Function to generate a cylinder
    private fun generateCylinder(radius: Float, height: Float, slices: Int, tx: Float = 0f, ty: Float = 0f, tz: Float = 0f, rx: Float = 0f, ry: Float = 0f, rz: Float = 0f): List<Float> {
        val vertices = mutableListOf<Float>()
        val halfHeight = height / 2f

        for (i in 0 until slices) {
            val theta = (2.0 * Math.PI * i.toDouble()) / slices
            val nextTheta = (2.0 * Math.PI * (i + 1).toDouble()) / slices

            val x0 = (radius * Math.cos(theta)).toFloat()
            val z0 = (radius * Math.sin(theta)).toFloat()
            val x1 = (radius * Math.cos(nextTheta)).toFloat()
            val z1 = (radius * Math.sin(nextTheta)).toFloat()

            // Quad face (two triangles)
            // First triangle
            vertices.addAll(applyTransform(x0, -halfHeight, z0, tx, ty, tz, rx, ry, rz))
            vertices.addAll(applyTransform(x1, -halfHeight, z1, tx, ty, tz, rx, ry, rz))
            vertices.addAll(applyTransform(x0, halfHeight, z0, tx, ty, tz, rx, ry, rz))
            // Second triangle
            vertices.addAll(applyTransform(x1, -halfHeight, z1, tx, ty, tz, rx, ry, rz))
            vertices.addAll(applyTransform(x1, halfHeight, z1, tx, ty, tz, rx, ry, rz))
            vertices.addAll(applyTransform(x0, halfHeight, z0, tx, ty, tz, rx, ry, rz))
        }
        return vertices
    }

    // Function to generate a sphere at a position
    private fun generateSphere(radius: Float, stacks: Int, slices: Int, tx: Float = 0f, ty: Float = 0f, tz: Float = 0f): List<Float> {
        val vertices = mutableListOf<Float>()

        for (i in 0 until stacks) {
            val lat0 = Math.PI * (-0.5 + i.toDouble() / stacks)
            val z0 = (Math.sin(lat0) * radius).toFloat()
            val zr0 = (Math.cos(lat0) * radius).toFloat()

            val lat1 = Math.PI * (-0.5 + (i + 1).toDouble() / stacks)
            val z1 = (Math.sin(lat1) * radius).toFloat()
            val zr1 = (Math.cos(lat1) * radius).toFloat()

            for (j in 0 until slices) {
                val lng0 = 2 * Math.PI * (j.toDouble() / slices)
                val x0 = (Math.cos(lng0) * zr0).toFloat()
                val y0 = (Math.sin(lng0) * zr0).toFloat()

                val lng1 = 2 * Math.PI * ((j + 1).toDouble() / slices)
                val x1 = (Math.cos(lng1) * zr0).toFloat()
                val y1 = (Math.sin(lng1) * zr0).toFloat()

                val x2 = (Math.cos(lng0) * zr1).toFloat()
                val y2 = (Math.sin(lng0) * zr1).toFloat()

                val x3 = (Math.cos(lng1) * zr1).toFloat()
                val y3 = (Math.sin(lng1) * zr1).toFloat()

                // First triangle
                vertices.addAll(listOf(x0 + tx, y0 + ty, z0 + tz))
                vertices.addAll(listOf(x1 + tx, y1 + ty, z0 + tz))
                vertices.addAll(listOf(x2 + tx, y2 + ty, z1 + tz))
                // Second triangle
                vertices.addAll(listOf(x1 + tx, y1 + ty, z0 + tz))
                vertices.addAll(listOf(x3 + tx, y3 + ty, z1 + tz))
                vertices.addAll(listOf(x2 + tx, y2 + ty, z1 + tz))
            }
        }
        return vertices
    }

    // Apply translation and rotation to a vertex
    private fun applyTransform(x: Float, y: Float, z: Float, tx: Float, ty: Float, tz: Float, rx: Float, ry: Float, rz: Float): List<Float> {
        // For simplicity, only translation is applied here
        return listOf(x + tx, y + ty, z + tz)
    }

    private fun setupBuffers() {
        // Setup the vertices buffer
        mVertices = ByteBuffer
            .allocateDirect(mVerticesData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(mVerticesData)
        mVertices.position(0)

        // Setup the colors buffer
        mColors = ByteBuffer
            .allocateDirect(mColorsData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(mColorsData)
        mColors.position(0)
    }

    private fun setupProgram() {
        // Load and compile the shaders
        val vertexShader = Renderer.loadShader(GLES30.GL_VERTEX_SHADER, vShaderStr)
        val fragmentShader = Renderer.loadShader(GLES30.GL_FRAGMENT_SHADER, fShaderStr)

        // Create and link the program object
        val programObject = GLES30.glCreateProgram()

        if (programObject == 0) {
            Log.e(TAG, "Error: Program object could not be created")
        }

        GLES30.glAttachShader(programObject, vertexShader)
        GLES30.glAttachShader(programObject, fragmentShader)

        // Bind attributes
        GLES30.glBindAttribLocation(programObject, 0, "vPosition")
        GLES30.glBindAttribLocation(programObject, 1, "vColor")

        // Link the program
        GLES30.glLinkProgram(programObject)

        // Check the link status
        val linked = IntArray(1)
        GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0)

        if (linked[0] == 0) {
            Log.e(TAG, "Error linking program:")
            Log.e(TAG, GLES30.glGetProgramInfoLog(programObject))
            GLES30.glDeleteProgram(programObject)
        }

        // Store the program object
        mProgramObject = programObject
    }

    fun draw(mvpMatrix: FloatArray) {
        // Use the program object
        GLES30.glUseProgram(mProgramObject)

        // Get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgramObject, "uMVPMatrix")
        Renderer.checkGlError("glGetUniformLocation")

        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        Renderer.checkGlError("glUniformMatrix4fv")

        // Enable vertex attribute arrays
        val VERTEX_POS_INDX = 0
        val VERTEX_COLOR_INDX = 1

        mVertices.position(0)
        GLES30.glVertexAttribPointer(VERTEX_POS_INDX, 3, GLES30.GL_FLOAT, false, 0, mVertices)
        GLES30.glEnableVertexAttribArray(VERTEX_POS_INDX)

        mColors.position(0)
        GLES30.glVertexAttribPointer(VERTEX_COLOR_INDX, 4, GLES30.GL_FLOAT, false, 0, mColors)
        GLES30.glEnableVertexAttribArray(VERTEX_COLOR_INDX)

        // Draw the cactus
        val numVertices = mVerticesData.size / 3
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, numVertices)
    }
}
