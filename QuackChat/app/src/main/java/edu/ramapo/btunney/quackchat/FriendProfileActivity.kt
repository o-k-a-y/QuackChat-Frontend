package edu.ramapo.btunney.quackchat

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import edu.ramapo.btunney.quackchat.caching.RoomDatabaseDAO
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes
import edu.ramapo.btunney.quackchat.views.FriendViewFactory
import edu.ramapo.btunney.quackchat.views.FriendViewType
import kotlinx.android.synthetic.main.activity_friend_profile.*

/**
 * This activity shows you the friend's profile containing user information
 * like username and profile picture as well as an option to delete them
 *
 */
class FriendProfileActivity : AppCompatActivity() {
    // The friend's username
    private var mFriendUsername: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_profile)

        // Disable screen rotations
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        // Hide the top bar in activity
        if (supportActionBar != null)
            supportActionBar?.hide()

        // Get username of friend from intent
        val extras = intent.extras
        if (extras != null) {
            mFriendUsername = extras.getString("username").toString()
        }

        // No username was set, we probably came to this Activity from deleting a friend
        if (mFriendUsername == "") {
            finish()
        }

        // Display friend information
        displayFriendProfile(mFriendUsername)
    }

    /**
     * Display the friend's profile
     * The profile contains their username and profile picture
     *
     * @param username the friend to display
     */
    private fun displayFriendProfile(username: String) {
        Thread {

            var friendProfile = LinearLayout(this)

            // Get friend data
            val friend = RoomDatabaseDAO.getInstance(applicationContext).getFriendByName(username)

            // Show friend data
            runOnUiThread {
                Runnable {
                    friendProfile = FriendViewFactory.createFriendView(this, FriendViewType.PROFILE, friend)
                    friendProfileLinearLayout.addView(friendProfile)
                }.run()
            }

        }.start()

    }

    /**
     * Deletes the friend as well as their messages from the Room DB cache
     *
     */
    private fun deleteFriendFromCache() {
        RoomDatabaseDAO.getInstance(applicationContext).deleteFriend(mFriendUsername)
        RoomDatabaseDAO.getInstance(applicationContext).deleteMessages(mFriendUsername)
    }

    /**
     * Delete the friend whose profile you're on
     *
     * @param view the delete friend button
     */
    fun deleteFriendOnClick(view: View) {
        NetworkRequester.deleteFriend(ServerRoutes.DELETE_FRIEND, mFriendUsername, object: NetworkCallback {
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                TODO("Not yet implemented")
            }

            /**
             * If successfully delete friend, go back to MessageActivity
             *
             * @param data
             */
            override fun onSuccess(data: Any?) {
                // Delete friend and their messages from cache
                deleteFriendFromCache()

                // TODO: hopefully doesn't break activity stack (hint: it does)

                finish()
//                startActivity(intent)
            }

        })
    }

}
