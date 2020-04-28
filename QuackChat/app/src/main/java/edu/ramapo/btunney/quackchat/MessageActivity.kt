package edu.ramapo.btunney.quackchat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.room.Room
import edu.ramapo.btunney.quackchat.caching.AppDatabase
import edu.ramapo.btunney.quackchat.caching.entities.Cache
import edu.ramapo.btunney.quackchat.caching.entities.Message
import edu.ramapo.btunney.quackchat.fragments.MessageFragment
import edu.ramapo.btunney.quackchat.networking.MessageType
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import edu.ramapo.btunney.quackchat.views.MediaOpenedViewFactory
import edu.ramapo.btunney.quackchat.views.MessageViewFactory
import edu.ramapo.btunney.quackchat.views.MessageViewType
import kotlinx.android.synthetic.main.activity_message.*
import org.json.JSONObject

/**
 * This activity is where you can send text messages to a friend
 * and also view text messages the friend has sent as well as
 * any pictures and videos they might have sent
 *
 */
class MessageActivity : AppCompatActivity() {
    private lateinit var friend: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        // Get username of friend from intent
        val extras = intent.extras
        if (extras != null) {
            friend = extras.getString("username").toString()
        }

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

    private fun clearMessageCache() {
        Thread {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "CacheTest").build()
            db.messageDao().nukeTable()

            if(db.isOpen) {
                db.openHelper.close()
            }
        }
    }


    private fun showFriendUsername() {
        friendUsernameTextView.text = friend
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
        NetworkRequester.sendMessage(ServerRoutes.SEND_MESSAGE, arrayOf(friend), message, MessageType.TEXT, object: NetworkCallback {
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

    private fun fetchMessages() {
        Thread {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "CacheTest").build()
            val hash = db.cacheHashDao().getHash("messages")

            if(db.isOpen) {
                db.openHelper.close()
            }

            val json = "{\"hash\":\"$hash\"}"
            val hashJSON = JSONObject(json)

            hashJSON.put("hashType", "messages")


            NetworkRequester.validateHash(ServerRoutes.CHECK_HASH, hashJSON, object: NetworkCallback {
                override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                    TODO()
                }

                // Check if hashes match
                // If hashes don't match, get new list of friends
                override fun onSuccess(data: Any?) {
                    // Hashes don't match, load cached friends
                    if (JSONObject(data.toString()).getString("hash").toString() != hashJSON.getString("hash").toString()) {
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

                val newHash = messageJSON.getString("messagesHash")
                val messages = messageJSON.getJSONArray("messages")

                // Make new thread to handle access to database so it doesn't run on main UI thread
                Thread {
                    val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "CacheTest").build()

                    // Insert any new friends into User table
                    for (i in 0 until messages.length()) {
                        val messageData = messages.getJSONObject(i)

                        // Get fields from JSON
                        val type: String = messageData.getString("type")
                        val to: String = messageData.getString("to")
                        val from: String = messageData.getString("from")
                        val message: String = messageData.getString("message")
                        val timeSent: String = messageData.getString("timeSent")

                        // Insert into local DB
                        val messageEntity = Message(0, type, to, from, message, timeSent)
                        db.messageDao().insertOne(messageEntity)
                    }

                    // Insert hash of friend list into Cache table
                    val cache = Cache("messages", newHash)
                    db.cacheHashDao().insertOne(cache)


                    // Print all message
                    for (message in db.messageDao().getAllFromFriend(friend)) {
                        Log.i("@RoomDB message: ", message.toString())
                    }

                    // Check messages hash
                    val messageHash = db.cacheHashDao().getHash("messages")
                    Log.d("@RoomDB messages Hash: ", messageHash)


                    if(db.isOpen) {
                        db.openHelper.close()
                    }

                    // TODO: pass some actual data instead of null so we know what changed
                    callback.perform(null, null)
                }.start()
            }
        })
    }

    /**
     * Load all the messages from the friend
     *
     */
    private fun loadMessages() {
        Log.d("@Load messages", "TODO")

        // Get messages
        Thread {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "CacheTest").build()
            for (message in db.messageDao().getAllFromFriend(friend)) {
//                runOnUiThread {

                // TODO
                makeMessageLinearLayout(message)
//                    makeMessageFragment(message)
//                }
            }
            if(db.isOpen) {
                db.openHelper.close()
            }
        }.start()


    }

    /**
     * Create a LinearLayout from message data and add to message list
     *
     * @param message
     */
    private fun makeMessageLinearLayout(message: Message) {
        runOnUiThread {
            Runnable {
                val messageLinearLayout = MessageViewFactory.createMessageView(this, message, null)

                messagesLinearLayout.addView(messageLinearLayout)

                // Add padding
                addPadding(messageLinearLayout)

                addOnClickToPictureView(messageLinearLayout, message)

            }.run()
        }


    }


    /**
     * Create a LinearLayout from the text message sent and add to message list
     *f
     * @param message
     */
    private fun makeMessageLinearLayout(message: String) {
        runOnUiThread {
            Runnable {
                val messageLinearLayout = MessageViewFactory.createMessageView(this, null, message)

                // Add padding
                addPadding(messageLinearLayout)

                messagesLinearLayout.addView(messageLinearLayout)
            }.run()
        }

    }


    /**
     * Rotate a bitmap x degrees
     * This is used when taking a picture because by default the image comes in landscape (horizontal)
     * We really only allow vertical images
     *
     * @param source
     * @param angle
     * @return
     */
    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
                matrix, true)
    }

    private fun addOnClickToPictureView(mediaView: LinearLayout, message: Message) {
        // TODO: add onClick to layout
        // Add an onClick to the button so image is displayed in full screen
        mediaView.setOnClickListener {
            // Decode and rotate image so it shows normally
            val decodedString = Base64.decode(message.message, Base64.DEFAULT)
            var decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            decodedBitmap = rotateImage(decodedBitmap, 90F)


            // Create image view to display image
            val pictureView = ImageView(this)
            pictureView.setImageBitmap(decodedBitmap)

            mediaFrameLayout.addView(pictureView)

            setMediaViewOpened(mediaView)

            mediaView.isClickable = false
            Log.d("@CLICK", "click")
        }
    }


    /**
     * Change what the media view pictures (picture and video) look like
     *
     * @param mediaView
     */
    private fun setMediaViewOpened(mediaView: LinearLayout) {
        mediaView.removeAllViews()
        mediaView.addView(MediaOpenedViewFactory.createOpenedMediaView(this, MessageViewType.PICTURE))
    }

    /**
     * Add padding to the linear layout
     *
     * @param linearLayout
     */
    private fun addPadding(linearLayout: LinearLayout) {
        linearLayout.setPadding(0, 20, 0, 20)
    }

    private fun decodeImage() {

    }

    /**
     * Create fragment using the message data
     *
     */
    private fun makeMessageFragment(message: Message) {
        // Shove fragments into linear layout
        val bundle = Bundle()
        bundle.putParcelable(MessageFragment.ReceivedMessageBundleKey, message)
        val fragment = MessageFragment()
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction()
                .add(R.id.messagesLinearLayout, fragment)
                .commit()
    }

    /**
     * Create fragment using the text message sent
     *
     */
    private fun makeMessageFragment(message: String) {
        val bundle = Bundle()
        bundle.putString("messageSent", message)
        val fragment = MessageFragment()
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction()
                .add(R.id.messagesLinearLayout, fragment)
                .commit()
    }

}
