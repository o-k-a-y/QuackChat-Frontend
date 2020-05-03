package edu.ramapo.btunney.quackchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import edu.ramapo.btunney.quackchat.caching.RoomDatabaseDAO
import edu.ramapo.btunney.quackchat.caching.entities.Cache
import edu.ramapo.btunney.quackchat.caching.entities.Friend
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import org.json.JSONObject

/**
 * This activity allows you to choose who to send the media to
 *
 */
class SendMediaToFriends : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_media_to_friends)

        displayFriendListCheckBoxes()
    }

    private fun displayFriendListCheckBoxes() {

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


    fun sendToFriendsOnClick(view: View) {

    }
}
