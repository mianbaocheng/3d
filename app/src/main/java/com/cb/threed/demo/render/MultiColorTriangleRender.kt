package com.cb.threed.demo.render

import android.graphics.Shader
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL
import javax.microedition.khronos.opengles.GL10

/**
 * $Author  cb
 * CreateTime 2019/4/10
 * 等腰三角形彩色
 */
class MultiColorTriangleRender : GLSurfaceView.Renderer {
    private var floatBuffer: FloatBuffer? = null
    private var colorFloatBuffer: FloatBuffer? = null
    private val vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "varying vec4 vColor;" +
                    "attribute vec4 aColor;" +
                    "void main(){" +
                    " gl_Position = vMatrix*vPosition;" +
                    " vColor = aColor;" +
                    "}"
    private val fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main(){" +
                    " gl_FragColor=vColor;" +
                    "} "

    private val trianglePoints = floatArrayOf(
            0.5f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f
    )

    private val color = floatArrayOf(
            0f, 1f, 0f, 1f,
            1f, 0f, 0f, 1f,
            0f, 0f, 1f, 1f
    )

    private val pointDes = 3
    private val pointCount = trianglePoints.size
    private val offPoint = pointDes * 4

    private var mProgram: Int? = null

    private val reflectArray = FloatArray(16)
    private val cameraArray = FloatArray(16)
    private val finalArray = FloatArray(16)

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        GLES20.glUseProgram(mProgram!!)

        val matrixShader = GLES20.glGetUniformLocation(mProgram!!, "vMatrix")
        GLES20.glUniformMatrix4fv(matrixShader, 1, false, finalArray, 0)

        val vertexShader = GLES20.glGetAttribLocation(mProgram!!, "vPosition")
        GLES20.glEnableVertexAttribArray(vertexShader)
        GLES20.glVertexAttribPointer(vertexShader, pointDes, GLES20.GL_FLOAT
                , false, offPoint, floatBuffer)

        val fragmentShader = GLES20.glGetAttribLocation(mProgram!!, "aColor")
        GLES20.glEnableVertexAttribArray(fragmentShader)
        GLES20.glVertexAttribPointer(fragmentShader, 4, GLES20.GL_FLOAT,
                false, 0, colorFloatBuffer)
//        GLES20.glUniform4fv(fragmentShader, 1, color, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, pointCount)

        GLES20.glDisableVertexAttribArray(vertexShader)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        val ratio = width.toFloat() / height
        Matrix.frustumM(reflectArray, 0, -ratio, ratio, -1f, 1f, 3f, 10f)
        Matrix.setLookAtM(cameraArray, 0, 0f, 0f, 7f, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.multiplyMM(finalArray, 0, reflectArray, 0, cameraArray, 0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
        val byteBuffer = ByteBuffer.allocateDirect(trianglePoints.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        floatBuffer = byteBuffer.asFloatBuffer()
        floatBuffer!!.put(trianglePoints)
        floatBuffer!!.position(0)

        val colorByteBuffer = ByteBuffer.allocateDirect(color.size * 4)
        colorByteBuffer.order(ByteOrder.nativeOrder())
        colorFloatBuffer = colorByteBuffer.asFloatBuffer()
        colorFloatBuffer!!.put(color)
        colorFloatBuffer!!.position(0)

        mProgram = GLES20.glCreateProgram()

        val vertexShader = getShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = getShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        GLES20.glAttachShader(mProgram!!, vertexShader)
        GLES20.glAttachShader(mProgram!!, fragmentShader)
        GLES20.glLinkProgram(mProgram!!)

    }

    private fun getShader(type: Int, code: String): Int {
        val shader = GLES20.glCreateShader(type)

        GLES20.glShaderSource(shader, code)
        GLES20.glCompileShader(shader)

        return shader
    }
}