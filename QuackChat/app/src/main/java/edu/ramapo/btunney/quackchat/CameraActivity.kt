package edu.ramapo.btunney.quackchat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.VideoCapture
import androidx.camera.view.CameraView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.isInvisible
import edu.ramapo.btunney.quackchat.networking.MessageType
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.util.concurrent.Executor


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

//        view_camera.bindToLifecycle(this)
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
     * @param context
     * @param permissions
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
//        view_camera.isEnabled = false
        view_camera.bindToLifecycle(this)
//        view_camera.setOnTouchListener(null)
//        view_camera.setOnClickListener(null)
//        view_camera.setOnKeyListener(null)
//        view_camera.isEnabled = false
//        view_camera.isEnabled = false
    }


    /**
     * Set the capture mode for the camera
     * CaptureMode.IMAGE for pictures
     * CaptureMode.VIDEO for recording
     *
     * @param captureMode the mode for capture
     */
    private fun setCaptureMode(captureMode: CameraView.CaptureMode) {
        view_camera.captureMode = captureMode
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
        startActivity(intent)
    }


    /**
     * Take a picture when the take picture button is clicked
     *
     * @param view take photo button
     */
    fun takePictureOnClick(view: View) {
        Log.d("Fuk", "ff")

        // Make sure capture mode is for images
        setCaptureMode(CameraView.CaptureMode.IMAGE)
        try {
            view_camera.takePicture(
                    File(applicationContext.cacheDir, "test").absoluteFile,
                    object : Executor {
                        override fun execute(command: Runnable) {
                            command.run()
                        }

                    },
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            println("image saved?")
                            val file = File(applicationContext.cacheDir, "test")
                            val filePath = file.absolutePath

                            // TODO: Bitmap if I needed to change from byte array
//                            val bitmap = BitmapFactory.decodeFile(filePath)
//
//                            runOnUiThread {
//                                Runnable {
//                                    imageButton.setImageBitmap(bitmap)
//                                }.run()
//                            }

                            val byteArray = file.readBytes()

                            // Base64 encode data
                            val base64EncodedData = Base64.encodeToString(byteArray, Base64.DEFAULT)
                            Log.d("picture", base64EncodedData)

                            // TODO: Show preview of picture before sending it

                            // TODO: VERY TEMP ARRAY OF FRIENDS (HARDCODED)
                            val friends = arrayOf("joe")
                            // TODO: TEMP, sending picture straight to backend
                            NetworkRequester.sendMessage(ServerRoutes.SEND_MESSAGE, friends, base64EncodedData, MessageType.PICTURE, object : NetworkCallback {
                                override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                                    TODO("Not yet implemented")
                                }

                                override fun onSuccess(data: Any?) {
                                    Log.d("@CameraAct", "picture message sent")
                                }

                            })
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
     * TODO
     *
     * @param view
     */
    fun startRecordingOnClick(view: View) {

        // Make sure capture mode is for images
        setCaptureMode(CameraView.CaptureMode.VIDEO)
//        view_camera.setCaptureMode(CameraView.CaptureMode.VIDEO)
        view_camera.startRecording(File(applicationContext.cacheDir, "video.mp4"),
                object : Executor {
                    override fun execute(command: Runnable) {
                        command.run()
                    }

                },
                object : VideoCapture.OnVideoSavedCallback {
                    override fun onVideoSaved(file: File) {
                        runOnUiThread {
                            Runnable {
                                val uri = FileProvider.getUriForFile(applicationContext, applicationContext.packageName + ".provider", file)
                                videoView.setVideoURI(uri)

                                view_camera.visibility = View.INVISIBLE

//                                val mediaController = MediaController(applicationContext)
//                                mediaController.setAnchorView(videoView)
//                                videoView.setMediaController(mediaController)


                                videoView.start()
                            }.run()
                        }
                        val byteArray = file.readBytes()
                        // Base64 encode data
                        val base64EncodedData = Base64.encodeToString(byteArray, Base64.DEFAULT)
                        Log.d("picture", base64EncodedData)
                    }

                    override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                        TODO("Not yet implemented")
                    }

                })
    }

    /**
     * TODO
     *
     * @param view
     */
    fun stopRecordingOnClick(view: View) {
        view_camera.stopRecording()
    }

}
