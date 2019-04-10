package com.cb.threed.demo.render

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * $Author  cb
 * CreateTime 2019/4/9
 */
class FGLRender : GLSurfaceView.Renderer {

    private var vertexBuffer: FloatBuffer? = null
    //顶点着色器 gl_Position为Shader的内部变量，顶点的位置
    private val vertexShaderCode = "attribute vec4 vPosition;" +
            "void main(){" +
            "   gl_Position = vPosition;" +
            "}"

    //片元着色器 gl_FragColor为Shader的内容变量，片元的颜色
    private val fragmentShaderCode = "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main(){" +
            "   gl_FragColor=vColor;" +
            "}"

    //定义坐标数据
    private val triangleCoords = floatArrayOf(
            0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f
    )


    //定义单一颜色(白色)
    private val color = floatArrayOf(
            1.0f, 1.0f, 1.0f, 1.0f
    )

    private var mPrograms: Int? = null
    private val COORDS_PER_VERTEX = 3
    private val vertexStride = COORDS_PER_VERTEX * 4 //每个顶点四个字节
    private val vertexCount = triangleCoords.size / COORDS_PER_VERTEX

    override fun onDrawFrame(gl: GL10?) {//绘制
        Log.i("com.cb.test", "<--------------onDrawFrame")
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mPrograms!!)

        //获取顶点作色器的vPosition成员句柄
        val mPositionHandle = GLES20.glGetAttribLocation(mPrograms!!, "vPosition")
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer)
        //获取片元着色器的vColor成员的句柄
        val mColorHandle = GLES20.glGetUniformLocation(mPrograms!!, "vColor")
        //设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0)
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {//设置视图窗口
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {//创建program对象
        //将背景颜色设置为灰色
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
        //申请底层空间
        val byteBuffer = ByteBuffer.allocateDirect(triangleCoords.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        //将坐标数据转换为FloatBuffer,用以传入给OpenGL ES程序
        vertexBuffer = byteBuffer.asFloatBuffer()
        vertexBuffer!!.put(triangleCoords)
        vertexBuffer!!.position(0)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        //创建一个空的OpenGlES程序
        mPrograms = GLES20.glCreateProgram()
        //将顶点着色器加入到程序中
        GLES20.glAttachShader(mPrograms!!, vertexShader)
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mPrograms!!, fragmentShader)
        //连接到着色程序
        GLES20.glLinkProgram(mPrograms!!)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
}