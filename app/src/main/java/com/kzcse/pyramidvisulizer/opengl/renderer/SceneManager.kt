package com.kzcse.pyramidvisulizer.opengl.renderer

import com.kzcse.pyramidvisulizer.opengl.`object`.Cactus
import com.kzcse.pyramidvisulizer.opengl.`object`.CactusRenderer
import com.kzcse.pyramidvisulizer.opengl.`object`.Pyramid
import com.kzcse.pyramidvisulizer.opengl.`object`.PyramidRenderer
import com.kzcse.pyramidvisulizer.opengl.`object`.SandDunes
import com.kzcse.pyramidvisulizer.opengl.`object`.SandDunesRenderer
import com.kzcse.pyramidvisulizer.opengl.`object`.Sun
import com.kzcse.pyramidvisulizer.opengl.`object`.SunRenderer

class SceneManager {
    private lateinit var pyramid: Pyramid
    private lateinit var sun: Sun
    private lateinit var sandDunes: SandDunes
    private lateinit var cactus: Cactus

    private val pyramidRenderer = PyramidRenderer()
    private val sunRenderer = SunRenderer()
    private val cactusRenderer = CactusRenderer()
    private val sandDunesRenderer = SandDunesRenderer()

    fun initializeScene() {
        pyramid = Pyramid()
        sun = Sun()
        sandDunes = SandDunes()
        cactus = Cactus()
    }

    fun drawSceneObjects(vpMatrix: FloatArray, globalTransformMatrix: FloatArray) {
        sandDunesRenderer.draw(vpMatrix, globalTransformMatrix, sandDunes)
        sunRenderer.draw(vpMatrix, globalTransformMatrix, sun)
        cactusRenderer.draw(vpMatrix, globalTransformMatrix, cactus)
        pyramidRenderer.draw(vpMatrix, globalTransformMatrix, pyramid)
    }
}
