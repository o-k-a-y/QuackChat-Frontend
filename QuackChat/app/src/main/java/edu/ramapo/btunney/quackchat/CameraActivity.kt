package edu.ramapo.btunney.quackchat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
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
 * To take a picture, simply click the circle in the middle bottom of the screen, this will send the preview
 * of the image to the SendMediaActivity
 *
 * To take a video, click the video icon on the bottom left of the screen.
 * This will set the capture mode to video.
 * This video icon show what mode is currently being used. If there is a slash through it, video is disabled.
 * Then you can click the circle to start recording.
 * The circle button will have an inner red circle to indicate recording is in place.
 * To stop recording click it again.
 * The video preview will be sent to SendMediaActivity

 *
 * Along with the camera, the user can also access their account setting by clicking the settings button
 * which will take them to the SettingsActivity.
 * They can also view their messages by clicking the message button which will take them to the FriendListActivity.
 * There they can see any messages their friends have sent them (if they have any friends)
 *
 */
class CameraActivity : AppCompatActivity() {

    // Constant request code needed for requestPermissions()
    private val PERMISSION_USE_CAMERA = 4000

    // Permissions we need to use the app
    private val mPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Disable screen rotations
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        // Hide the top bar in activity
        if (supportActionBar != null)
            supportActionBar?.hide()

        setContentView(R.layout.activity_camera)
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
     * and set camera capture mode set picture mode
     * onResume is called right after onCreate
     *
     */
    override fun onResume() {
        super.onResume()
        requestCameraPermissions()

        // Default camera capture mode is picture mode
        startRecordingButton.visibility = View.GONE
        stopRecordingButton.visibility = View.GONE
        takePictureButton.visibility = View.VISIBLE
        changeCaptureModeButton.setImageResource(R.drawable.ic_videocam_off_outline_24px)
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

        // Save captured picture to a cache file when capture is successful is stopped
        // Send picture preview to SendMediaActivity
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

        // Save recorded video to a cache file when recording is stopped
        // Send video preview to SendMediaActivity
        viewCamera.startRecording(File(applicationContext.cacheDir, "video"),
                object : Executor {
                    override fun execute(command: Runnable) {
                        command.run()
                    }

                },
                object : VideoCapture.OnVideoSavedCallback {
                    override fun onVideoSaved(file: File) {
                        sendMedia(MessageType.VIDEO)
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
    fun switchCaptureModeOnClick(view: View) {
        // Set mode of capture to picture
        if (takePictureButton.visibility == View.VISIBLE) {
            changeCaptureModeButton.setImageResource(R.drawable.ic_videocam_outline_24px)

            takePictureButton.visibility = View.GONE
            startRecordingButton.visibility = View.VISIBLE
            stopRecordingButton.visibility = View.GONE
        } else {
            // Set mode of capture to video
            changeCaptureModeButton.setImageResource(R.drawable.ic_videocam_off_outline_24px)
            
            takePictureButton.visibility = View.VISIBLE
            startRecordingButton.visibility = View.GONE
            stopRecordingButton.visibility = View.GONE
        }
    }

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

}
