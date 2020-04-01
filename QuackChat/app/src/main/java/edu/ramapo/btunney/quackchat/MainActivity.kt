package edu.ramapo.btunney.quackchat

import java.util.Date

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.room.*
import edu.ramapo.btunney.quackchat.dao.AppDatabase
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import okhttp3.Cookie


class MainActivity : AppCompatActivity() {

    /**
     * TODO
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Test Room DB
        val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "database-name"
        ).build()
        

        // Set NetworkRequester's context to use application's context (very bad)
        NetworkRequester.setContext(applicationContext)

        // Add auth token if it exists
        NetworkRequester.addAuthToken()

        val mainRef = this

        // Ask server if user is authenticated
        NetworkRequester.authenticate(ServerRoutes.AUTH, object: NetworkCallback {
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
                        val intent = Intent(mainRef, LoginSignUpActivity::class.java)

                        startActivityForResult(intent, 10)

                        finish()
//                        startLoginSignUpActivity()
                    }.run()
                }
            }

            /**
             * User is authenticated
             *
             */
            override fun onSuccess() {
                // go to camera activity

                runOnUiThread {
                    Runnable {
                        val intent = Intent(mainRef, CameraActivity::class.java)

                        startActivityForResult(intent, 11)

                        finish()
//                        startCameraActivity()
                    }.run()
                }
            }

        })


    }

    /**
     * TODO
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
     * TODO
     *
     */
    private fun startLoginSignUpActivity() {
        val intent = Intent(this, LoginSignUpActivity::class.java)

        startActivity(intent)

        finish()
    }

}
