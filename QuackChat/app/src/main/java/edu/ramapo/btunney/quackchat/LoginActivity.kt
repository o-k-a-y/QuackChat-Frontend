package edu.ramapo.btunney.quackchat

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.room.Room
import edu.ramapo.btunney.quackchat.caching.AppDatabase
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import edu.ramapo.btunney.quackchat.utils.CCleaner
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

        // Nuke Room DB data and delete all cache files
        clearCache()
    }

    private fun clearCache() {
//        Thread {
//            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "CacheTest").build()
//
//            // TODO
//            db.clearAllTables()
//            applicationContext.cacheDir.deleteRecursively()
////            db.messageDao().nukeTable()
//
//            if(db.isOpen) {
//                db.openHelper.close()
//            }
//        }.start()

        // TODO make global var with database name instead of coupling
        CCleaner(applicationContext, "CacheTest").wipeCache()
    }


    /**
     * Verify login information and log in if correct
     *
     * @param view
     */
    fun loginOnClick(view: View) {
        val username: String = usernameEditText.text.toString()
        val password: String = passwordEditText.text.toString()

        val userPass: Map<String, String> = mapOf("username" to username, "password" to password)
        val userJSON = JSONObject(userPass)


        // To change to camera activity
        val activityRef = this

        // Attempt to login with credentials
        NetworkRequester.login(ServerRoutes.LOGIN, userJSON, object:
            NetworkCallback {
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                println(failureCode)

                // Make sure changing the text is done on the LoginActivity
                runOnUiThread {
                    Runnable {
                        loginErrText.text = "Incorrect login"
                    }.run()

                }
            }

            override fun onSuccess(data: Any?) {
                runOnUiThread {
                    Runnable {
                        // Store username in SharedPreferences
                        saveUsername(username)

                        val intent = Intent(activityRef, CameraActivity::class.java)
                        setResult(Activity.RESULT_OK, intent)
                        finish()

                    }.run()
                }
            }
        })
    }

    /**
     * Store username in shared preferences
     *
     * @param username
     */
    private fun saveUsername(username: String) {
        val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences("Username", MODE_PRIVATE) ?: return
        val editor = sharedPreferences.edit()
        editor.putString("Username", username)
        editor.apply() // if something breaks, change to commit() even though it doesn't activate in bg
    }


}
