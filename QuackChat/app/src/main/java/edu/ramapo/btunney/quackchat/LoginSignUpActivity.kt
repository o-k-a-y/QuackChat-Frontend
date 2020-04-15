package edu.ramapo.btunney.quackchat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes

class LoginSignUpActivity : AppCompatActivity() {

    /**
     * TODO
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)
    }

    /**
     * Handle cases when login and signup dies
     *
     * @param requestCode the code login() or signup()
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("Result code: ", resultCode.toString())

        // What caused us to get to this screen
        when (resultCode) {
            Activity.RESULT_CANCELED -> {
                val intent = Intent(this, LoginSignUpActivity::class.java)
                startActivity(intent)
                finish()
                return
            }

        }


        when (requestCode) {
            5, 7 -> {
                val ref = this

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
                                val intent = Intent(ref, LoginSignUpActivity::class.java)
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }.run()
                        }
                    }

                    /**
                     * User is authenticated
                     *
                     */
                    override fun onSuccess(data: Any?) {
                        // go to camera activity
                        println()

                        runOnUiThread {
                            Runnable {
                                val intent = Intent(ref, CameraActivity::class.java)
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }.run()
                        }
                    }

                })
            }
            else -> {
                Log.e(javaClass.simpleName, "Received code $requestCode")
                return
            }
        }

        // Make sure result code is ok
        if (resultCode != RESULT_OK) {
            // TODO how message if makes sense
            Log.e(javaClass.simpleName, "Something went terribly wrong")
            return

        }

        // Start camera activity
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * TODO
     *
     * @param view
     */
    fun loginOnClick(view: View) {
        login()
    }

    /**
     * TODO
     *
     * @param view
     */
    fun signUpOnClick(view: View) {
        signUp()
    }

    /**
     * TODO
     *
     */
    private fun login() {
        val intent = Intent(this, LoginActivity::class.java)

        // Start activity as normal but tell android OS, when this returns/dies tell me
        startActivityForResult(intent, 7)

    }

    /**
     * TODO
     *
     */
    private fun signUp() {
        val intent = Intent(this, SignUpActivity::class.java)
//        startActivity(intent)
        startActivityForResult(intent, 5)

    }


}
