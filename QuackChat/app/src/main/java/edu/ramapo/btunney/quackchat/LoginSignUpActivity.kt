package edu.ramapo.btunney.quackchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        startActivity(intent)
    }

    /**
     * TODO
     *
     */
    private fun signUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}
