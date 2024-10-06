package com.kzcse.pyramidvisulizer.opengl


import android.opengl.GLES30
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Sun {

    private var mProgramObject: Int = 0
    private var mMVPMatrixHandle: Int = 0
    private var mColorHandle: Int = 0
    private lateinit var mVertices: FloatBuffer
    private lateinit var mColors: FloatBuffer

    // Number of stacks and slices to approximate the sphere
    private val stacks = 18  // Adjust for smoothness
    private val slices = 36  // Adjust for smoothness
    private val radius = 0.5f

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

    private val TAG = "Sun"

    // Constructor
    init {
        generateSphere()
        setupBuffers()
        setupProgram()
    }

    private fun generateSphere() {
        val vertices = mutableListOf<Float>()
        val colors = mutableListOf<Float>()
        val colorList = listOf(
            Colors.red, Colors.green, Colors.blue, Colors.yellow,
            Colors.cyan, Colors.magenta, Colors.gray, Colors.white
        )

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
                vertices.addAll(listOf(x0, y0, z0))
                vertices.addAll(listOf(x1, y1, z0))
                vertices.addAll(listOf(x2, y2, z1))
                // Second triangle
                vertices.addAll(listOf(x1, y1, z0))
                vertices.addAll(listOf(x3, y3, z1))
                vertices.addAll(listOf(x2, y2, z1))

                // Assign colors to each vertex of the triangles
                val color = colorList[(i * slices + j) % colorList.size]
                for (k in 0 until 6) { // Two triangles, three vertices each
                    colors.addAll(color.toList())
                }
            }
        }

        mVerticesData = vertices.toFloatArray()
        mColorsData = colors.toFloatArray()
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

        // Draw the sun
        val numVertices = mVerticesData.size / 3
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, numVertices)
    }
}
