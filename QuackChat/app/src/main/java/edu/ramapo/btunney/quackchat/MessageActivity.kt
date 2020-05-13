package edu.ramapo.btunney.quackchat

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import edu.ramapo.btunney.quackchat.caching.HashType
import edu.ramapo.btunney.quackchat.caching.RoomDatabaseDAO
import edu.ramapo.btunney.quackchat.caching.entities.Cache
import edu.ramapo.btunney.quackchat.caching.entities.Message
import edu.ramapo.btunney.quackchat.networking.*
import edu.ramapo.btunney.quackchat.views.MediaOpenedViewFactory
import edu.ramapo.btunney.quackchat.views.MessageViewFactory
import edu.ramapo.btunney.quackchat.views.MessageViewType
import kotlinx.android.synthetic.main.activity_message.*
import org.json.JSONObject
import java.io.File


/**
 * This activity is where you can send text messages to a friend
 * and also view text messages the friend has sent as well as
 * any pictures and videos they might have sent
 *
 */
class MessageActivity : AppCompatActivity() {
    private lateinit var mFriend: String

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Disable screen rotations
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        // Hide the top bar in activity
        if (supportActionBar != null)
            supportActionBar?.hide()

        setContentView(R.layout.activity_message)

        // Set loading gif (duck walking gif)
        Glide.with(this)
                .asGif()
                .load("file:///android_asset/loading.gif")
                .into(loadingGifimageView)

        // Get username of friend from intent
        val extras = intent.extras
        if (extras != null) {
            mFriend = extras.getString("username").toString()
        }

        // Hide video view
        messageVideoView.visibility = View.GONE

        // Display friend username
        showFriendUsername()

        // Add listener to message box
        addMessageBoxListener()

        // Fetch any new messages from friend
        fetchMessages()

        // When clicked all views are removed
        mediaFrameLayout.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                mediaFrameLayout.removeAllViews()
            }

        })
    }


    private fun showFriendUsername() {
        friendUsernameTextView.text = mFriend
    }

    /**
     * Adds a listener for when user presses enter to send a message
     *
     */
    private fun addMessageBoxListener() {
        sendMessageEditText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent?): Boolean {
                // If the event is a key-down event on the "enter" button
                if ((event?.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (sendMessageEditText.text.toString() == "") {
                        return false;
                    }
                    sendMessage(sendMessageEditText.text.toString())
                    sendMessageEditText.text.clear()

                    // Remove duck no message gif
                    showNoMessagesGif(false)

                    return true
                }
                return false
            }
        })
    }


    /**
     * Sends a message using NetworkRequester's sendMessage function
     *
     * @param message
     */
    private fun sendMessage(message: String) {
        NetworkRequester.sendMessage(ServerRoutes.SEND_MESSAGE, arrayOf(mFriend), message, MessageType.TEXT, object: NetworkCallback {
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(data: Any?) {
                Log.d("@MessageActivity", "sent message?")
                // TODO: this only handles text
//                makeMessageFragment(message)
                makeMessageLinearLayout(message)
            }

        })
    }


    /**
     * Check if local hash for messages matches remote
     * If they don't match, fetch new messages from server by calling retrieveNewMessages()
     * If they match, call loadMessages()
     *
     */
    private fun fetchMessages() {
        Thread {
            val hash = RoomDatabaseDAO.getInstance(this).getHash(HashType.MESSAGES)

            val json = "{\"hash\":\"$hash\"}"
            val hashJSON = JSONObject(json)

            hashJSON.put(NetworkJSONKeys.HASHTYPE.type, HashType.MESSAGES.type)


            NetworkRequester.validateHash(ServerRoutes.CHECK_HASH, hashJSON, object: NetworkCallback {
                override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                    TODO()
                }

                // Check if hashes match
                // If hashes don't match, get new list of friends
                override fun onSuccess(data: Any?) {
                    // Hashes don't match, load cached friends
                    if (JSONObject(data.toString()).getString(NetworkJSONKeys.HASH.type).toString() != hashJSON.getString(NetworkJSONKeys.HASH.type).toString()) {
                        // Fetch new list of friends as well as new hash
                        retrieveNewMessages(object: Callback<Any> {
                            override fun perform(data: Any?, error: Throwable?) {
                                loadMessages()
                            }
                        })
                    } else {
                        loadMessages()
                    }
                }
            })
        }.start()
    }

    /**
     * Fetch new messages from server
     *
     */
    private fun retrieveNewMessages(callback: Callback<Any>) {
        NetworkRequester.fetchMessages(ServerRoutes.FETCH_MESSAGES, object: NetworkCallback {
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(data: Any?) {
                val stringData: String = data.toString()
                val messageJSON: JSONObject = JSONObject(stringData)

                val newHash = messageJSON.getString(NetworkJSONKeys.MESSAGESHASH.type)
                val messages = messageJSON.getJSONArray(NetworkJSONKeys.MESSAGES.type)

                // Make new thread to handle access to database so it doesn't run on main UI thread
                Thread {
                    // Insert any new messages into Message table
                    for (i in 0 until messages.length()) {
                        val messageData = messages.getJSONObject(i)

                        // Get fields from JSON
                        val type: String = messageData.getString(NetworkJSONKeys.TYPE.type)
                        val to: String = messageData.getString(NetworkJSONKeys.TO.type)
                        val from: String = messageData.getString(NetworkJSONKeys.FROM.type)
                        var message: String = messageData.getString(NetworkJSONKeys.MESSAGE.type)
                        val timeSent: String = messageData.getString(NetworkJSONKeys.TIMESENT.type)

                        // If message is picture or video, we should make a file in the cache directory so
                        // we don't overwhelm the local DB (max 1mb files)
                        // TODO
                        if (type == MessageType.PICTURE.type || type == MessageType.VIDEO.type) {
                            // The actual contents of the message
                            val fileContents = message

                            message = from + timeSent

                            val cacheDir = applicationContext.cacheDir

                            if (cacheDir != null) {
                                val cacheFile = File.createTempFile(message, null, cacheDir)

                                // Write message contents to cache file
                                cacheFile.writeText(fileContents)

                                // This will be the name of the file
                                message = cacheFile.name
                            }
                        }

                        // Insert into local DB
                        val messageEntity = Message(0, type, to, from, message, timeSent)
                        RoomDatabaseDAO.getInstance(applicationContext).insertMessage(messageEntity)
                    }

                    // Insert hash of friend list into Cache table
                    val cache = Cache(HashType.MESSAGES.type, newHash)
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
     * Show the gif and message informing user they have no messages
     *
     */
    private fun showNoMessagesGif(show: Boolean) {
        runOnUiThread {
            if (show) {
                Glide.with(this)
                        .asGif()
                        .load("file:///android_asset/noMessagesPhone.gif")
                        .into(noMessagesGifImageView)
            } else {
                noMessagesGifImageView.visibility = View.GONE
            }
        }
    }

    /**
     * Load all the messages from the friend
     *
     */
    private fun loadMessages() {
        Log.d("@Load messages", "TODO")
        disableLoadingScreen()

        // Get messages from Room DB
        Thread {
            val messages = RoomDatabaseDAO.getInstance(applicationContext).getAllMessagesFrom(mFriend)

            // No messages
            if (messages.size <= 0) {
                showNoMessagesGif(true)
            }

            for (message in messages) {
                makeMessageLinearLayout(message)
            }

            // Delete the messages so they can't be opened again when refreshing activity
            deleteMessagesFromBackend()
            deleteMessagesFromCache()
        }.start()
    }

    /**
     * Delete all the messages the user opened from the backend
     * It would have been better to only delete the messages the user "opened" but we
     * assume that if you opened the messages you will read each of them
     *
     */
    private fun deleteMessagesFromBackend() {
        NetworkRequester.deleteMessages(ServerRoutes.DELETE_MESSAGES, mFriend, object: NetworkCallback {
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(data: Any?) {
//                TODO("Not yet implemented")
                // Deleting messages requires no action
            }

        })
    }

    /**
     * Delete all the messages the friend sent you from the Room DB
     *
     */
    private fun deleteMessagesFromCache() {
        RoomDatabaseDAO.getInstance(applicationContext).deleteMessages(mFriend)
    }

    /**
     * Display each message received by creating a LinearLayout representing each message
     * Text is displayed as text, picture and video are displayed as buttons you can click
     * to view the actual content
     *
     * @param message
     */
    private fun makeMessageLinearLayout(message: Message) {
        runOnUiThread {
            Runnable {
                // Create the view from the message's content
                val messageLinearLayout = MessageViewFactory.createMessageView(this, message, null)
                messagesLinearLayout.addView(messageLinearLayout)

                // Add padding
                addPadding(messageLinearLayout)

                // Make image clickable
                if (message.type == MessageViewType.PICTURE.type) {
                    addOnClickToPictureView(messageLinearLayout, message)
                } else if (message.type == MessageViewType.VIDEO.type) {
                    addOnClickToVideoView(messageLinearLayout, message)
                }

            }.run()
        }


    }


    /**
     * Display each text message sent by creating a LinearLayout containing text
     *
     * @param message
     */
    private fun makeMessageLinearLayout(messageSent: String) {
        runOnUiThread {
            Runnable {
                // Create the view from the message's content (just text)
                val messageLinearLayout = MessageViewFactory.createMessageView(this, null, messageSent)
                messagesLinearLayout.addView(messageLinearLayout)

                // Add padding
                addPadding(messageLinearLayout)
            }.run()
        }

    }

    /**
     * Add an onClick listener to the LinearLayout containing a picture
     * When clicked, it will display the image in fullscreen, and when clicked again,
     * the image will disappear and the original LinearLayout can not be opened again
     *
     * @param mediaView the LinearLayout containing the picture message
     * @param message the message data
     */
    private fun addOnClickToPictureView(mediaView: LinearLayout, message: Message) {
        // Add an onClick to the button so image is displayed in full screen
        mediaView.setOnClickListener {
            // message.message = filename in cacheDir
            val messageContent = File(cacheDir, message.message)

            // Decode image
            val decodedString = Base64.decode(messageContent.readText(), Base64.DEFAULT)
            val decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

            // Create image view to display image
            val pictureView = ImageView(this)
            pictureView.setScaleType(ImageView.ScaleType.FIT_XY) // image should fit full screen size
            pictureView.setImageBitmap(decodedBitmap)

            mediaFrameLayout.addView(pictureView)

            // Change the LinearLayout to the opened picture/video version
            setMediaViewOpened(mediaView, MessageViewType.PICTURE)

            // Picture can not be reopened
            mediaView.isClickable = false
            Log.d("@CLICK", "click")
        }
    }

    /**
     * Add an onClick listener to the LinearLayout containing a video
     * When clicked, it will display the video in fullscreen, play it, and close it.
     * The video will disappear and the original LinearLayout can not be opened again
     *
     * @param mediaView the LinearLayout containing the video message
     * @param message the message data
     */
    private fun addOnClickToVideoView(mediaView: LinearLayout, message: Message) {
        mediaView.setOnClickListener{
            messageVideoView.visibility = View.VISIBLE
            // message.message = filename in cacheDir
            val messageContent = File(cacheDir, message.message)

            // Decode video
            val byteArray = Base64.decode(messageContent.readText(), Base64.DEFAULT)

            // Create file with byte array
            val file = File(cacheDir.absolutePath + "/test")
            file.writeBytes(byteArray)

            // Get the video from cache
            val uri = FileProvider.getUriForFile(this, this.packageName + ".provider", file)
            messageVideoView.setVideoURI(uri)

            // Display the video in full screen
            val metrics = DisplayMetrics()
            applicationContext.resources.displayMetrics;
            val params = messageVideoView.layoutParams
            params.width = metrics.widthPixels
            params.height = metrics.heightPixels
            messageVideoView.layoutParams = params
            messageVideoView.start()

            // When video is done being played, delete it from view
            messageVideoView.setOnCompletionListener {
                messageVideoView.visibility = View.GONE
            }

            // Change the LinearLayout to the opened picture/video version
            setMediaViewOpened(mediaView, MessageViewType.VIDEO)

            // Video can not be reopened
            mediaView.isClickable = false
            Log.d("@CLICK", "click")
        }
    }


    /**
     * Change what the media view pictures (picture and video) look like
     * Replaces previous LinearLayout with new one showing media message has been opened
     *
     * @param mediaView
     */
    private fun setMediaViewOpened(mediaView: LinearLayout, messageType: MessageViewType) {
        // Remove the TextView and ImageView in the message LinearLayout
        val children = BFSView(mediaView)
        for (child in children) {
            if (child is TextView || child is LinearLayout) {
                mediaView.removeView(child)
            }
        }

        // Replace with new media LinearLayout
        val openedMediaLinearLayout = MediaOpenedViewFactory.createOpenedMediaView(this, messageType)
        mediaView.addView(openedMediaLinearLayout)
    }

    /**
     * Breadth-first search the view and return a list
     * containing itself and all of it's children
     *
     * @param view the parent view to search
     * @return
     */
    private fun BFSView(view: View): List<View> {
        val visited: MutableList<View> = ArrayList()
        val unvisited: MutableList<View> = ArrayList()
        unvisited.add(view)

        // Visit each view
        while (unvisited.isNotEmpty()) {
            val child = unvisited.removeAt(0)
            visited.add(child)

            if (child !is ViewGroup) continue
            val childCount = child.childCount

            // Add each of the view's views
            for (i in 0 until childCount) {
                unvisited.add(child.getChildAt(i))
            }
        }

        return visited
    }

    /**
     * Add padding to the linear layout
     *
     * @param linearLayout
     */
    private fun addPadding(linearLayout: LinearLayout) {
        linearLayout.setPadding(0, 20, 0, 20)
    }

}
