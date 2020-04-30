package edu.ramapo.btunney.quackchat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.hardware.Camera.Parameters.SCENE_MODE_PORTRAIT
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
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
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


private const val DEBUG_TAG = "Gestures"

class CameraActivity : AppCompatActivity() {

    private lateinit var mDetector: GestureDetectorCompat

    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null
    private var mediaRecorder: MediaRecorder? = null

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

        // Reset the camera and its preview
        resetCamera()

        // TODO: Show preview of picture before sending it

        // TODO: VERY TEMP ARRAY OF FRIENDS (HARDCODED)
        val friends = arrayOf("joe", "bob", "me")
        // TODO: TEMP, sending picture straight to backend
        NetworkRequester.sendMessage(ServerRoutes.SEND_MESSAGE, friends, base64EncodedData, MessageType.PICTURE, object: NetworkCallback {
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
            cameraPreview.removeView(mPreview) // ???? maybe
            mCamera?.release()
            mCamera = null
        }

        // TODO: TEMP
        // Release MediaRecorder if used
        releaseMediaRecorder()
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
//                    displayCameraPreview()
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
            cameraPreview.removeView(mPreview) // ???? maybe
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

        val params: Camera.Parameters? = mCamera?.parameters
//        params?.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
//        mCamera?.parameters = params
        params?.sceneMode = SCENE_MODE_PORTRAIT
        mCamera?.setDisplayOrientation(90)

        mPreview = mCamera?.let {
            // Create our Preview view
            CameraPreview(this, it)
        }

        // Set the Preview view as the content of our activity.
        mPreview?.also {
            val preview: FrameLayout = findViewById(R.id.cameraPreview)
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

    // TODO: BEGIN TEMP
    /** Create a file Uri for saving an image or video */
    private fun getOutputMediaFileUri(type: Int): Uri {
        return Uri.fromFile(getOutputMediaFile(type))
    }

    /** Create a File for saving an image or video */
    // Code taken from https://developer.android.com/guide/topics/media/camera
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


    private fun prepareVideoRecorder(): Boolean {
        mediaRecorder = MediaRecorder()

        mCamera?.let { camera ->
            // Step 1: Unlock and set camera to MediaRecorder
            camera?.unlock()

            mediaRecorder?.run {
                setCamera(camera)

                // Step 2: Set sources
                setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
                setVideoSource(MediaRecorder.VideoSource.CAMERA)

                // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
                setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH))

                // Step 4: Set output file
                setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString())

                // Step 5: Set the preview output
                setPreviewDisplay(mPreview?.holder?.surface)

                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
                setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT)


                // Step 6: Prepare configured MediaRecorder
                return try {
                    prepare()
                    true
                } catch (e: IllegalStateException) {
                    Log.d("@VIDEO", "IllegalStateException preparing MediaRecorder: ${e.message}")
                    releaseMediaRecorder()
                    false
                } catch (e: IOException) {
                    Log.d("@VIDEO", "IOException preparing MediaRecorder: ${e.message}")
                    releaseMediaRecorder()
                    false
                }
            }

        }
        return false
    }

    private fun releaseMediaRecorder() {
        mediaRecorder?.reset() // clear recorder configuration
        mediaRecorder?.release() // release the recorder object
        mediaRecorder = null
        mCamera?.lock() // lock camera for later use
    }


    // TODO: END TEMP


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
