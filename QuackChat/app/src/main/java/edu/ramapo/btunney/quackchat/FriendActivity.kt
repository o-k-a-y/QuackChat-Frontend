package edu.ramapo.btunney.quackchat

//import android.support.v7.app.AppCompatActivity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import edu.ramapo.btunney.quackchat.caching.AppDatabase
import edu.ramapo.btunney.quackchat.caching.entities.Cache
import edu.ramapo.btunney.quackchat.caching.entities.Friend
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import edu.ramapo.btunney.quackchat.views.FriendViewFactory
import edu.ramapo.btunney.quackchat.views.FriendViewType
import kotlinx.android.synthetic.main.activity_friend.*
import org.json.JSONObject

class FriendActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)

        // TODO: add a swipe gesture to refresh data and then call updateFriends() method :P
        fetchFriends()
    }

    private fun fetchFriends() {
        Thread {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "CacheTest").build()
            val hash = db.cacheHashDao().getHash("friendList")
            val json = "{\"hash\":\"$hash\"}"
            val hashJSON = JSONObject(json)
            if(db.isOpen) {
                db.openHelper.close()
            }

            NetworkRequester.validateHash(ServerRoutes.CHECK_HASH, hashJSON, object: NetworkCallback {
                override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                    TODO()
                }

                // Check if hashes match
                // If hashes don't match, get new list of friends
                override fun onSuccess(data: Any?) {
                    // Hashes match, load cached friends
                    if (data.toString() != hashJSON.toString()) {
                        // Fetch new list of friends as well as new hash
                        retrieveNewFriends(object: Callback<Any> {
                            override fun perform(data: Any?, error: Throwable?) {
                                loadFriends()
                            }

                        })
                    } else {
                        loadFriends()
                    }
                }
            })
        }.start()
    }

    /**
     * Fetch new list of friends from server
     *
     */
    fun retrieveNewFriends(callback: Callback<Any>) {
        NetworkRequester.fetchFriends(ServerRoutes.GET_FRIENDS, object: NetworkCallback {
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(data: Any?) {
                val stringData: String = data.toString()
                val friendJSON: JSONObject = JSONObject(stringData)

                val newHash = friendJSON.getString("friendListHash")
                val friendList = friendJSON.getJSONArray("friendList")

                // Make new thread to handle access to database so it doesn't run on main UI thread
                Thread {
                    val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "CacheTest").build()

                    // Insert any new friends into User table
                    for (i in 0 until friendList.length()) {
                        val friendData = friendList.getJSONObject(i)

                        // Get fields from JSON
                        val username: String = friendData.getString("username")
                        val imageLarge: String = friendData.getString("imageLarge")
                        val imageSmall: String = friendData.getString("imageSmall")

                        // Insert into local DB
                        val friend = Friend(username, imageLarge, imageSmall)
                        db.friendDao().insertOne(friend)
                    }

                    // Insert hash of friend list into Cache table
                    val cache = Cache("friendList", newHash)
                    db.cacheHashDao().insertOne(cache)


                    // Print all friends
                    for (fren in db.friendDao().getAll()) {
                        Log.i("@RoomDB friend: ", fren.toString())
                    }

                    // Check friend list hash
                    val he = db.cacheHashDao().getHash("friendList")
                    Log.d("@RoomDB friends Hash: ", he)


                    if(db.isOpen) {
                        db.openHelper.close()
                    }

                    // TODO: pass some actual data instead of null so we know what changed
                    callback.perform(null, null)
                }.start()
            }
        })
    }

    fun loadFriends() {
        Log.d("Load friends", "loading friends")

        Thread {
            var factoryTest = LinearLayout(this)

            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "CacheTest").build()

            // Every friend
            for (friend in db.friendDao().getAll()) {
                // Testing bad factory
                runOnUiThread {
                    Runnable {
                        factoryTest = FriendViewFactory.createFriendView(this, FriendViewType.LIST, friend)
                        friendListLinearLayout.addView(factoryTest)
                    }.run()
                }

            }
            if(db.isOpen) {
                db.openHelper.close()
            }
        }.start()

    }

}