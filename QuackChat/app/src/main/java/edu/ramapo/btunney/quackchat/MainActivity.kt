package edu.ramapo.btunney.quackchat

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import kotlinx.android.synthetic.main.activity_main.*


// Needed for glide to work
@GlideModule
class MyAppGlideModule : AppGlideModule()


/**
 * This activity lives for the purpose of checking if the user is already authenticated.
 * They are authenticated if:
 *      1. They have a cookie stored in SharedPreferences that hasn't expired
 *      2. The cookie matches the session stored on the MongoDB backend database
 *      3. The NodeJS backend is active and can talk with the MongoDB database
 *      4. The network request to check if the user is authenticated succeeds
 *
 * If the user is authenticated they will be brought to the CameraActivity.
 * Otherwise they will be forced to log in again. Note that if the network is bad they
 * will probably never be able to log in
 *
 */
class MainActivity : AppCompatActivity() {

    /**
     * Show loading screen while making network request
     * Check if user is authenticated
     * If they are, bring to CameraActivity
     * otherwise bring to LoginSignUpActivity
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Disable screen rotations
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        // Hide the top bar in activity
        if (supportActionBar != null)
            supportActionBar?.hide()

        // Set loading gif (duck walking gif)
        Glide.with(this)
                .asGif()
                .load("file:///android_asset/loading.gif")
//                .load("https://i.imgur.com/0kvtMLE.gif")
                .into(loadingGifimageView)

        // Set NetworkRequester's context to use application's context (very bad)
        NetworkRequester.setContext(applicationContext)

        // Add auth token if it exists
        NetworkRequester.addAuthToken()

        val mainRef = this

        // Ask server if user is authenticated
        NetworkRequester.authenticate(ServerRoutes.AUTH, object: NetworkCallback {
            /**
             * User is not authenticated, ask them to log in or sign up
             *
             * @param failureCode
             */
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                // Go to login/signup page
                Log.d("fail code", failureCode.toString())

                startLoginSignUpActivity()
            }

            /**
             * User is authenticated, go to CameraActivity
             *
             */
            override fun onSuccess(data: Any?) {
                // Go to camera activity
                startCameraActivity()
            }

        })


    }

    /**
     * Bring user to CameraActivity
     *
     */
    private fun startCameraActivity() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
        finish()

        // Kotlin style
//        Intent(this, CameraActivity::class.java).also { startActivity(it) }
//        finish()
    }

    /**
     * Bring user to Login/Signup Activity
     *
     */
    private fun startLoginSignUpActivity() {
        val intent = Intent(this, LoginSignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

}
