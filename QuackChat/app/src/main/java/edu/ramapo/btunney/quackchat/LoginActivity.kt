package edu.ramapo.btunney.quackchat

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import edu.ramapo.btunney.quackchat.caching.RoomDatabaseDAO
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import edu.ramapo.btunney.quackchat.utils.CCleaner
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

/**
 * This activity is where the user can sign in using a pre-existing account.
 * It will check if the credentials are invalid or if there is an error making
 * the network request to check.
 * The user must provide a username, an email address, and a password.
 *
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Disable screen rotations
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        // Hide the top bar in activity
        if (supportActionBar != null)
            supportActionBar?.hide()

        // Nuke Room DB data and delete all cache files
        clearCache()
    }


    /**
     * Verify login information and log in if correct
     *
     * @param view the log in button
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
        editor.apply() // if something breaks, change to commit() even though it doesn't activate in bg
    }

    /**
     * Clear the Room DB cache when logging in
     *
     */
    private fun clearCache() {
        CCleaner(applicationContext, RoomDatabaseDAO.DATABASE_NAME).wipeCache()
    }


}
