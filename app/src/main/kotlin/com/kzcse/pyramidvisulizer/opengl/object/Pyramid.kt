package com.kzcse.pyramidvisulizer.opengl.`object`;


import android.opengl.GLES30
import android.opengl.Matrix
import android.util.Log
import com.kzcse.pyramidvisulizer.opengl.Colors
import com.kzcse.pyramidvisulizer.opengl.renderer.Renderer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
class PyramidRenderer {
    private val pyramidModelMatrix = FloatArray(16)
    private val pyramidMVPMatrix = FloatArray(16)
    private val pyramidPositionInScreenSpace = FloatArray(4) // To store pyramid's screen position

    var x: Float = 0f

    var y: Float = 0f
    var zPosition: Float = 0f

    // Define the pyramid's size in world space
    private val pyramidWidth: Float = 2.0f
    private val pyramidHeight: Float = 2.0f

    init {
        initializeModelMatrix()
    }

    private fun initializeModelMatrix() {
        Matrix.setIdentityM(pyramidModelMatrix, 0)
    }

    private fun setPosition() {
        Matrix.setIdentityM(pyramidModelMatrix, 0)
        Matrix.translateM(pyramidModelMatrix, 0, x, y, zPosition)
    }

    private fun setScale() {
        Matrix.scaleM(pyramidModelMatrix, 0, pyramidWidth, pyramidHeight, 2.0f) // Assuming some depth scale
    }

    private fun applyGlobalTransformations(globalTransformMatrix: FloatArray) {
        Matrix.multiplyMM(pyramidModelMatrix, 0, globalTransformMatrix, 0, pyramidModelMatrix, 0)
    }

    private fun computeMVPMatrix(vpMatrix: FloatArray) {
        Matrix.multiplyMM(pyramidMVPMatrix, 0, vpMatrix, 0, pyramidModelMatrix, 0)
    }

    fun draw(vpMatrix: FloatArray, globalTransformMatrix: FloatArray, pyramid: Pyramid) {
        setPosition()
        setScale()
        applyGlobalTransformations(globalTransformMatrix)
        computeMVPMatrix(vpMatrix)
        pyramid.draw(pyramidMVPMatrix)
    }


    fun isPyramidTouched(vpMatrix: FloatArray, touchX: Float, touchY: Float, screenWidth: Int, screenHeight: Int): Boolean {
        // Transform the pyramid's position to screen coordinates
        val pyramidPosition = floatArrayOf(x, y, zPosition, 1f)
        Matrix.multiplyMV(pyramidPositionInScreenSpace, 0, vpMatrix, 0, pyramidPosition, 0)

        // Normalize the screen coordinates (divide by w)
        val screenX = (pyramidPositionInScreenSpace[0] / pyramidPositionInScreenSpace[3] + 1) / 2 * screenWidth
        val screenY = (1 - (pyramidPositionInScreenSpace[1] / pyramidPositionInScreenSpace[3] + 1) / 2) * screenHeight
        println(
            "TappedPosition:ScreenCoordinate: ($screenX, $screenY):"
        )
        // Calculate the bounds of the pyramid in screen space
        val pyramidScreenWidth = pyramidWidth / 2 * screenWidth
        val pyramidScreenHeight = pyramidHeight / 2 * screenHeight

        // Check if the touch point is within the pyramid's screen-space bounds
        return touchX in (screenX - pyramidScreenWidth)..(screenX + pyramidScreenWidth) &&
                touchY in (screenY - pyramidScreenHeight)..(screenY + pyramidScreenHeight)
    }
}



class Pyramid {

    private var mProgramObject: Int = 0
    private var mMVPMatrixHandle: Int = 0
    private var mColorHandle: Int = 0
    private var mVertices: FloatBuffer

    // Initial size of the pyramid, set here for easy modification later
    private val size = 0.4f

    // Initial vertex data for the pyramid
    private val mVerticesData = floatArrayOf(
            // Top
            // Front
            0.0f, size, 0.0f,   // top
            -size, -size, size, // front-left
            size, -size, size,  // front-right
            // Right
            0.0f, size, 0.0f,   // top
            size, -size, size,  // front-right
            size, -size, -size, // back-right
            // Back
            0.0f, size, 0.0f,   // top
            size, -size, -size, // back-right
            -size, -size, -size, // back-left
            // Left
            0.0f, size, 0.0f,   // top
            -size, -size, -size, // back-left
            -size, -size, size,  // front-left
            // Bottom
            -size, -size, -size, // back-left
            -size, -size, size,  // front-left
            size, -size, size,   // front-right
            size, -size, size,   // front-right
            size, -size, -size,  // back-right
            -size, -size, -size  // back-left
    )

    // Colors for different faces
    private val colorCyan = Colors.cyan
    private val colorBlue = Colors.blue
    private val colorRed = Colors.red
    private val colorGray = Colors.gray
    private val colorGreen = Colors.green
    private val colorYellow = Colors.yellow

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

    private val TAG = "Pyramid"

    // Constructor
    init {
        // Setup the vertices buffer
        mVertices = ByteBuffer
                .allocateDirect(mVerticesData.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(mVerticesData)
        mVertices.position(0)

        // Load and compile the shaders
        val vertexShader = Renderer.loadShader(GLES30.GL_VERTEX_SHADER, vShaderStr)
        val fragmentShader = Renderer.loadShader(GLES30.GL_FRAGMENT_SHADER, fShaderStr)

        // Create and link the program object
        val programObject = GLES30.glCreateProgram()

        if (programObject == 0) {
            Log.e(TAG, "Error: Program object could not be created")
            //return
        }

        GLES30.glAttachShader(programObject, vertexShader)
        GLES30.glAttachShader(programObject, fragmentShader)

        // Bind vPosition to attribute 0
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
         //   return
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

        // Get handle to fragment shader's vColor member
        mColorHandle = GLES30.glGetUniformLocation(mProgramObject, "vColor")

        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        Renderer.checkGlError("glUniformMatrix4fv")

        // Set up vertex data
        val VERTEX_POS_INDX = 0
        mVertices.position(VERTEX_POS_INDX)

        GLES30.glVertexAttribPointer(VERTEX_POS_INDX, 3, GLES30.GL_FLOAT, false, 0, mVertices)
        GLES30.glEnableVertexAttribArray(VERTEX_POS_INDX)

        // Draw each face of the pyramid
        var startPos = 0
        val verticesPerFace = 3

        // Draw front face
        GLES30.glUniform4fv(mColorHandle, 1, colorBlue, 0)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerFace)
        startPos += verticesPerFace

        // Draw right face
        GLES30.glUniform4fv(mColorHandle, 1, colorCyan, 0)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerFace)
        startPos += verticesPerFace

        // Draw back face
        GLES30.glUniform4fv(mColorHandle, 1, colorRed, 0)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerFace)
        startPos += verticesPerFace

        // Draw left face
        GLES30.glUniform4fv(mColorHandle, 1, colorGray, 0)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerFace)
        startPos += verticesPerFace

        // Draw bottom faces
        GLES30.glUniform4fv(mColorHandle, 1, colorYellow, 0)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerFace)
        startPos += verticesPerFace

        GLES30.glUniform4fv(mColorHandle, 1, colorYellow, 0)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerFace)
    }
}
