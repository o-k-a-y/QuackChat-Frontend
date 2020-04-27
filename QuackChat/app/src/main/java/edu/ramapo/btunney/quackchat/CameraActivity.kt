package edu.ramapo.btunney.quackchat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GestureDetectorCompat
import edu.ramapo.btunney.quackchat.networking.MessageType
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import edu.ramapo.btunney.quackchat.views.CameraPreview
import kotlinx.android.synthetic.main.activity_camera.*


private const val DEBUG_TAG = "Gestures"

class CameraActivity : AppCompatActivity() {

    private lateinit var mDetector: GestureDetectorCompat

    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null

    val MEDIA_TYPE_IMAGE = 1
    val MEDIA_TYPE_VIDEO = 2


    // Used for onRequestPermissionsResult callback when checking for permissions
    private val PERMISSION_USE_CAMERA = 4000


    /**
     * Handles what happens when a picture is taken
     */
    private val mPicture = Camera.PictureCallback { data, _ ->
        Log.d("what is data", data.toString())

        // Base64 encode data
        val base64EncodedData = Base64.encodeToString(data, Base64.DEFAULT)
        Log.d("picture", base64EncodedData)

        var bm = BitmapFactory.decodeByteArray(data, 0, data.size)
        bm = rotateImage(bm, 90F)
        imageView2.setImageBitmap(bm)

        println(data.size)

        // Reset the camera and its preview
        resetCamera()

        // TODO: Show preview of picture before sending it

        // TODO: TEMP, sending picture straight to backend
        NetworkRequester.sendMessage(ServerRoutes.SEND_MESSAGE, "joe", base64EncodedData, MessageType.PICTURE, object: NetworkCallback {
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(data: Any?) {
                Log.d("@CameraAct", "picture message sent")
            }

        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // Set the swipe detector to swipe to FriendList activity
        mDetector = GestureDetectorCompat(this, MyGestureListener())

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
     * onResume is called right after onCreate
     *
     */
    override fun onResume() {
        super.onResume()
        requestCameraPermissions()
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
     * Every time we take a picture we must reset the camera and the preview
     *
     */
    private fun resetCamera() {
        if (mCamera != null) {
            mCamera?.stopPreview()
            camera_preview.removeView(mPreview) // ???? maybe
            mCamera?.release()
            mCamera = null
        }

        displayCameraPreview()
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


    /**
     * Rotate a bitmap x degrees
     * This is used when taking a picture because by default the image comes in landscape (horizontal)
     * We really only allow vertical images
     *
     * @param source
     * @param angle
     * @return
     */
    // TODO: make this in some ImageConvert model class?
    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
                matrix, true)
    }


}
