package com.cb.threed.demo.render

import android.graphics.Canvas
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * $Author  cb
 * CreateTime 2019/4/16
 */
class ConeRender : GLSurfaceView.Renderer {
    private val vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "void main(){" +
                    " gl_vPosition = vMatrix*vPosition;" +
                    "}"

    private val fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main(){" +
                    " gl_FragColor=vColor;" +
                    "}"

    private var vertexPoints = null

    private var mProgram: Int = 0


    override fun onDrawFrame(gl: GL10?) {

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)

    }

    /**
     * 初始化点
     */
    private fun initPoints() {

    }


    private fun getShader(type: Int, code: String): Int {
        val shader = GLES20.glCreateShader(type)

        GLES20.glShaderSource(shader, code)
        GLES20.glCompileShader(shader)

        return shader
    }
}