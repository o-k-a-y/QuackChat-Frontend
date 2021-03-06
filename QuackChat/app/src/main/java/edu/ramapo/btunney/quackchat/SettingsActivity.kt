package edu.ramapo.btunney.quackchat

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import edu.ramapo.btunney.quackchat.caching.RoomDatabaseDAO
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import edu.ramapo.btunney.quackchat.utils.CCleaner
import kotlinx.android.synthetic.main.activity_settings.*

/**
 * This activity shows different user settings are available.
 * Currently there aren't many.
 *
 * The only thing you can do here is:
 *      1. View your username
 *      2. Log out
 *      3. Add a friend by username
 *
 */
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Disable screen rotations
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        // Hide the top bar in activity
        if (supportActionBar != null)
            supportActionBar?.hide()

        // Show username from SharedPreferences
        displayUsername()

        // If you press enter, it will attempt to add the friend in the TextView
        addFriendEditText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent?): Boolean {
                // If the event is a key-down event on the "enter" button
                if ((event?.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    addFriendOnClick(v)
                    return true;
                }
                return false;
            }
        })

    }


    /**
     * Signs user out of application, and de-authenticates them
     *
     * @param view
     */
    fun signOutOnClick(view: View) {
        val activityRef = this

        NetworkRequester.logOut(ServerRoutes.LOGOUT, object: NetworkCallback {
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSuccess(data: Any?) {
                println("logged out attempt")

                // TODO: Delete cookie from storage
                // Clear the cache on log out
                clearCache()


                runOnUiThread {
                    Runnable {
                        val intent = Intent(activityRef, LoginSignUpActivity::class.java)
                        startActivity(intent)

                        // Kill Settings and Camera Activity (Camera is still active on the stack)
                        finishAffinity()
                    }.run()
                }
            }

        })
    }

    /**
     * Sends a friend request to the user if they exist
     *
     * @param view
     */
    fun addFriendOnClick(view: View) {
        val activityRef = this

        val friend = addFriendEditText.text.toString()

        // Attempt to add friend
        NetworkRequester.addFriend(ServerRoutes.ADD_FRIEND, friend, object: NetworkCallback {
            // Display error message to user
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                var toastText = ""
                toastText = when (failureCode) {
                    NetworkCallback.FailureCode.DOES_NOT_EXIST -> {
                        "User $friend does not exist"
                    }
                    NetworkCallback.FailureCode.ALREADY_ADDED -> {
                        "You've already sent a friend request to $friend"
                    }
                    NetworkCallback.FailureCode.DEFAULT -> {
                        "Network error"
                    }
                    else -> {
                        "It's ducked"
                    }
                }
                // Show error message as a toast
                runOnUiThread {
                    Runnable {
                        Toast.makeText(activityRef.applicationContext, toastText, Toast.LENGTH_SHORT).show()
                    }.run()
                }
            }

            // Tell user request was sent
            override fun onSuccess(data: Any?) {
                runOnUiThread {
                    Runnable {
                        Toast.makeText(activityRef.applicationContext, "Sent a friend request to $friend", Toast.LENGTH_SHORT).show()
                    }.run()
                }
            }

        })
    }

    /**
     * Display the logged in user's username from SharedPreferences
     *
     */
    private fun displayUsername() {
        val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences("Username", MODE_PRIVATE)
        val username = sharedPreferences.getString("Username", "null")

        Log.d("@SHARED_PREF", username)
        currentUserIdText.text = getString(R.string.logged_in_as, username)
    }

    /**
     * Clears the cache files as well as the Room DB tables
     *
     */
    private fun clearCache() {
        CCleaner(applicationContext, RoomDatabaseDAO.DATABASE_NAME).wipeCache()
    }
}
