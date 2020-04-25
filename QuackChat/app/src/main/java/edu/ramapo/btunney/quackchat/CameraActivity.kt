package edu.ramapo.btunney.quackchat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import edu.ramapo.btunney.quackchat.views.CameraPreview

private const val DEBUG_TAG = "Gestures"

class CameraActivity : AppCompatActivity() {

    private lateinit var mDetector: GestureDetectorCompat

    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null

    // Used for onRequestPermissionsResult callback when checking for permissions
    private val PERMISSION_USE_CAMERA = 4000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // Request permissions to use camera to take pictures and record video
        // If accepted or previously accepted, camera stream will be displayed on the activity
        requestCameraPermissions()

        // Set the swipe detector to swipe to FriendList activity
        mDetector = GestureDetectorCompat(this, MyGestureListener())

    }


    /**
     * Detect when user swipes left and right
     *
     * @param event
     * @return
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Send to Friend activity
        if (mDetector.onTouchEvent(event)) {
            runOnUiThread {
                Runnable {
                    val intent = Intent(this, FriendListActivity::class.java)
                    startActivity(intent)
                }.run()
            }
        }
        return super.onTouchEvent(event)
    }


    /**
     * Ask user to accept permissions to take pictures and record videos
     *
     */
    private fun requestCameraPermissions() {
        // TODO: temp way to check for permissions (VERY BAD)
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this, "Camera disabled, check permissions", Toast.LENGTH_LONG).show()
                val permissions = arrayOf(android.Manifest.permission.CAMERA)
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_USE_CAMERA)
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CAMERA),
                        PERMISSION_USE_CAMERA)

                // PERMISSION_USE_CAMERA is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted

            // Display preview of camera on activity
            displayCameraPreview()
        }
    }

    // TODO: temp
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

    // TODO: temp
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

    fun settingsOnClick(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
//        finish()
    }

}
