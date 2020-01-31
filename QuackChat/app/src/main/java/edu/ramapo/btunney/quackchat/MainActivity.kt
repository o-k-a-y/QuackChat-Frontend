package edu.ramapo.btunney.quackchat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testCamera()
    }


    @SuppressLint("MissingPermission")
    fun testCamera() : Unit {
        Log.d(object : Any() {

        }.javaClass.enclosingMethod?.name, "oh")


        // Create pointer to camera daemon
        val manager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {
                val chars: CameraCharacteristics = manager.getCameraCharacteristics(cameraId!!)
                // Do something with the characteristics

                // Does the camera have a forwards facing lens?
                val facing = chars[CameraCharacteristics.LENS_FACING]
                Log.d("is facing", facing.toString())
            }

        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        val cameraIds = manager.getCameraIdList()

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            for (cameraId in cameraIds) {
                Log.d("id", cameraId.toString())
                manager.openCamera(cameraId, object: CameraDevice.StateCallback() {
                    /**
                     * The method called when a camera device has finished opening.
                     *
                     *
                     * At this point, the camera device is ready to use, and
                     * [CameraDevice.createCaptureSession] can be called to set up the first capture
                     * session.
                     *
                     * @param camera the camera device that has become opened
                     */
                    override fun onOpened(camera: CameraDevice) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        
                    }

                    /**
                     * The method called when a camera device is no longer available for
                     * use.
                     *
                     *
                     * This callback may be called instead of [.onOpened]
                     * if opening the camera fails.
                     *
                     *
                     * Any attempt to call methods on this CameraDevice will throw a
                     * [CameraAccessException]. The disconnection could be due to a
                     * change in security policy or permissions; the physical disconnection
                     * of a removable camera device; or the camera being needed for a
                     * higher-priority camera API client.
                     *
                     *
                     * There may still be capture callbacks that are invoked
                     * after this method is called, or new image buffers that are delivered
                     * to active outputs.
                     *
                     *
                     * The default implementation logs a notice to the system log
                     * about the disconnection.
                     *
                     *
                     * You should clean up the camera with [CameraDevice.close] after
                     * this happens, as it is not recoverable until the camera can be opened
                     * again. For most use cases, this will be when the camera again becomes
                     * [available][CameraManager.AvailabilityCallback.onCameraAvailable].
                     *
                     *
                     * @param camera the device that has been disconnected
                     */
                    override fun onDisconnected(camera: CameraDevice) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    /**
                     * The method called when a camera device has encountered a serious error.
                     *
                     *
                     * This callback may be called instead of [.onOpened]
                     * if opening the camera fails.
                     *
                     *
                     * This indicates a failure of the camera device or camera service in
                     * some way. Any attempt to call methods on this CameraDevice in the
                     * future will throw a [CameraAccessException] with the
                     * [CAMERA_ERROR][CameraAccessException.CAMERA_ERROR] reason.
                     *
                     *
                     *
                     * There may still be capture completion or camera stream callbacks
                     * that will be called after this error is received.
                     *
                     *
                     * You should clean up the camera with [CameraDevice.close] after
                     * this happens. Further attempts at recovery are error-code specific.
                     *
                     * @param camera The device reporting the error
                     * @param error The error code.
                     *
                     * @see .ERROR_CAMERA_IN_USE
                     *
                     * @see .ERROR_MAX_CAMERAS_IN_USE
                     *
                     * @see .ERROR_CAMERA_DISABLED
                     *
                     * @see .ERROR_CAMERA_DEVICE
                     *
                     * @see .ERROR_CAMERA_SERVICE
                     */
                    override fun onError(camera: CameraDevice, error: Int) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }


                }, null)
            }
        }

    }

}
