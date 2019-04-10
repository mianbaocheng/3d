package com.cb.threed.demo.render

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * $Author  cb
 * CreateTime 2019/4/10
 * 等腰三角形
 */
class IsoscelesTriangleRender : GLSurfaceView.Renderer {

    //顶点着色器Code
    private val vertexShaderCode = "attribute vec4 vPosition;" +
            "uniform mat4 vMatrix;" +
            "void main(){" +
            " gl_Position = vMatrix*vPosition;" +
            "}"

    //片元作色器
    private val fragmentShaderCode = "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main(){" +
            " gl_FragColor = vColor;" +
            "}"

    private var vertexBuffer: FloatBuffer? = null

    /**
     * 顶点坐标数组
     */
    private val triangleCoords = floatArrayOf(
            0.5f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f
    )

    private var mPrograms: Int? = null

    private var mProjectMatrix: FloatArray = FloatArray(16)
    private var mViewMatrix: FloatArray = FloatArray(16)
    private var mMVPMatrix: FloatArray = FloatArray(16)
    private val COORDS_PER_VERTEX = 3
    private val vertexStride = COORDS_PER_VERTEX * 4
    private val vertexCount = triangleCoords.size / COORDS_PER_VERTEX
    private val color = floatArrayOf(
            1.0f, 1.0f, 1.0f, 1.0f
    )


    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        //将程序加入到OpenGL ES2.0环境
        GLES20.glUseProgram(mPrograms!!)
        //获取变换租证vMatrix成员句柄
        val mMatrixHandle = GLES20.glGetUniformLocation(mPrograms!!, "vMatrix")
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0)

        //获取顶点着色器的vPosition的句柄
        val mPositionHandle = GLES20.glGetAttribLocation(mPrograms!!, "vPosition")
        //启用三角形顶点句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        //准备三角形的坐标
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer)

        //获取片元着色器句柄
        val colorHandle = GLES20.glGetUniformLocation(mPrograms!!, "vColor")
        //设置绘制三角形颜色
        GLES20.glUniform4fv(colorHandle, 1, color, 0)

        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //计算宽高比
        val ratio = width.toFloat() / height
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1f, 1f, 2f, 4f)
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0f)
        //计算变换举证
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //将背景色设置成为灰色
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
        //申请底层空间
        val byteBuffer = ByteBuffer.allocateDirect(triangleCoords.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        //将坐标数据转换为FloatBuffer，用以传入给OpenGL ES程序
        vertexBuffer = byteBuffer.asFloatBuffer()
        vertexBuffer!!.put(triangleCoords)
        vertexBuffer!!.position(0)

        //创建Programs
        mPrograms = GLES20.glCreateProgram()
        //将顶点作色器加入程序中
        GLES20.glAttachShader(mPrograms!!, loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode))
        //将片元着色器加入程序中
        GLES20.glAttachShader(mPrograms!!, loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode))
        //连接到着色程序
        GLES20.glLinkProgram(mPrograms!!)
    }


    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        //将资源加载到shader中
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
}