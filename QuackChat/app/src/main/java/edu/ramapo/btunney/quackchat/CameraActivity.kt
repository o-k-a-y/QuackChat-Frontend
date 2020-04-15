package edu.ramapo.btunney.quackchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import edu.ramapo.btunney.quackchat.dao.AppDatabase
import edu.ramapo.btunney.quackchat.dao.UserDao
import edu.ramapo.btunney.quackchat.dao.database
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import kotlinx.android.synthetic.main.activity_camera.*
import org.json.JSONArray

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)


//        val test: database = database()
//        val dao = test.userDao()


        // TODO: ALL OF THIS SHOULD BE MOVED TO THE FRIENDS ACTIVITY WITH A CUSTOM VIEW FOR EACH FRIEND
        val activityRef = this
        // Load friends
//        var friends: JSONArray? = null
        NetworkRequester.fetchFriends(ServerRoutes.GET_FRIENDS, object: NetworkCallback {
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(data: Any?) {
//                TODO("Not yet implemented")
                Log.d("friend call", "success")
//                println(data)

                runOnUiThread {
                    Runnable {
//                        var friends = JSONArray(data)
                        var newView: TextView = TextView(activityRef)
                        // TOOD: no
                        newView.text = "hello"
                        activityRef.friendListScrollView.addView(newView)
                        println(data)
                    }.run()
                }


            }

        })
    }


    fun settingsOnClick(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
//        finish()
    }

}
