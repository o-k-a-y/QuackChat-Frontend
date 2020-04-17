package edu.ramapo.btunney.quackchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.room.Room
import edu.ramapo.btunney.quackchat.caching.AppDatabase
import edu.ramapo.btunney.quackchat.caching.entities.Cache
import edu.ramapo.btunney.quackchat.caching.entities.Friend
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import org.json.JSONArray
import org.json.JSONObject

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)


        // TODO: ALL OF THIS SHOULD BE MOVED TO THE FRIENDS ACTIVITY WITH A CUSTOM VIEW FOR EACH FRIEND ?

        // Fetch friends from backend
        NetworkRequester.fetchFriends(ServerRoutes.GET_FRIENDS, object: NetworkCallback {
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(data: Any?) {
                // Open connection to db
                val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "CacheTest").build()

                println("friends from server:$data")
                val stringData: String = data.toString()
                val friendJSON: JSONObject = JSONObject(stringData)

                println("hash:" + friendJSON.getString("friendListHash"))
                val hash = friendJSON.getString("friendListHash")
                val friendList = friendJSON.getJSONArray("friendList")

                // Make new thread to handle access to database so it doesn't run on main UI thread
                Thread {
                    // Insert any new friends into User table
                    for (i in 0 until friendList.length()) {
                        val friendData = friendList.getJSONObject(i)

                        // Convert JSON to map to index
                        val username: String = friendData.getString("username")
                        val imageLarge: String = friendData.getString("imageLarge")
                        val imageSmall: String = friendData.getString("imageSmall")

                        // Insert into local DB
                        val friend = Friend(username, imageLarge, imageSmall)
                        db.friendDao().insertOne(friend)
                    }

                    // Insert hash of friend list into Cache table
                    val cache = Cache("friendList", hash)
                    db.cacheDao().insertOne(cache)
                }.start()


                // Print all friends
                for (fren in db.friendDao().getAll()) {
                    Log.i("@RoomDB friend: ", fren.toString())
                }

                // Check friend list hash
                val he = db.cacheDao().getHash("friendList")
                Log.d("@RoomDB friends Hash: ", he)
            }

        })
    }


    fun settingsOnClick(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
//        finish()
    }

}
