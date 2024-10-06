package com.kzcse.pyramidvisulizer.opengl

import android.opengl.GLES30
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class SandDunes {

    private var mProgramObject: Int = 0
    private var mMVPMatrixHandle: Int = 0
    private lateinit var mVertices: FloatBuffer

    // Grid size
    private val gridSize = 50 // Adjust for detail
    private val planeSize = 5f // Size of the plane

    // Vertex data
    private lateinit var mVerticesData: FloatArray

    // Color for the sand
    private val sandColor = floatArrayOf(0.96f, 0.76f, 0.36f, 1f) // Sand-like color

    // Vertex shader code
    private val vShaderStr = """
        #version 300 es
        uniform mat4 uMVPMatrix;
        in vec4 vPosition;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
        }
    """.trimIndent()

    // Fragment shader code
    private val fShaderStr = """
        #version 300 es
        precision mediump float;
        uniform vec4 vColor;
        out vec4 fragColor;
        void main() {
            fragColor = vColor;
        }
    """.trimIndent()

    private val TAG = "SandDunes"

    // Constructor
    init {
        generateDunes()
        setupBuffers()
        setupProgram()
    }

    private fun generateDunes() {
        val vertices = mutableListOf<Float>()
        val halfSize = planeSize / 2f
        val step = planeSize / gridSize

        for (i in 0..gridSize) {
            val z = -halfSize + i * step
            for (j in 0..gridSize) {
                val x = -halfSize + j * step
                val y = getHeight(x, z)
                vertices.addAll(listOf(x, y, z))
            }
        }

        // Create indices for triangle strips
        val indices = mutableListOf<Short>()
        for (i in 0 until gridSize) {
            for (j in 0..gridSize) {
                val row1 = (i * (gridSize + 1) + j).toShort()
                val row2 = ((i + 1) * (gridSize + 1) + j).toShort()
                indices.add(row1)
                indices.add(row2)
            }
            // Restart the strip
            if (i < gridSize - 1) {
                indices.add((-1).toShort())
            }
        }

        // Convert lists to arrays
        mVerticesData = vertices.toFloatArray()
        mIndicesData = indices.toShortArray()
    }

    // Height function to simulate dunes
    private fun getHeight(x: Float, z: Float): Float {
        val frequency = 1.0f
        val amplitude = 0.2f
        return (Math.sin((frequency * x).toDouble()) * Math.cos((frequency * z).toDouble()) * amplitude).toFloat()
    }

    private lateinit var mIndicesData: ShortArray
    private lateinit var mIndices: ByteBuffer

    private fun setupBuffers() {
        // Setup the vertices buffer
        mVertices = ByteBuffer
            .allocateDirect(mVerticesData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(mVerticesData)
        mVertices.position(0)

        // Setup the indices buffer
        mIndices = ByteBuffer
            .allocateDirect(mIndicesData.size * 2)
            .order(ByteOrder.nativeOrder())
        mIndices.asShortBuffer().put(mIndicesData)
        mIndices.position(0)
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

        // Set the color
        val mColorHandle = GLES30.glGetUniformLocation(mProgramObject, "vColor")
        GLES30.glUniform4fv(mColorHandle, 1, sandColor, 0)

        // Enable vertex attribute arrays
        val VERTEX_POS_INDX = 0

        mVertices.position(0)
        GLES30.glVertexAttribPointer(VERTEX_POS_INDX, 3, GLES30.GL_FLOAT, false, 0, mVertices)
        GLES30.glEnableVertexAttribArray(VERTEX_POS_INDX)

        // Draw the dunes using triangle strips with primitive restart
        GLES30.glEnable(GLES30.GL_PRIMITIVE_RESTART_FIXED_INDEX)
        GLES30.glDrawElements(GLES30.GL_TRIANGLE_STRIP, mIndicesData.size, GLES30.GL_UNSIGNED_SHORT, mIndices)
        GLES30.glDisable(GLES30.GL_PRIMITIVE_RESTART_FIXED_INDEX)
    }
}
