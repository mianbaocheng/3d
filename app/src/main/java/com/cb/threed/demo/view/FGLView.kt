package com.cb.threed.demo.view

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.cb.threed.demo.FGLRender

/**
 * $Author  cb
 * CreateTime 2019/4/9
 */
class FGLView : GLSurfaceView {

    constructor(mContext: Context) : this(mContext, null)

    constructor(mContext: Context, attr: AttributeSet?) : super(mContext, attr) {
        initConfig()
    }

    /**
     * 初始化配置
     */
    private fun initConfig() {
        //设置使用OpenGl2.0版本
        setEGLContextClientVersion(2)
        //设置渲染器
        setRenderer(FGLRender())
        //当调用requestRender()的时候进行刷新
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

}