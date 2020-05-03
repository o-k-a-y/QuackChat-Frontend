package edu.ramapo.btunney.quackchat

//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import edu.ramapo.btunney.quackchat.caching.RoomDatabaseDAO
import edu.ramapo.btunney.quackchat.caching.HashType
import edu.ramapo.btunney.quackchat.caching.entities.Cache
import edu.ramapo.btunney.quackchat.caching.entities.Friend
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import edu.ramapo.btunney.quackchat.views.FriendViewFactory
import edu.ramapo.btunney.quackchat.views.FriendViewType
import kotlinx.android.synthetic.main.activity_friend.*
import org.json.JSONObject

class FriendListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)

        // TODO: add a swipe gesture to refresh data and then call updateFriends() method :P
        fetchFriends()
    }

    /**
     * Check if local and remote friend list hash match
     * If they don't, call retrieveNewFriends() and fetch them
     *
     */
    private fun fetchFriends() {
        Thread {
            // Get friend list hash
            val hash = RoomDatabaseDAO.getInstance(this).getHash(HashType.FRIENDLIST)

            val json = "{\"hash\":\"$hash\"}"
            val hashJSON = JSONObject(json)
            hashJSON.put("hashType", HashType.FRIENDLIST.type)

            NetworkRequester.validateHash(ServerRoutes.CHECK_HASH, hashJSON, object: NetworkCallback {
                override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                    TODO()
                }
                // Check if hashes match
                // If hashes don't match, get new list of friends
                override fun onSuccess(data: Any?) {
                    // Hashes don't match, load cached friends
                    if (JSONObject(data.toString()).getString("hash").toString() != hashJSON.getString("hash").toString()) {
                        Log.d("@data hash", data.toString())
                        Log.d("@HASH MISMATCH", "friends hash dont match")
                        Log.d("@HASH", hashJSON.getString("hash").toString())
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
    private fun retrieveNewFriends(callback: Callback<Any>) {
        NetworkRequester.fetchFriends(ServerRoutes.FETCH_FRIENDS, object: NetworkCallback {
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
                    // Insert any new friends into User table
                    for (i in 0 until friendList.length()) {
                        val friendData = friendList.getJSONObject(i)

                        // Get fields from JSON
                        val username: String = friendData.getString("username")
                        val imageLarge: String = friendData.getString("imageLarge")
                        val imageSmall: String = friendData.getString("imageSmall")

                        // Insert into local DB
                        val friend = Friend(username, imageLarge, imageSmall)
                        RoomDatabaseDAO.getInstance(applicationContext).insertFriend(friend)
                    }

                    // Insert hash of friend list into Cache table
                    val cache = Cache("friendList", newHash)
                    RoomDatabaseDAO.getInstance(applicationContext).insertHash(cache)

                    // TODO: pass some actual data instead of null so we know what changed
                    callback.perform(null, null)
                }.start()
            }
        })
    }

    private fun loadFriends() {
        Log.d("Load friends", "loading friends")

        Thread {
            var factoryTest = LinearLayout(this)

            // Every friend
            for (friend in RoomDatabaseDAO.getInstance(applicationContext).getAllFriends()) {
                // Testing bad factory
                runOnUiThread {
                    Runnable {
                        factoryTest = FriendViewFactory.createFriendView(this, FriendViewType.LIST, friend)
                        friendListLinearLayout.addView(factoryTest)
                    }.run()
                }

            }
        }.start()

    }

}