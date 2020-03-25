package edu.ramapo.btunney.quackchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }


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
}
