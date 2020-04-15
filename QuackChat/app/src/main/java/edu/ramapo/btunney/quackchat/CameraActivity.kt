package edu.ramapo.btunney.quackchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.room.Room
import edu.ramapo.btunney.quackchat.room.AppDatabase
import edu.ramapo.btunney.quackchat.room.User
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)


        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "CacheTest").build()

        // Make new thread to handle access to database so it doesn't run on main UI thread
        Thread {
            val user = User(3, "Joe", "Jo")
            db.userDao().insertOne(user)

            for (helo in db.userDao().getAll()) {
                Log.i("@RoomDB user: ", helo.toString())
            }
        }.start()

        // TODO: ALL OF THIS SHOULD BE MOVED TO THE FRIENDS ACTIVITY WITH A CUSTOM VIEW FOR EACH FRIEND ?
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
                        // TODO: no
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
