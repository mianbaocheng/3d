package com.cb.threed.demo.render

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.support.v4.math.MathUtils
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * $Author  cb
 * CreateTime 2019/4/11
 */
class CircleRender : GLSurfaceView.Renderer {
    private lateinit var vertexBuffer: FloatBuffer
    private val vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "void main(){" +
                    " gl_Position=vMatrix*vPosition;" +
                    "}"

    private val fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main(){" +
                    " gl_FragColor=vColor;" +
                    "}"

    val n = 1440 //三角新个数
    private val vertexCount = n + 2 //点的个数
    private val vertexPoints = FloatArray(vertexCount * 3)

    private val color = floatArrayOf(
            1.0f, 1.0f, 1.0f, 1.0f
    )

    private val index = 3 //每个点占3个坐标

    private val vertexOff = index * 4

    private val reflectMatrix = FloatArray(16)
    private val cameraMatrix = FloatArray(16)
    private val finalMatrix = FloatArray(16)

    private var mPrograms: Int = 0

    private fun setPoints(radius: Float) {
        //顶点坐标
        vertexPoints[0] = 0f
        vertexPoints[1] = 0f
        vertexPoints[2] = 0f

        val angleDegSpan = 360.toDouble() / n

        var i = 0.toDouble()
        var j = 1
        while (i < 360 + angleDegSpan) {
            vertexPoints[j * 3] = radius * Math.cos((i / 180.toDouble() * Math.PI)).toFloat()
            vertexPoints[j * 3 + 1] = radius * Math.sin(i * Math.PI / 180f).toFloat()
            vertexPoints[j * 3 + 2] = 0f
            Log.i("com.cb.test", "<-----i:" + i + "-----(${vertexPoints[j * 3]},${vertexPoints[j * 3 + 1]},${vertexPoints[j * 3 + 2]})" + "---" +
                    Math.sin(Math.toDegrees(90.0)) + "-------" + Math.sin(180.0 / 180.0 * Math.PI))
            i += angleDegSpan
            j += 1

        }
    }

    override fun onDrawFrame(gl: GL10?) {
        Log.i("com.cb.test", "onDrawFrame")
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        GLES20.glUseProgram(mPrograms)

        val matrixShader = GLES20.glGetUniformLocation(mPrograms, "vMatrix")
        GLES20.glUniformMatrix4fv(matrixShader, 1, false, finalMatrix, 0)

        val vertexShader = GLES20.glGetAttribLocation(mPrograms, "vPosition")
        GLES20.glEnableVertexAttribArray(vertexShader)
        GLES20.glVertexAttribPointer(vertexShader, index, GLES20.GL_FLOAT,
                false, vertexOff, vertexBuffer)

        val colorShader = GLES20.glGetUniformLocation(mPrograms, "vColor")
        GLES20.glUniform4fv(colorShader, 1, color, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount)
        GLES20.glDisableVertexAttribArray(vertexShader)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.i("com.cb.test", "onSurfaceChanged")
        val ratio = width.toFloat() / height
        Matrix.frustumM(reflectMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 10f)
        Matrix.setLookAtM(cameraMatrix, 0, 0f, 0f, 7f, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.multiplyMM(finalMatrix, 0, reflectMatrix, 0, cameraMatrix, 0)

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        setPoints(1.0f)
        Log.i("com.cb.test", "onSurfaceCreated")
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1f)

        val byteBuffer = ByteBuffer.allocateDirect(vertexPoints.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())

        vertexBuffer = byteBuffer.asFloatBuffer()
        vertexBuffer.put(vertexPoints)
        vertexBuffer.position(0)

        val vertexShader = getShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = getShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mPrograms = GLES20.glCreateProgram()

        GLES20.glAttachShader(mPrograms, vertexShader)
        GLES20.glAttachShader(mPrograms, fragmentShader)

        GLES20.glLinkProgram(mPrograms)

    }

    private fun getShader(type: Int, code: String): Int {
        val shader = GLES20.glCreateShader(type)

        GLES20.glShaderSource(shader, code)
        GLES20.glCompileShader(shader)

        return shader
    }
}