package edu.ramapo.btunney.quackchat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GestureDetectorCompat
import edu.ramapo.btunney.quackchat.views.CameraPreview
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val DEBUG_TAG = "Gestures"

class CameraActivity : AppCompatActivity() {

    private lateinit var mDetector: GestureDetectorCompat

    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null

    val MEDIA_TYPE_IMAGE = 1
    val MEDIA_TYPE_VIDEO = 2


    // Used for onRequestPermissionsResult callback when checking for permissions
    private val PERMISSION_USE_CAMERA = 4000

    // TODO: NO
    private val mPicture = Camera.PictureCallback { data, _ ->
        val pictureFile: File = getOutputMediaFile(MEDIA_TYPE_IMAGE) ?: run {
            Log.d("@@@@", ("Error creating media file, check storage permissions"))
            return@PictureCallback
        }

        try {
            val fos = FileOutputStream(pictureFile)
            fos.write(data)
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.d("@@@@", "File not found: ${e.message}")
        } catch (e: IOException) {
            Log.d("@@@@", "Error accessing file: ${e.message}")
        }
    }


    /** Create a File for saving an image or video */
    private fun getOutputMediaFile(type: Int): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        val mediaStorageDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp"
        )

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        mediaStorageDir.apply {
            if (!exists()) {
                if (!mkdirs()) {
                    Log.d("MyCameraApp", "failed to create directory")
                    return null
                }
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return when (type) {
            MEDIA_TYPE_IMAGE -> {
                File("${mediaStorageDir.path}${File.separator}IMG_$timeStamp.jpg")
            }
            MEDIA_TYPE_VIDEO -> {
                File("${mediaStorageDir.path}${File.separator}VID_$timeStamp.mp4")
            }
            else -> null
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // Request permissions to use camera to take pictures and record video
        // If accepted or previously accepted, camera stream will be displayed on the activity
//        requestCameraPermissions()

        // Set the swipe detector to swipe to FriendList activity
//        mDetector = GestureDetectorCompat(this, MyGestureListener())


    }

    /**
     * When the activity is paused, free the camera object so we don't run into issues
     *
     */
    override fun onPause() {
        super.onPause()
        if (mCamera != null) {
            mCamera?.stopPreview()
            camera_preview.removeView(mPreview) // ???? maybe
            mCamera?.release()
            mCamera = null
        }
    }


    /**
     * When activity is resumed, camera should be shown again
     *
     */
    override fun onResume() {
        super.onResume()
//        super.onResume()
//        val numCams = Camera.getNumberOfCameras()
//        if (numCams > 0) {
//            try {
//                mCamera = Camera.open(0)
//                mCamera?.startPreview()
//            } catch (ex: RuntimeException) {
//                Toast.makeText(this, "Camera not found", Toast.LENGTH_LONG).show()
//            }
//        }
        requestCameraPermissions()
//        displayCameraPreview()
    }

    /**
     * Detect when user swipes left and right
     *
     * @param event
     * @return
     */
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        // Send to Friend activity
//        if (mDetector.onTouchEvent(event)) {
//            runOnUiThread {
//                Runnable {
//                    val intent = Intent(this, FriendListActivity::class.java)
//                    startActivity(intent)
//                }.run()
//            }
//        }
//        return super.onTouchEvent(event)
//    }


    /**
     * Ask user to accept permissions to take pictures and record videos
     *
     */
    private fun requestCameraPermissions() {
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_USE_CAMERA)
        } else {
            // Permission has already been granted

            // Display preview of camera on activity
            displayCameraPreview()
        }
    }

    /**
     * The callback for when a permission request is accepted or denied
     *
     * @param requestCode the code defined for the permission (PERMISSION_USE_CAMERA)
     * @param permissions list of permissions being requested
     * @param grantResults list of the result of each permission response
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_USE_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // camera-related task you need to do.
                    displayCameraPreview()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Camera disabled, check permissions", Toast.LENGTH_LONG).show()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


    /**
     * Checks if a list of permissions is granted
     *
     * @param context
     * @param permissions
     * @return
     */
    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }


    /**
     * Show the camera stream within the FrameLayout on the activity
     *
     */
    private fun displayCameraPreview() {

        Log.d("@Camera activity", "got em")

        // Create an instance of Camera
        mCamera = getCameraInstance()

        mPreview = mCamera?.let {
            // Create our Preview view
            CameraPreview(this, it)
        }

        // Set the Preview view as the content of our activity.
        mPreview?.also {
            val preview: FrameLayout = findViewById(R.id.camera_preview)
            preview.addView(it)
        }

    }

    /** A safe way to get an instance of the Camera object. */
    private fun getCameraInstance(): Camera? {
        return try {
            Camera.open() // attempt to get a Camera instance
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
            null // returns null if camera is unavailable
        }
    }


    private class MyGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(event: MotionEvent): Boolean {
            Log.d(DEBUG_TAG, "onDown: $event")
            return true
        }

        override fun onFling(
                event1: MotionEvent,
                event2: MotionEvent,
                velocityX: Float,
                velocityY: Float
        ): Boolean {
            Log.d(DEBUG_TAG, "onFling: $event1 $event2")
            return true
        }
    }


    /**
     * Go to Settings activity when setting button is clicked
     *
     * @param view
     */
    fun settingsOnClick(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
//        finish()
    }

    /**
     * Take a picture when the take picture button is clicked
     *
     * @param view
     */
    fun takePictureOnClick(view: View) {
        try {
            mCamera!!.takePicture(null, null, mPicture)
        } catch (e: Exception) {
            println(e.message)
        }
    }




}
