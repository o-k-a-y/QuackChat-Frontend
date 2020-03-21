package edu.ramapo.btunney.quackchat

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import edu.ramapo.btunney.quackchat.Networking.NetworkCallback
import edu.ramapo.btunney.quackchat.Networking.NetworkRequester
import edu.ramapo.btunney.quackchat.Networking.ServerRoutes
import kotlinx.android.synthetic.main.activity_login.usernameEditText
import kotlinx.android.synthetic.main.activity_signup.*
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
     * TODO
     *
     * @param userJSON
     */
    private fun createUser(userJSON: JSONObject) {
        NetworkRequester.postUser(ServerRoutes.SIGNUP, userJSON, object: NetworkCallback {
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                println(failureCode)

                // Make sure changing the text is done on the SignUpActivity
                runOnUiThread {
                    Runnable {
                        // TODO: this ONLY handles duplicate users so make sure to catch anything else
                        when (failureCode) {
                            NetworkCallback.FailureCode.DUPLICATE_USER -> {
                                signUpErrorText.text = "User already exists"
                            }
                        }
                    }.run()

                }
            }

            override fun onSuccess() {
                runOnUiThread {
                    Runnable {


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
