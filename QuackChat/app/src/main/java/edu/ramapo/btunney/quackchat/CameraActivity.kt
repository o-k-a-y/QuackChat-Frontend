package edu.ramapo.btunney.quackchat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.VideoCapture
import androidx.camera.view.CameraView
import androidx.core.app.ActivityCompat
import edu.ramapo.btunney.quackchat.networking.MessageType
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.util.concurrent.Executor

/**
 * The main feature of this Activity is to allow the user to take pictures and record videos
 * to send to friends.
 * If the user gives camera permission, they will see a camera preview (what the camera sees).
 * If the user gives record audio permission, they can record videos with sound.
 * The user can send these pictures and videos to friends (if they have any) which will send them a message
 * and can be viewed in the MessageActivity.
 *
 * Along with the camera, the user can also access their account setting by clicking the settings button
 * which will take them to the SettingsActivity.
 * They can also view their messages by clicking the message button which will take them to the FriendListActivity.
 * There they can see any messages their friends have sent them (if they have any friends)
 *
 */
class CameraActivity : AppCompatActivity() {

    private val PERMISSION_USE_CAMERA = 4000
//    private lateinit var mDetector: GestureDetectorCompat
    private val mPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide the top bar in activity
        if (supportActionBar != null)
            supportActionBar?.hide()

        setContentView(R.layout.activity_camera)

        // TODO: broken
//        mDetector = GestureDetectorCompat(this, MyGestureListener())
    }

    /**
     * When activity is paused, we should stop the camera if it's currently recording otherwise bad voodoo will happen
     *
     */
    override fun onPause() {
        if (viewCamera.isRecording) {
            viewCamera.stopRecording()
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            super.onPause()
        } else super.onPause()
    }

    /**
     * When activity is resumed, camera should be shown again
     * onResume is called right after onCreate
     *
     */
    override fun onResume() {
        super.onResume()
        requestCameraPermissions()

        startRecordingButton.visibility = View.GONE
        stopRecordingButton.visibility = View.GONE
        takePictureButton.visibility = View.VISIBLE

        changeCaptureModeButton.setImageResource(R.drawable.ic_videocam_off_outline_24px)


//        setRecordButtonVisible(true)

    }

//    private class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
//        override fun onDown(event: MotionEvent): Boolean {
//            Log.d("Motion event", "onDown: $event")
//            return true
//        }
//
//        override fun onFling(
//                event1: MotionEvent,
//                event2: MotionEvent,
//                velocityX: Float,
//                velocityY: Float
//        ): Boolean {
//            Log.d("Motion event", "onFling: $event1 $event2")
//            return true
//        }
//    }


//    /**
//     * Detect when user swipes left and right
//     *
//     * @param event
//     * @return
//     */
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
        if (!hasPermissions(this, mPermissions)) {
            ActivityCompat.requestPermissions(this, mPermissions, PERMISSION_USE_CAMERA)
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
    @SuppressLint("RestrictedApi")
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
//
//
    /**
     * Checks if a list of permissions is granted
     *
     * @param context application context
     * @param permissions list of permissions to grant
     * @return
     */
    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }


    /**
     * Show the camera stream within the CameraView on the activity
     *
     */
    private fun displayCameraPreview() {
        viewCamera.bindToLifecycle(this)
    }


    /**
     * Set the capture mode for the camera
     * CaptureMode.IMAGE for pictures
     * CaptureMode.VIDEO for recording
     *
     * @param captureMode the mode for capture
     */
    private fun setCaptureMode(captureMode: CameraView.CaptureMode) {
        viewCamera.captureMode = captureMode
    }


    /**
     * Goes to the SendMediaActivity with the media type in the intent
     *
     * @param messageType type of media to send (picture/video)
     */
    private fun sendMedia(messageType: MessageType) {
        val intent = Intent(applicationContext, SendMediaActivity::class.java)
        intent.putExtra(SendMediaActivity.MEDIATYPE, messageType.type)
        startActivity(intent)
    }

    /**
     * Set the visibility of the start and stop recording buttons
     * If true is passed in, start will be visible while stop will not and vice versa
     *
     * @param doIt whether or not to make the start button visible
     */
    private fun setRecordButtonVisible(doIt: Boolean) {
        if (doIt) {
            startRecordingButton.visibility = View.VISIBLE
            stopRecordingButton.visibility = View.GONE
        } else {
            startRecordingButton.visibility = View.GONE
            stopRecordingButton.visibility = View.VISIBLE
        }
    }

    /**
     * Go to Settings activity when settings button is clicked
     *
     * @param view settings button
     */
    fun settingsOnClick(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
//        finish()
    }


    /**
     * Go to FriendList activity when friend list button is clicked
     *
     * @param view friend list button
     */
    fun friendListOnClick(view: View) {
        val intent = Intent(this, FriendListActivity::class.java)
        intent.putExtra(FriendListActivity.DELETEFRIEND, false)
        startActivity(intent)
    }


    /**
     * Take a picture when the take picture button is clicked
     *
     * @param view take photo button
     */
    fun takePictureOnClick(view: View) {
        // Make sure capture mode is for images
        setCaptureMode(CameraView.CaptureMode.IMAGE)
        try {
            viewCamera.takePicture(
                    File(applicationContext.cacheDir, "picture").absoluteFile,
                    object : Executor {
                        override fun execute(command: Runnable) {
                            command.run()
                        }

                    },
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            sendMedia(MessageType.PICTURE)
                        }

                        override fun onError(exception: ImageCaptureException) {
                            println("image cant be saved")
                        }
                    }
            )
        } catch (e: Exception) {
            println(e.message)
        }
    }

    /**
     * Start recording a video
     *
     * @param view start recording button
     */
    fun startRecordingOnClick(view: View) {
        setRecordButtonVisible(false)

        // Make sure capture mode is for images
        setCaptureMode(CameraView.CaptureMode.VIDEO)
//        view_camera.setCaptureMode(CameraView.CaptureMode.VIDEO)
        viewCamera.startRecording(File(applicationContext.cacheDir, "video"),
                object : Executor {
                    override fun execute(command: Runnable) {
                        command.run()
                    }

                },
                object : VideoCapture.OnVideoSavedCallback {
                    override fun onVideoSaved(file: File) {
                        sendMedia(MessageType.VIDEO)

//                        // TODO: this should be in a different activity
//                        val byteArray = file.readBytes()
//                        // Base64 encode data
//                        val base64EncodedData = Base64.encodeToString(byteArray, Base64.DEFAULT)
//                        Log.d("picture", base64EncodedData)
                    }

                    override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                        Toast.makeText(applicationContext,"Failed to record video, check log", Toast.LENGTH_LONG).show()
                        Log.d("@Camera error", message)
                        Log.d("@Camera error", videoCaptureError.toString())
                        Log.d("@Camera error", cause?.message)
                    }

                })
    }

    /**
     * Stop recording the video
     *
     * @param view
     */
    fun stopRecordingOnClick(view: View) {
        viewCamera.stopRecording()
    }

    /**
     * Switch mode of capture from picture to video or vice versa
     *
     * @param view
     */
    fun switchCaptureMode(view: View) {
        if (takePictureButton.visibility == View.VISIBLE) {
            changeCaptureModeButton.setImageResource(R.drawable.ic_videocam_outline_24px)

            takePictureButton.visibility = View.GONE
            startRecordingButton.visibility = View.VISIBLE
            stopRecordingButton.visibility = View.GONE
        } else {
            changeCaptureModeButton.setImageResource(R.drawable.ic_videocam_off_outline_24px)
            
            takePictureButton.visibility = View.VISIBLE
            startRecordingButton.visibility = View.GONE
            stopRecordingButton.visibility = View.GONE
        }
    }

}
