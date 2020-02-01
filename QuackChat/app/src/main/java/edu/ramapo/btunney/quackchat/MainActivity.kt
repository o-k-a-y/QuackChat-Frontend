package edu.ramapo.btunney.quackchat

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.hardware.camera2.*
import android.os.Bundle
import android.util.Log
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getCameraPermission()
        testCamera()
    }


    private fun getCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)

        }
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


        for (cameraId in cameraIds) {
            Log.d("id", cameraId.toString())
            manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
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
                    takePicture()
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
                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    camera.close()
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
                    die()

                }


            }, null) // TODO("Change null to a handler which uses a new thread")
        }

    }

    private fun die() {
        Log.d("oh", "oh")
    }

    private fun takePicture() {
        Log.d("oh", "oh")
    }

}
