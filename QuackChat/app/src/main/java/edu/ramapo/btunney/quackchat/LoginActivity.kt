package edu.ramapo.btunney.quackchat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    /**
     * TODO
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }


    /**
     * Verify login information and log in if correct
     *
     * @param view
     */
    fun loginOnClick(view: View) {
        val username: String = usernameEditText.text.toString()
        val password: String = passwordEditText.text.toString()

        val userPass: Map<String, String> = mapOf("username" to username, "password" to password)
        val userJSON = JSONObject(userPass)


        // To change to camera activity
        val activityRef = this

        // Attempt to login with credentials
        NetworkRequester.login(ServerRoutes.LOGIN, userJSON, object:
            NetworkCallback {
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                println(failureCode)

                // Make sure changing the text is done on the LoginActivity
                runOnUiThread {
                    Runnable {
                        loginErrText.text = "Incorrect login"
                    }.run()

                }
            }

            override fun onSuccess() {
                runOnUiThread {
                    Runnable {
                        val intent = Intent(activityRef, CameraActivity::class.java)
                        setResult(Activity.RESULT_OK, intent)
                        finish()

                    }.run()
                }
            }
        })
    }


}
