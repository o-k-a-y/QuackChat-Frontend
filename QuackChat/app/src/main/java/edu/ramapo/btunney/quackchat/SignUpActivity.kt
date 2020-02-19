package edu.ramapo.btunney.quackchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.usernameEditText
import kotlinx.android.synthetic.main.activity_signup.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class SignUpActivity : AppCompatActivity() {

    /**
     * TODO
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }


    /**
     * TODO
     *
     * @param view
     */
    fun signUpOnClick(view: View) {
        val userMap = signUpFormToMap()
        val userJSON = JSONObject(userMap)

        // TODO: Make HTTP request to backend / improve method
//        createUser(userJSON)
        createUser()

    }


    /**
     * TODO
     *
     * @param user
     */
    private fun createUser() {
        // TODO don't hardcode this you idiot
        // TODO MAKE THIS INTO A CLASS
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("http://52.55.108.86:3000/users")
            .get()
            .build()

            client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }

                    println(response.body!!.string())
                }
            }
        })
    }


    /**
     * TODO
     *
     * @return
     */
    private fun signUpFormToMap(): Map<String, String> {
        val username: String = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        // TODO: Error if both passwords are not the same
        val user: Map<String, String> = mapOf(
            "username" to username,
            "email" to email,
            "password" to password)

        return user
    }

}
