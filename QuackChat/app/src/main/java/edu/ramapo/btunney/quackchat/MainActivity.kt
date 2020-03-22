package edu.ramapo.btunney.quackchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import edu.ramapo.btunney.quackchat.Networking.NetworkCallback
import edu.ramapo.btunney.quackchat.Networking.NetworkRequester

class MainActivity : AppCompatActivity() {

    /**
     * TODO
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainRef = this

        // Ask server if user is authenticated
        NetworkRequester.authenticate(object: NetworkCallback {
            /**
             * User is not authenticated
             *
             * @param failureCode
             */
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                // go to loginsignup page
                Log.d("fail code", failureCode.toString())
                runOnUiThread {
                    Runnable {
                        startLoginSignupActivity()
                    }.run()
                }
            }

            /**
             * User is authenticated
             *
             */
            override fun onSuccess() {
                // go to camera activity
                println()

                runOnUiThread {
                    Runnable {
                        startCameraActivity()
                    }.run()
                }
            }

        })


        // TODO ask server if user is authenticated and change this
//        val authenticated = false
//
//        if (!authenticated) {
//            startLoginSignupActivity()
//        } else {
//            startCameraActivity()
//        }
    }

    /**
     * TODO
     *
     */
    private fun startCameraActivity() {
        val intent = Intent(this, CameraActivity::class.java)

        startActivity(intent)

        finish()
    }

    /**
     * TODO
     *
     */
    private fun startLoginSignupActivity() {
        val intent = Intent(this, LoginSignUpActivity::class.java)

        startActivity(intent)

        finish()
    }

}
