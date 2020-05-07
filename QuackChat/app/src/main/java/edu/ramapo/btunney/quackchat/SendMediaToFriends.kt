package edu.ramapo.btunney.quackchat

import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.children
import com.bumptech.glide.Glide
import edu.ramapo.btunney.quackchat.caching.HashType
import edu.ramapo.btunney.quackchat.caching.RoomDatabaseDAO
import edu.ramapo.btunney.quackchat.caching.entities.Cache
import edu.ramapo.btunney.quackchat.caching.entities.Friend
import edu.ramapo.btunney.quackchat.networking.MessageType
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import kotlinx.android.synthetic.main.activity_send_media_to_friends.*
import org.json.JSONObject
import java.io.File

/**
 * This activity allows you to choose who to send the media to
 * They will receive it as a picture/video message which they can view in MessageActivity
 *
 */
class SendMediaToFriends : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_media_to_friends)

        // Disable screen rotations
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        // Hide the top bar in activity
        if (supportActionBar != null)
            supportActionBar?.hide()

        hideSendButton()

        displayFriendListCheckBoxes()
    }

    /**
     * Hide the send button so a user can't send a message without choosing who to send to
     *
     */
    private fun hideSendButton() {

        sendToFriendsButton.visibility = View.GONE
    }

    /**
     * Show the send button so a user can send a message to selected friends
     *
     */
    private fun showSendButton() {
        sendToFriendsButton.visibility = View.VISIBLE
    }

    /**
     * Display list of friends as checkboxes by first verifying we have the updated list
     * Then fetching the new list if our list of friends is outdated
     *
     */
    private fun displayFriendListCheckBoxes() {
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

                                runOnUiThread {
                                    Runnable {
                                        showSendButton()
                                    }.run()
                                }

                            }

                        })
                    } else {
                        loadFriends()

                        runOnUiThread {
                            Runnable {
                                showSendButton()
                            }.run()
                        }
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

    /**
     * Inform the user they have no friends by showing text informing them so
     * and also show a sad duck
     *
     */
    private fun displayNoFriends() {
        runOnUiThread {
            noFriendsSendGifImageView.visibility = View.VISIBLE
            noFriendsSendTextView.visibility = View.VISIBLE
            sendToFriendsButton.visibility = View.GONE
            noFriendsSendTextView.text = "You have no friends"
            Glide.with(this)
                    .asGif()
                    .load("file:///android_asset/noFriends.gif")
                    .into(noFriendsSendGifImageView)
        }

    }

    /**
     * Load the list of friends as checkboxes
     *
     */
    private fun loadFriends() {
        Log.d("Load friends", "loading friends")

        Thread {
            // TODO
//            var factoryTest = LinearLayout(this)

            // You have no friends
            val friends = RoomDatabaseDAO.getInstance(applicationContext).getAllFriends()
            if (friends.size <= 0) {
                displayNoFriends()
            }

            // Give a checkbox to each friend
            for (friend in friends) {
                runOnUiThread {
                    Runnable {
                        // TODO: create FriendViewFactory method to create checkbox for me and use that
//                        val checkBox = FriendViewFactory.createFriendView(...)
                        val checkBox = CheckBox(this).also {
                            it.setText(friend.username)
                        }

                        sendToFriendsList.addView(checkBox)

                    }.run()
                }

            }
        }.start()
    }


    // TODO
    private fun getMediaFile(): File {
        var filePath = cacheDir.absolutePath
        filePath += when (getMediaType()){
            MessageType.PICTURE.type -> "/picture"
            MessageType.VIDEO.type -> "/video"
            else -> null
        }

        return File(filePath)
    }

    /**
     * Find out what type of media it by accessing the intent extra
     *
     */
    private fun getMediaType(): String  {
        return intent.getStringExtra(SendMediaActivity.MEDIATYPE)
    }

    /**
     * Send the network request to send the media to the list of friends
     *
     * @param friends friends to send media to
     */
    private fun initiateMediaSendRequest(friends: ArrayList<String>) {
        val file = getMediaFile()

        if (file == null) {
            throw Exception("File for media was null when sending media")
        }

        val byteArray = file.readBytes()

        // Base64 encode data
        val base64EncodedData = Base64.encodeToString(byteArray, Base64.DEFAULT)
        Log.d("picture", base64EncodedData)

        val mediaType = getMediaType()
        val messageType = when(mediaType) {
            MessageType.PICTURE.type -> {
                MessageType.PICTURE
            }
            MessageType.VIDEO.type -> {
                MessageType.VIDEO
            }
            else -> {
                null
            }
        }

        if (messageType == null) {
            throw Exception("Message type of media is null")
        }

        NetworkRequester.sendMessage(ServerRoutes.SEND_MESSAGE, friends.toTypedArray(), base64EncodedData, messageType, object : NetworkCallback {
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(data: Any?) {
                Log.d("@SendMediaToFriends", "media message sent")
                Log.d("Sent to", friends.toString())

                // TODO: maybe figure out way to come back to SendMediaActivity using finishAffinity()
                finish()
            }

        })
    }


    /**
     * When send button is clicked, send the media to each selected friend in the list of checkboxes
     *
     * @param view
     */
    fun sendToFriendsOnClick(view: View) {
        val friendsToSendTo = ArrayList<String>()

        val checkBoxes = sendToFriendsList.children.toList()

        for (checkBox in checkBoxes) {
            if (checkBox !is CheckBox) continue

            if (checkBox.isChecked) {
                friendsToSendTo.add(checkBox.text.toString())
            }
        }

        // Must select at least 1 friend
        if (friendsToSendTo.size <= 0) {
            Toast.makeText(this, "Please select someone to send your memes to", Toast.LENGTH_SHORT).show()
            return
        }

        println(friendsToSendTo)
        // Send the media
        initiateMediaSendRequest(friendsToSendTo)
    }
}
