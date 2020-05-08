package edu.ramapo.btunney.quackchat

//import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import edu.ramapo.btunney.quackchat.caching.RoomDatabaseDAO
import edu.ramapo.btunney.quackchat.caching.HashType
import edu.ramapo.btunney.quackchat.caching.entities.Cache
import edu.ramapo.btunney.quackchat.caching.entities.Friend
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import edu.ramapo.btunney.quackchat.views.FriendViewFactory
import edu.ramapo.btunney.quackchat.views.FriendViewType
import kotlinx.android.synthetic.main.activity_friend_list.*
import org.json.JSONObject

/**
 * This activity is where you see your list of friends.
 * The list of friends is displayed as a Vertical ScrollView containing LinearLayouts
 * where each represents a friend.
 *
 * Tapping on the friend's profile picture bring you to the FriendProfileActivity where you can view
 * friend information as well as remove the friend from your friend list (also deletes messages)
 *
 * Tapping on the friend's username or white space near it will take you to the MessageActivity where
 * you can see what message they have sent you
 *
 */
class FriendListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)

        // Disable screen rotations
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        // Hide the top bar in activity
        if (supportActionBar != null)
            supportActionBar?.hide()

        // Set loading gif (duck walking gif)
        Glide.with(this)
                .asGif()
                .load("file:///android_asset/loading.gif")
                .into(loadingGifimageView)

        // Check if we need to update the friend list and display all friends
        fetchFriends()
    }

    /**
     * Kill the activity when a friend is deleted
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        finish()
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

                // Make new thread to handle access to database because it can't run on main UI thread
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

    /**
     * Turn off the loading duck gif
     *
     */
    private fun disableLoadingScreen() {
        runOnUiThread {
            loadingGifimageView.visibility = View.GONE
        }
    }

    /**
     * Display gif and text that shows user has no friends
     * Also set text to notify user they have no friends
     *
     */
    private fun showNoFriendsGif() {
        runOnUiThread {
            Glide.with(this)
                    .asGif()
                    .load("file:///android_asset/noFriends.gif")
                    .into(noFriendsGifImageView)

            noFriendsTextView.text = "You have no friends"
        }
    }

    /**
     * Load all the friends onto the screen showing both the username and profile picture
     * The picture can be clicked to show user information along with option to delete friend
     * The username or whitespace can be clicked to see and send messages to that friend
     *
     */
    private fun loadFriends() {
        disableLoadingScreen()
        Log.d("Load friends", "loading friends")

        val activityRef = this
        Thread {
            var friendView = LinearLayout(this)

            // Our list of friends
            val friends = RoomDatabaseDAO.getInstance(applicationContext).getAllFriends()

            // No friends
            if (friends.size <= 0) {
                showNoFriendsGif()
            }

            // Create a friend view for each friend in our friend list
            for (friend in friends) {
                runOnUiThread {
                    Runnable {
                        friendView = FriendViewFactory.createFriendView(activityRef, FriendViewType.LIST, friend)
                        friendListLinearLayout.addView(friendView)
                    }.run()
                }

            }
        }.start()

    }

    /**
     * Used when passing intents to this Activity
     */
    companion object {
        val DELETEFRIEND = "deleteFriend"
    }

}