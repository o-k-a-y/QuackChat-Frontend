package edu.ramapo.btunney.quackchat

import java.util.Date

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

//        // Check that cookie exists in SharedPreferences
//        val sharedPreferences: SharedPreferences = getSharedPreferences("AuthLogin", Context.MODE_PRIVATE)
//        val cookieString: String? = sharedPreferences.getString("AuthToken", null)
//
//        // Get properties of cookie
//        val keys:List<String> = cookieString!!.split(";")
//        var name = keys[0]
//
//        val nameAndValue = name.split("=")
//        name = nameAndValue[0]
//        val value = nameAndValue[1]
//
//
//        var expiresAt = keys[1] // need to format date probably
//
//        val expire = expiresAt.split("=")
//        expiresAt = expire[1]
//
//        var path = keys[2] //
//        val pathh = path.split("=")
//        path = pathh[1]
//
//        val httpOnly = keys[3] // if value = "httponly" it should be a boolean set to true
//
//        var httponly = false
//
//        if (httpOnly == "httponly") {
//                httponly = true
//        }
//
//        var date = Date(expiresAt)
//        var time = date.time
//
//        // Make string into cookie
//        // Probably should move this to a NetworkRequester method
//        val sharedPrefCookie = Cookie.Builder()
//            .domain("52.55.108.86")
//            .name(name)
//            .value(value)
//            .expiresAt(time) // long
//            .path(path)
//            .httpOnly()
//            .build()
//
//        // Wrap cookie
//        // Add wrapped cookie to MemoryCookieJar cache
//        NetworkRequester.addStoredCookie(sharedPrefCookie)
//
//
//
//        Log.d("Cookie in shared prefs:", cookieString)

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
