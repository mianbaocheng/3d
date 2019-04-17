package com.cb.threed.demo.render

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * $Author  cb
 * CreateTime 2019/4/11
 * 正方体
 */
class CubeRender : GLSurfaceView.Renderer {
    private lateinit var vertextFloatBuffer: FloatBuffer
    private lateinit var colorFloatBuffer: FloatBuffer
    private lateinit var squareBuffer: ShortBuffer
    private val vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "varying vec4 vColor;" +
                    "attribute vec4 aColor;" +
                    "void main(){" +
                    "gl_Position=vMatrix*vPosition;" +
                    "vColor = aColor;" +
                    "}"

    private val fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main(){" +
                    "gl_FragColor=vColor;" +
                    "}"

    //立方体的所有点坐标
    private val vertexPoints = floatArrayOf(
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f
    )

    //使用索引来进行正方形的点的创建(前面，上面，左侧面，右侧面，底部，后面)
    private val squares = shortArrayOf(
            6, 2, 3, 6, 3, 7,
            6, 2, 1, 6, 1, 5,
            6, 5, 4, 6, 4, 7,
            0, 1, 2, 0, 2, 3,
            0, 3, 7, 0, 7, 4,
            0, 1, 5, 0, 5, 4

    )

    private val color = floatArrayOf(
            0f, 1f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
            0f, 0f, 1f, 1f,
            0f, 1f, 1f, 1f,
            1f, 0f, 1f, 1f,
            0f, 0f, 0f, 1f,
            1f, 1f, 1f, 1f
    )

    private val vertexCount = vertexPoints.size
    private val COORDS_PER_VERTEX = 3
    private val offSize = COORDS_PER_VERTEX * 4

    private val reflectMatrix = FloatArray(16)
    private val cameraMatrix = FloatArray(16)
    private val finalMatrix = FloatArray(16)

    private var mProgram = 0

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUseProgram(mProgram)
        val matrixShader = GLES20.glGetUniformLocation(mProgram, "vMatrix")
        GLES20.glUniformMatrix4fv(matrixShader, 1, false, finalMatrix, 0)

        val vertexShader = GLES20.glGetAttribLocation(mProgram, "vPosition")
        GLES20.glEnableVertexAttribArray(vertexShader)
        GLES20.glVertexAttribPointer(vertexShader, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, offSize, vertextFloatBuffer)

        val colorShader = GLES20.glGetAttribLocation(mProgram, "aColor")
        GLES20.glEnableVertexAttribArray(colorShader)
        GLES20.glVertexAttribPointer(colorShader, 4, GLES20.GL_FLOAT
                , false, 0, colorFloatBuffer)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, squares.size, GLES20.GL_UNSIGNED_SHORT,
                squareBuffer)

        GLES20.glDisableVertexAttribArray(vertexShader)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        val radio = width.toFloat() / height
        Matrix.frustumM(reflectMatrix, 0, -radio, radio, -1f, 1f, 3f, 20f)
        Matrix.setLookAtM(cameraMatrix, 0, 5.0f, 5.0f, 10f, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.multiplyMM(finalMatrix, 0, reflectMatrix, 0, cameraMatrix, 0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
        val byteBuffer = ByteBuffer.allocateDirect(vertexCount * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        vertextFloatBuffer = byteBuffer.asFloatBuffer()
        vertextFloatBuffer.put(vertexPoints)
        vertextFloatBuffer.position(0)

        val colorByteBuffer = ByteBuffer.allocateDirect(color.size * 4)
        colorByteBuffer.order(ByteOrder.nativeOrder())
        colorFloatBuffer = colorByteBuffer.asFloatBuffer()
        colorFloatBuffer.put(color)
        colorFloatBuffer.position(0)

        val indexBuffer = ByteBuffer.allocateDirect(squares.size * 4)
        indexBuffer.order(ByteOrder.nativeOrder())
        squareBuffer = indexBuffer.asShortBuffer()
        squareBuffer.put(squares)
        squareBuffer.position(0)


        val vertextShader = getShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = getShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(mProgram, vertextShader)
        GLES20.glAttachShader(mProgram, fragmentShader)

        GLES20.glLinkProgram(mProgram)
    }

    private fun getShader(type: Int, code: String): Int {
        val shader = GLES20.glCreateShader(type)

        GLES20.glShaderSource(shader, code)
        GLES20.glCompileShader(shader)

        return shader
    }
}