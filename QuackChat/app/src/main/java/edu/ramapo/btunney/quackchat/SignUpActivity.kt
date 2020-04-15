package edu.ramapo.btunney.quackchat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.usernameEditText
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.passwordEditText
import org.json.JSONObject


class SignUpActivity : AppCompatActivity() {

    /**
     * TODO
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }



    /**
     * TODO
     *
     * @param view
     */
    fun signUpOnClick(view: View) {
        if (confirmPasswordEditText.text.toString() != this.passwordEditText.text.toString()) {
            signUpErrorText.text = "Passwords don't match!"
            Log.d(confirmPasswordEditText.text.toString(), passwordEditText.text.toString())
            return
        }

        val userMap = signUpFormToMap()
        val userJSON = JSONObject(userMap)

        // TODO: Make HTTP request to backend / improve method
        createUser(userJSON)
    }


    /**
     * Attempts to create a user account and automatically logs them in
     *
     * @param userJSON
     */
    private fun createUser(userJSON: JSONObject) {

        val activityRef = this;

        NetworkRequester.postUser(ServerRoutes.SIGNUP, userJSON, object: NetworkCallback {
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                println(failureCode)

                // Make sure changing the text is done on the SignUpActivity
                runOnUiThread {
                    Runnable {
                        // TODO: this ONLY handles duplicate users so make sure to catch anything else
                        when (failureCode) {
                            NetworkCallback.FailureCode.DUPLICATE_USER -> {
                                signUpErrorText.text = getString(R.string.user_exists)
                            }
                            NetworkCallback.FailureCode.DEFAULT -> {
                                signUpErrorText.text = "Login failed due to network issues"
                            }
                        }
                    }.run()

                }
            }

            // TODO: When sign up is successful, bring to Camera Activity
            override fun onSuccess(data: Any?) {

                // User object
                val username: String = usernameEditText.text.toString()
                val password: String = passwordEditText.text.toString()

                val userPass: Map<String, String> = mapOf("username" to username, "password" to password)
                val userPassJSON = JSONObject(userPass)

                runOnUiThread {
                    Runnable {
                        // Login which will authenticate the user into the session on the backend
                        // Attempt to login with credentials
                        NetworkRequester.login(ServerRoutes.LOGIN, userPassJSON, object: NetworkCallback {
                            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                                println(failureCode)

                                // Make sure changing the text is done on the SignUpActivity
                                runOnUiThread {
                                    Runnable {
                                        activityRef.signUpErrorText.text = "Login failed due to network issues"
                                    }.run()
                                }
                            }

                            override fun onSuccess(data: Any?) {
                                runOnUiThread {
                                    Runnable {
                                        val intent = Intent(activityRef, CameraActivity::class.java)
                                        setResult(Activity.RESULT_OK, intent)
                                        finish()
                                    }.run()
                                }
                            }
                        })
                    }.run()
                }
            }

        })
    }


    /**
     * TODO
     *
     * @return
     */
    private fun signUpFormToMap(): Map<String, String> {
        val username: String = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        return mapOf(
            "username" to username,
            "email" to email,
            "password" to password)
    }

}
