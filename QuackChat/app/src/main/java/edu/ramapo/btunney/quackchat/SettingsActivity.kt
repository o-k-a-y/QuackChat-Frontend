package edu.ramapo.btunney.quackchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
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
        val cameraRef = this

        NetworkRequester.logOut(ServerRoutes.LOGOUT, object: NetworkCallback {
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSuccess() {
                println("logged out attempt")

                // TODO: Delete cookie from storage


                runOnUiThread {
                    Runnable {
                        val intent = Intent(cameraRef, LoginSignUpActivity::class.java)
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

    }
}
