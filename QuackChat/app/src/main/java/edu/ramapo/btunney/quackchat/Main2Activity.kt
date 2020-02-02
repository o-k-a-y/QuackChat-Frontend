//package edu.ramapo.btunney.quackchat
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.pm.PackageManager
//import android.graphics.ImageFormat
//import android.hardware.camera2.*
//import android.media.ImageReader
//import android.os.Bundle
//import android.os.Handler
//import android.util.Log
//import android.util.Size
//import android.view.Surface
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import java.util.*
//import java.util.Arrays.asList
//
//
//class Main2Activity : AppCompatActivity() {
//    private lateinit var cameraDevice: CameraDevice
//    private lateinit var imageReader: ImageReader
//    private lateinit var backgroundHandler: Handler
//    private var cameraId: String? = null
//    private lateinit var cameraCaptureSession: CameraCaptureSession
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//
//        // Such great code
//        getCameraPermission()
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            testCamera()
//        } else {
//            getCameraPermission()
//
//        }
//
//    }
//
//    private fun getCameraPermission() {
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.CAMERA)
//            != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
//
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    fun testCamera() : Unit {
//        Log.d(object : Any() {
//
//        }.javaClass.enclosingMethod?.name, "oh")
//
//
//        // Create pointer to camera daemon
//        val manager = getSystemService(CAMERA_SERVICE) as CameraManager
//
//        // Print which way each camera is facing
////        try {
////            for (cameraId in manager.cameraIdList) {
////                val chars: CameraCharacteristics = manager.getCameraCharacteristics(cameraId!!)
////                // Do something with the characteristics
////
////                // Does the camera have a forwards facing lens?
////                val facing = chars[CameraCharacteristics.LENS_FACING]
////                Log.d("is facing", facing.toString())
////            }
////
////        } catch (e: CameraAccessException) {
////            e.printStackTrace()
////        }
//
//        val cameraIds = manager.getCameraIdList()
//
//
//        for (cameraId in cameraIds) {
//
//            var characteristics: CameraCharacteristics = manager.getCameraCharacteristics(cameraId);
//
//            if (characteristics.get(CameraCharacteristics.LENS_FACING) != CameraCharacteristics.LENS_FACING_FRONT) {
//                continue;
//            }
//
//            val map = characteristics[CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP]
//                ?: continue
//
////            this.cameraId = cameraId
////            // For still image captures, we use the largest available size.
////            // For still image captures, we use the largest available size.
////            val largest: Size = Collections.max(
////                asList(map.getOutputSizes(ImageFormat.JPEG)),
////                object: CompareSizesByArea() {
////
////                }
////            )
////            imageReader = ImageReader.newInstance(
////                largest.width, largest.height,
////                ImageFormat.JPEG,  /*maxImages*/2
////            )
////            imageReader.setOnImageAvailableListener(
////                ImageReader.OnImageAvailableListener, mBackgroundHandler
////            )
//
//            Log.d("id", cameraId.toString())
//            manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
//                /**
//                 * The method called when a camera device has finished opening.
//                 *
//                 *
//                 * At this point, the camera device is ready to use, and
//                 * [CameraDevice.createCaptureSession] can be called to set up the first capture
//                 * session.
//                 *
//                 * @param camera the camera device that has become opened
//                 */
//                override fun onOpened(camera: CameraDevice) {
//                    cameraDevice = camera
//                    createCaptureSession()
//                }
//
//                /**
//                 * The method called when a camera device is no longer available for
//                 * use.
//                 *
//                 *
//                 * This callback may be called instead of [.onOpened]
//                 * if opening the camera fails.
//                 *
//                 *
//                 * Any attempt to call methods on this CameraDevice will throw a
//                 * [CameraAccessException]. The disconnection could be due to a
//                 * change in security policy or permissions; the physical disconnection
//                 * of a removable camera device; or the camera being needed for a
//                 * higher-priority camera API client.
//                 *
//                 *
//                 * There may still be capture callbacks that are invoked
//                 * after this method is called, or new image buffers that are delivered
//                 * to active outputs.
//                 *
//                 *
//                 * The default implementation logs a notice to the system log
//                 * about the disconnection.
//                 *
//                 *
//                 * You should clean up the camera with [CameraDevice.close] after
//                 * this happens, as it is not recoverable until the camera can be opened
//                 * again. For most use cases, this will be when the camera again becomes
//                 * [available][CameraManager.AvailabilityCallback.onCameraAvailable].
//                 *
//                 *
//                 * @param camera the device that has been disconnected
//                 */
//                override fun onDisconnected(camera: CameraDevice) {
//                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                    camera.close()
//                }
//
//                /**
//                 * The method called when a camera device has encountered a serious error.
//                 *
//                 *
//                 * This callback may be called instead of [.onOpened]
//                 * if opening the camera fails.
//                 *
//                 *
//                 * This indicates a failure of the camera device or camera service in
//                 * some way. Any attempt to call methods on this CameraDevice in the
//                 * future will throw a [CameraAccessException] with the
//                 * [CAMERA_ERROR][CameraAccessException.CAMERA_ERROR] reason.
//                 *
//                 *
//                 *
//                 * There may still be capture completion or camera stream callbacks
//                 * that will be called after this error is received.
//                 *
//                 *
//                 * You should clean up the camera with [CameraDevice.close] after
//                 * this happens. Further attempts at recovery are error-code specific.
//                 *
//                 * @param camera The device reporting the error
//                 * @param error The error code.
//                 *
//                 * @see .ERROR_CAMERA_IN_USE
//                 *
//                 * @see .ERROR_MAX_CAMERAS_IN_USE
//                 *
//                 * @see .ERROR_CAMERA_DISABLED
//                 *
//                 * @see .ERROR_CAMERA_DEVICE
//                 *
//                 * @see .ERROR_CAMERA_SERVICE
//                 */
//                override fun onError(camera: CameraDevice, error: Int) {
//                    die()
//
//                }
//
//
//            }, null) // TODO("Change null to a handler which uses a new thread")
//        }
//
//    }
//
//    /**
//     * Compares two {@code Size}s based on their areas.
//     */
//    internal class CompareSizesByArea : Comparator<Size?> {
//        override fun compare(lhs: Size?, rhs: Size?): Int { // We cast here to ensure the multiplications won't overflow
//            return java.lang.Long.signum(lhs!!.width.toLong() * lhs.height -
//                    rhs!!.width.toLong() * rhs!!.height)
//        }
//    }
//}
//
//private fun die() {
//    Log.d("oh", "oh")
//}
//
///**
// * Creates a capture session for a given camera device
// *
// */
////    private fun createCaptureSession() {
////        val outputSurfaces = LinkedList<Surface>()
////        outputSurfaces.add(imageReader.surface)
////
////        try {
////            cameraDevice.createCaptureSession(outputSurfaces, object: CameraCaptureSession.StateCallback() {
////                /**
////                 * This method is called if the session cannot be configured as requested.
////                 *
////                 *
////                 * This can happen if the set of requested outputs contains unsupported sizes,
////                 * or too many outputs are requested at once.
////                 *
////                 *
////                 * The session is considered to be closed, and all methods called on it after this
////                 * callback is invoked will throw an IllegalStateException. Any capture requests submitted
////                 * to the session prior to this callback will be discarded and will not produce any
////                 * callbacks on their listeners.
////                 *
////                 * @param session the session returned by [CameraDevice.createCaptureSession]
////                 */
////                override fun onConfigureFailed(session: CameraCaptureSession) {
////                    // TODO("implement error handling")
////                }
////
////                /**
////                 * This method is called when the camera device has finished configuring itself, and the
////                 * session can start processing capture requests.
////                 *
////                 *
////                 * If there are capture requests already queued with the session, they will start
////                 * processing once this callback is invoked, and the session will call [.onActive]
////                 * right after this callback is invoked.
////                 *
////                 *
////                 * If no capture requests have been submitted, then the session will invoke
////                 * [.onReady] right after this callback.
////                 *
////                 *
////                 * If the camera device configuration fails, then [.onConfigureFailed] will
////                 * be invoked instead of this callback.
////                 *
////                 * @param session the session returned by [CameraDevice.createCaptureSession]
////                 */
////                override fun onConfigured(session: CameraCaptureSession) {
////                    cameraCaptureSession = session
////                }
////
////
////            }, null)
////        } catch (e: CameraAccessException) {
////            e.printStackTrace()
////        }
////    }
//
//}
//
