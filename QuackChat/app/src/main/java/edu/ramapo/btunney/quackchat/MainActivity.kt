package edu.ramapo.btunney.quackchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    /**
     * TODO
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ask server if user is authenticated

        // TODO ask server if user is authenticated and change this
        val authenticated = false

        if (!authenticated) {
            startLoginSignupActivity()
        } else {
            startCameraActivity()
        }
    }

    /**
     * TODO
     *
     */
    private fun startCameraActivity() {
        val intent = Intent(this, CameraActivity::class.java)

        startActivity(intent)
    }

    /**
     * TODO
     *
     */
    private fun startLoginSignupActivity() {
        val intent = Intent(this, LoginSignUpActivity::class.java)

        startActivity(intent)
    }

}
