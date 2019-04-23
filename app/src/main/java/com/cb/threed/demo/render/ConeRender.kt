package com.cb.threed.demo.render

import android.graphics.Canvas
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * $Author  cb
 * CreateTime 2019/4/16
 * 绘制圆锥
 */
class ConeRender : GLSurfaceView.Renderer {
//    private val vertexShaderCode =
//            "attribute vec4 vPosition;" +
//                    "uniform mat4 vMatrix;" +
//                    "varying vec4 vColor;" +
//                    "void main(){" +
//                    " gl_Position = vMatrix*vPosition;" +
//                    "if(vPosition.z != 0.0){" +
//                    "  vColor = vec4(0.0f,0.0f,0.0f,1.0f);" +
//                    "}else{" +
//                    " vColor = vec4(0.9f,0.9f,0.9f,1.0f);" +
//                    "}" +
//                    "}"

    private val vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "varying vec4 vColor;" +
                    "void main(){" +
                    " gl_Position = vMatrix*vPosition;" +
                    "vColor = vec4(0.0,0.0,1.0,1.0)" +
                    "}"

    private val fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main(){" +
                    " gl_FragColor=vColor;" +
                    "}"

    private val size = 360
    private val height = 1.0f
    private val radius = 1.0f
    private var vertexPoints = FloatArray((size + 2) * 3)

    private var pointValueCount = vertexPoints.size

    private var mProgram: Int = 0
    private lateinit var floatBuffer: FloatBuffer

    private var reflectMatrix = FloatArray(16)
    private var cameraMatrix = FloatArray(16)
    private var finalMatrix = FloatArray(16)
    private val color = floatArrayOf(
            1.0f, 0.0f, 0.0f, 1.0f
    )


    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        GLES20.glUseProgram(mProgram)

        val matrixShader = GLES20.glGetUniformLocation(mProgram, "vMatrix")
        GLES20.glUniformMatrix4fv(matrixShader, 1, false, finalMatrix, 0)

        val vertexShader = GLES20.glGetAttribLocation(mProgram, "vPosition")
        GLES20.glEnableVertexAttribArray(vertexShader)
        GLES20.glVertexAttribPointer(vertexShader, 3, GLES20.GL_FLOAT,
                false, 12, floatBuffer)

        val fragmentShader = GLES20.glGetUniformLocation(mProgram, "vColor")
        GLES20.glUniform4fv(fragmentShader, 1, color, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, size + 2)
        GLES20.glDisableVertexAttribArray(vertexShader)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        val ratio = width / height.toFloat()
        Matrix.frustumM(reflectMatrix, 0, -ratio, ratio, 1.0f, -1.0f, 3f, 20f)
        Matrix.setLookAtM(cameraMatrix, 0, 0f, 0f, 7f, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.multiplyMM(finalMatrix, 0, reflectMatrix, 0, cameraMatrix, 0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        initPoints()
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
        val byteBuffer = ByteBuffer.allocateDirect(pointValueCount * 4)
        floatBuffer = byteBuffer.asFloatBuffer()
        floatBuffer.put(vertexPoints)
        floatBuffer.position(0)

        val vertexShader = getShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = getShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram()

        GLES20.glAttachShader(mProgram, vertexShader)
        GLES20.glAttachShader(mProgram, fragmentShader)

        GLES20.glLinkProgram(mProgram)

    }

    /**
     * 初始化点
     */
    private fun initPoints() {
        vertexPoints[0] = 0f
        vertexPoints[1] = 0f
        vertexPoints[2] = height

        val tempAngle = 360f / size
        var i = 0f
        var j = 1
        while (i < 360 + tempAngle) {
            vertexPoints[j * 3] = radius * Math.cos(i * Math.PI / 180f).toFloat()
            vertexPoints[j * 3 + 1] = radius * Math.sin(i * Math.PI / 180f).toFloat()
            vertexPoints[j * 3 + 2] = 0f
            i += tempAngle
            j += 1
        }

    }


    private fun getShader(type: Int, code: String): Int {
        val shader = GLES20.glCreateShader(type)

        GLES20.glShaderSource(shader, code)
        GLES20.glCompileShader(shader)

        return shader
    }
}