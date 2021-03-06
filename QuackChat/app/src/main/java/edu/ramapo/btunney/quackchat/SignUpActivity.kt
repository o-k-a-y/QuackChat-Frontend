package edu.ramapo.btunney.quackchat

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import kotlinx.android.synthetic.main.activity_login.usernameEditText
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.passwordEditText
import org.json.JSONObject

/**
 * This activity allows the user to create a user account by giving:
 *      1. a username
 *      2. an email address
 *      3. a password
 *      4. a verified password (do they match)
 *
 */
class SignUpActivity : AppCompatActivity() {

    /**
     * Create the activity view
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Disable screen rotations
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        // Hide the top bar in activity
        if (supportActionBar != null)
            supportActionBar?.hide()

    }


    /**
     * Creates a JSON object from the form fields to create a user
     *
     * @param view the sign up button
     */
    fun signUpOnClick(view: View) {
        // Check empty fields
        // TODO: clean up code
        if (usernameEditText.text.toString() == "") {
            usernameEditText.error = "Please provide a username"
            signUpErrorText.text = getString(R.string.fill_out_each_field)
            return
        }
        if (emailEditText.text.toString() == "") {
            emailEditText.error = "Please provide a valid email address"
            signUpErrorText.text = getString(R.string.fill_out_each_field)
            return
        }
        if (passwordEditText.text.toString() == "") {
            passwordEditText.error = "Please provide a valid password"
            signUpErrorText.text = getString(R.string.fill_out_each_field)
            return
        }

        // Check if passwords are the same
        if (confirmPasswordEditText.text.toString() != this.passwordEditText.text.toString()) {
            signUpErrorText.text = getString(R.string.mismatch_password)
            confirmPasswordEditText.error = "Passwords don't match!"
            Log.d(confirmPasswordEditText.text.toString(), passwordEditText.text.toString())
            return
        }

        // Create user
        val userMap = signUpFormToMap()
        val userJSON = JSONObject(userMap)
        createUser(userJSON)
    }


    /**
     * Attempts to create a user account and automatically logs them in
     *
     * @param userJSON
     */
    private fun createUser(userJSON: JSONObject) {

        val activityRef = this;

        NetworkRequester.createUser(ServerRoutes.SIGNUP, userJSON, object: NetworkCallback {
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

                // User object to send to backend
                val username: String = usernameEditText.text.toString()
                val password: String = passwordEditText.text.toString()
                val userPass: Map<String, String> = mapOf("username" to username, "password" to password)
                val userPassJSON = JSONObject(userPass)

                // Login which will authenticate the user into the session on the backend
                runOnUiThread {
                    Runnable {
                        // Attempt to login with credentials
                        NetworkRequester.login(ServerRoutes.LOGIN, userPassJSON, object: NetworkCallback {
                            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                                println(failureCode)

                                // Display network error to user
                                runOnUiThread {
                                    Runnable {
                                        activityRef.signUpErrorText.text = "Login failed due to network issues"
                                    }.run()
                                }
                            }

                            // Show the user the CameraActivity
                            override fun onSuccess(data: Any?) {
                                runOnUiThread {
                                    Runnable {

                                        // Store username in SharedPreferences
                                        saveUsername(username)

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
     * Convert the text inside the form into a map to convert to JSON later
     *
     * @return a map containing user details
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

    /**
     * Store username in shared preferences
     *
     * @param username
     */
    private fun saveUsername(username: String) {
        val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences("Username", MODE_PRIVATE) ?: return
        val editor = sharedPreferences.edit()
        editor.putString("Username", username)
        editor.apply()
    }

}
