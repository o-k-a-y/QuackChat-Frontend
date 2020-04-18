package edu.ramapo.btunney.quackchat

//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import edu.ramapo.btunney.quackchat.caching.AppDatabase
import edu.ramapo.btunney.quackchat.caching.entities.Cache
import edu.ramapo.btunney.quackchat.caching.entities.Friend
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import org.json.JSONObject

class FriendActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)

        // Load all of user's friend

        // 1. Check if hash in local DB is correct
        // Get hash from Room DB

        Thread {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "CacheTest").build()
            val hash = db.cacheDao().getHash("friendList")
            val json = "{\"hash\":\"$hash\"}"
            val hashJSON = JSONObject(json)
            if(db.isOpen) {
                db.openHelper.close()
            }

            NetworkRequester.validateHash(ServerRoutes.CHECK_HASH, hashJSON, object: NetworkCallback {
                override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                    // Get the new list of friends
                }

                // Check if hashes match
                // If hashes don't match, get new list of friends
                override fun onSuccess(data: Any?) {
                    // Hashes match, load cached friends
                    if (data.toString() != hashJSON.toString()) {
                        // Fetch new list of friends as well as new hash
                        getNewFriends()
                    }

                    // Load friend from Room DB
                    loadFriends()
                }
            })
        }.start()



        // 2. Update friend list of not updated
        // 3. Once friend list is up to date:
        //      create a new view to add to horizontal scroll view containing:
        //          a. name of friend (start with just this one)
        //          b. image of friend?
    }

    /**
     * Fetch new list of friends from server
     *
     */
    fun getNewFriends() {
        NetworkRequester.fetchFriends(ServerRoutes.GET_FRIENDS, object: NetworkCallback {
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(data: Any?) {
//                println("friends from server:$data")
                val stringData: String = data.toString()
                val friendJSON: JSONObject = JSONObject(stringData)

//                println("hash:" + friendJSON.getString("friendListHash"))
                val newHash = friendJSON.getString("friendListHash")
                val friendList = friendJSON.getJSONArray("friendList")

                // Make new thread to handle access to database so it doesn't run on main UI thread
                Thread {
                    val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "CacheTest").build()

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
                    val cache = Cache("friendList", newHash)
                    db.cacheDao().insertOne(cache)

                    // Print all friends
                    for (fren in db.friendDao().getAll()) {
                        Log.i("@RoomDB friend: ", fren.toString())
                    }

                    // Check friend list hash
                    val he = db.cacheDao().getHash("friendList")
                    Log.d("@RoomDB friends Hash: ", he)

                    if(db.isOpen) {
                        db.openHelper.close()
                    }
                }.start()


            }
        })
    }

    fun loadFriends() {
        Log.d("Load friends", "loading friends")
    }

}
