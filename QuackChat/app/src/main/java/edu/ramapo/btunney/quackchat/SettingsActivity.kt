package edu.ramapo.btunney.quackchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


//        // Really bad code to display username
//        NetworkRequester.getUsername(ServerRoutes.ME, object: NetworkCallback {
//            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onSuccess() {
//                runOnUiThread {
//                    Runnable {
//                        currentUserIdText.text = "???"
//                    }.run()
//                }
//            }
//
//        })
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

            override fun onSuccess() {
                println("logged out attempt")

                // TODO: Delete cookie from storage


                runOnUiThread {
                    Runnable {
                        val intent = Intent(activityRef, LoginSignUpActivity::class.java)
                        startActivity(intent)
                        finish()
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

            override fun onSuccess() {
                runOnUiThread {
                    Runnable {
                        Toast.makeText(activityRef.applicationContext, "Sent a friend request to $friend", Toast.LENGTH_SHORT).show()
                    }.run()
                }
            }

        })
    }
}
