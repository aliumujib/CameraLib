package com.aliumujib.cameralib

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log

/**
 * Created by aliumujib on 08/06/2018.
 */

class CameraViewModel(val listOfImages: MutableList<String>) : ViewModel() {

    val LOGGING_TAG = javaClass.canonicalName


    var flashEnabled = false
    var flashToggled = MutableLiveData<Boolean>()

    var activeCamera: Camera = Camera.Back
    var currentCamera: MutableLiveData<Camera> = MutableLiveData()

    var currentZoom: Int = 0
    var zoom: MutableLiveData<Int> = MutableLiveData()

    var maxPhotoCount: Int = 5
    var isAllowedToSnap = MutableLiveData<Boolean>()
    var photoAllowance = MutableLiveData<Int>()
    var refreshPhotos = MutableLiveData<Unit>()

    fun setUp() {
        emitCanTakeMorePhotos()
        setZoom(0)
        setCurrentCamera(activeCamera)
    }

    private fun emitCanTakeMorePhotos() {
        refreshPhotos.value = Unit
        photoAllowance.value = maxPhotoCount - listOfImages.size
        isAllowedToSnap.value = maxPhotoCount > listOfImages.size
    }

    fun toggleFlash() {
        flashEnabled = !flashEnabled
        flashToggled.value = flashEnabled
    }

    private fun setCurrentCamera(camera: Camera) {
        currentCamera.value = camera
    }

    fun setZoom(zoom: Int) {
        currentZoom = zoom
        this.zoom.value = currentZoom
    }

    fun addFilePath(filePath: String) {
        listOfImages.add(filePath)
        emitCanTakeMorePhotos()
    }

    fun changeCamera() {
        activeCamera = when (activeCamera) {
            Camera.Front -> Camera.Back
            Camera.Back -> Camera.Front
        }
        setCurrentCamera(activeCamera)
        zoom.value = 0
        flashToggled.value = false

        Log.i(LOGGING_TAG, "New camera position: ${if (activeCamera is Camera.Back) "back" else "front"}")
    }

}