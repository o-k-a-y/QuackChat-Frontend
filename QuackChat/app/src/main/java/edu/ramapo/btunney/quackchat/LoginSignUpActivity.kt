package edu.ramapo.btunney.quackchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

class LoginSignUpActivity : AppCompatActivity() {

    /**
     * TODO
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)
    }

    /**
     * TODO
     *
     * @param requestCode the code login() or signup()
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Make sure request code is correct
        if (requestCode != 7) {
            Log.e("result code from login", "Received code $requestCode")
            return
        }

        // Make sure result code is ok
        if (resultCode != RESULT_OK) {
            throw TODO()
        }

        // Start camera activity
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * TODO
     *
     * @param view
     */
    fun loginOnClick(view: View) {
        login()
    }

    /**
     * TODO
     *
     * @param view
     */
    fun signUpOnClick(view: View) {
        signUp()
    }

    /**
     * TODO
     *
     */
    private fun login() {
        val intent = Intent(this, LoginActivity::class.java)

        // Start activity as normal but tell android OS, when this returns/dies tell me
        startActivityForResult(intent, 7)

    }

    /**
     * TODO
     *
     */
    private fun signUp() {
        val intent = Intent(this, SignUpActivity::class.java)
//        startActivity(intent)
        startActivityForResult(intent, 5)

    }


}
