package edu.ramapo.btunney.quackchat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import edu.ramapo.btunney.quackchat.Networking.NetworkCallback
import edu.ramapo.btunney.quackchat.Networking.NetworkRequester
import edu.ramapo.btunney.quackchat.Networking.ServerRoutes

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // Check if auth'd
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
