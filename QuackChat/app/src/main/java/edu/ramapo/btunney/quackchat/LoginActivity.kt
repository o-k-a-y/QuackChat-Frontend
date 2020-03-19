package edu.ramapo.btunney.quackchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    /**
     * TODO
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }


    /**
     * TODO
     *
     * @param view
     */
    fun loginOnClick(view: View) {
        Log.d("hello", "hello")
        // TODO authenticate login
        val username: String = usernameEditText.text.toString()
        val password: String = passwordEditText.text.toString()

        val userPass: Map<String, String> = mapOf("username" to username, "password" to password)
        val userJSON = JSONObject(userPass)

        NetworkRequester.login("http://52.55.108.86:3000/users", userJSON, "/login")
    }


}
