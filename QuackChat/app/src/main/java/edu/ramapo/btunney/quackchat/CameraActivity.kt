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
import org.json.JSONArray
import org.json.JSONObject

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "CacheTest").build()

        // TODO: ALL OF THIS SHOULD BE MOVED TO THE FRIENDS ACTIVITY WITH A CUSTOM VIEW FOR EACH FRIEND ?

        // Fetch friends from backend
        NetworkRequester.fetchFriends(ServerRoutes.GET_FRIENDS, object: NetworkCallback {
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(data: Any?) {
                val stringData: String = data.toString()
                val friends: JSONArray = JSONArray(stringData)

                for (i in 0 until friends.length()) {
                    val friend = friends.getJSONObject(i)

                    // Make new thread to handle access to database so it doesn't run on main UI thread
                    Thread {
                        // Convert JSON to map to index
                        val username: String = friend.getString("username")
                        val imageLarge: String = friend.getString("imageLarge")
                        val imageSmall: String = friend.getString("imageSmall")

                        // Insert into DB
                        val user = User(username, imageLarge, imageSmall)
                        db.userDao().insertOne(user)
                    }.start()
                }
                // Print all friends
                for (fren in db.userDao().getAll()) {
                    Log.i("@RoomDB user: ", fren.toString())
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
