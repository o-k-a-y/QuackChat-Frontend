package edu.ramapo.btunney.quackchat

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room
import edu.ramapo.btunney.quackchat.caching.AppDatabase
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Show username from SharedPreferences
        displayUsername()
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
                clearRoomDB()


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

        NetworkRequester.addFriend(ServerRoutes.ADD_FRIEND, friend, object: NetworkCallback {
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
                        "It's fucked"
                    }
                }
                // Show error message as a toast
                runOnUiThread {
                    Runnable {
                        Toast.makeText(activityRef.applicationContext, toastText, Toast.LENGTH_SHORT).show()
                    }.run()
                }
            }

            override fun onSuccess(data: Any?) {
                runOnUiThread {
                    Runnable {
                        Toast.makeText(activityRef.applicationContext, "Sent a friend request to $friend", Toast.LENGTH_SHORT).show()
                    }.run()
                }
            }

        })
    }

    private fun clearRoomDB() {
        Thread {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "CacheTest").build()
            db.clearAllTables()
            db.close()
        }.start()
    }
}
