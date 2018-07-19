package com.aliumujib.cameralib

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.aliumujib.cameralib.imageloader.PicassoImageLoader
import com.squareup.picasso.Picasso
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.UpdateConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.parameter.Flash
import io.fotoapparat.parameter.Zoom
import io.fotoapparat.selector.*
import android.content.Intent
import kotlinx.android.synthetic.main.activity_camera_lib.*
import java.util.ArrayList
import kotlin.jvm.JvmField
import kotlin.jvm.JvmName


private const val LOGGING_TAG = "CAMERA_LIB"


//@JvmName("CameraLibActivity")
class CameraLibActivity : AppCompatActivity() {
    val RESULT_TAG = "RESULT_TAG"

    companion object {
        @JvmField
        public val PHOTO_DATA_TAG = "DATA_TAG"
        @JvmField
        public val MAX_PHOTO_COUNT: String = "MAX_PHOTO_COUNT"
        @JvmField
        public val SET_RESULT_BTN_TITLE: String = "SET_RESULT_BTN_TITLE"
    }


    private var pathList: MutableList<String> = mutableListOf()
    private val permissionsDelegate = PermissionsDelegate(this)
    private var cameraPermissionsGranted: Boolean = false
    private var storagePermissionsGranted: Boolean = false

    private lateinit var fotoapparat: Fotoapparat
    private lateinit var cameraViewModel: CameraViewModel
    private lateinit var photoAdapter: PhotoAdapter

    lateinit var camVMFactory: ViewModelFactory<CameraViewModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_lib)

        initializeViewModel()

        checkPermission()

        observeCurrentCamera()

        observeZoomLevels()

        observeFlashToggle()

        // observePhotoList()

        listenToCloseCamera()

        listenForViewInteractions()

        initializePreview()

        result_setter.setOnClickListener {
            setResults()
        }
    }


    private fun setResults() {
        val output = Intent()
        output.putStringArrayListExtra(PHOTO_DATA_TAG, cameraViewModel.listOfImages.toList() as ArrayList<String>?)
        setResult(Activity.RESULT_OK, output)
        finish()
    }

    private fun observePhotoList() {
        cameraViewModel.refreshPhotos.observe(this, Observer {
            photoAdapter.notifyDataSetChanged()
        })
    }

    private fun listenToCloseCamera() {
        cameraViewModel.photoAllowance.observe(this, Observer {
            allowance_count.text = it.toString()
        })
        cameraViewModel.isAllowedToSnap.observe(this, Observer {
            if (it!!) {
                capture.visibility = View.VISIBLE
                result_setter.visibility = View.GONE
            } else {
                capture.visibility = View.GONE
                result_setter.visibility = View.VISIBLE
            }
        })
    }

    private fun listenForViewInteractions() {
        capture onClick takePicture()

        zoomSeekBar onProgressChanged updateZoom()

        switchCamera.setOnCameraTypeChangeListener(object : CameraSwitchView.OnCameraTypeChangeListener {
            override fun onCameraTypeChanged(cameraType: Int) {
                changeCamera()
            }
        })

        torchSwitch.setFlashSwitchListener(object : FlashSwitchView.FlashModeSwitchListener {
            override fun onFlashModeChanged(mode: Int) {
                cameraViewModel.toggleFlash()
            }
        })
    }

    private fun observeFlashToggle() {
        cameraViewModel.flashToggled.observe(this, Observer {
            fotoapparat.updateConfiguration(
                    UpdateConfiguration(
                            flashMode = if (it!!) {
                                firstAvailable(
                                        torch(),
                                        off()
                                )
                            } else {
                                off()
                            }
                    )
            )
        })
    }

    private fun observeZoomLevels() {
        cameraViewModel.zoom.observe(this, Observer {
            fotoapparat.setZoom(it!! / zoomSeekBar.max.toFloat())
        })
    }

    private fun observeCurrentCamera() {
        cameraViewModel.currentCamera.observe(this, Observer {
            fotoapparat.switchTo(
                    lensPosition = it!!.lensPosition,
                    cameraConfiguration = it.configuration
            )

            adjustViewsVisibility()
        })
    }

    private fun initializePreview() {
        // Creates a vertical Layout Manager
        val imageLoader = PicassoImageLoader(Picasso.with(this))
        snapped_photos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        photoAdapter = PhotoAdapter(pathList, this, imageLoader)
        snapped_photos.adapter = photoAdapter

        fotoapparat = Fotoapparat(
                context = this,
                view = cameraView,
                focusView = focusView,
                logger = logcat(),
                lensPosition = cameraViewModel.activeCamera.lensPosition,
                cameraConfiguration = cameraViewModel.activeCamera.configuration,
                cameraErrorCallback = { Log.e(LOGGING_TAG, "Camera error: ", it) }
        )

    }

    private fun checkPermission() {
        cameraPermissionsGranted = permissionsDelegate.hasCameraPermission()
        storagePermissionsGranted = permissionsDelegate.hasStoragePermission()

        if (cameraPermissionsGranted && storagePermissionsGranted) {
            cameraView.visibility = View.VISIBLE
        } else {
            permissionsDelegate.requestStoragePermission()
            permissionsDelegate.requestCameraPermission()
        }
    }

    private fun initializeViewModel() {
        camVMFactory = ViewModelFactory(lazyOf(CameraViewModel(pathList, intent.getIntExtra(MAX_PHOTO_COUNT, 5))))
        if(intent.getStringExtra(SET_RESULT_BTN_TITLE)!=null){
            result_setter.text = intent.getStringExtra(SET_RESULT_BTN_TITLE)
        }
        cameraViewModel = ViewModelProviders.of(this, camVMFactory).get(CameraViewModel::class.java)
        cameraViewModel.setUp()
    }

    private fun changeCamera() {
        cameraViewModel.changeCamera()
    }

    private fun updateZoom(): (SeekBar, Int) -> Unit = { seekBar: SeekBar, progress: Int ->
        fotoapparat.setZoom(progress / seekBar.max.toFloat())
    }

    private fun takePicture(): () -> Unit = {
        val photoResult = fotoapparat
                .autoFocus()
                .takePicture()

        val file = CameraHelper.generateTimeStampPhotoFile()

        photoResult.saveToFile(file!!)

        photoResult
                .toBitmap()
                .whenAvailable { bitmapPhoto ->
                    this.runOnUiThread({
                        photoAdapter.notifyDataSetChanged()
                    })
                }

        Log.i(LOGGING_TAG, "New photo captured. saved at: ${file.path}")
        cameraViewModel.addFilePath(file.path)
    }

    override fun onStart() {
        super.onStart()
        if (cameraPermissionsGranted) {
            fotoapparat.start()
            adjustViewsVisibility()
        }
    }

    override fun onStop() {
        super.onStop()
        if (cameraPermissionsGranted) {
            fotoapparat.stop()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissionsDelegate.resultGranted(requestCode, permissions, grantResults)) {
            cameraPermissionsGranted = true
            fotoapparat.start()
            adjustViewsVisibility()
            cameraView.visibility = View.VISIBLE
        }
    }

    private fun adjustViewsVisibility() {
        fotoapparat.getCapabilities()
                .whenAvailable { capabilities ->
                    capabilities
                            ?.let {
                                zoomSeekBar.visibility = if (it.zoom is Zoom.VariableZoom) View.VISIBLE else View.GONE
                                torchSwitch.visibility = if (it.flashModes.contains(Flash.Torch)) View.VISIBLE else View.GONE
                            }
                            ?: Log.e(LOGGING_TAG, "Couldn't obtain capabilities.")
                }

        switchCamera.visibility = if (fotoapparat.isAvailable(front())) View.VISIBLE else View.GONE
    }

}


