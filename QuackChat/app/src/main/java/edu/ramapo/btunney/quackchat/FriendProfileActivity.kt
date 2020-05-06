package edu.ramapo.btunney.quackchat

import android.content.Intent
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
    var friendUsername: String = ""
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
//        var username: String = ""
        if (extras != null) {
            friendUsername = extras.getString("username").toString()
        }

        if (friendUsername == "") {
            finish()
        }

        // Display friend information
        displayFriendProfile(friendUsername)
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

            val friend = RoomDatabaseDAO.getInstance(applicationContext).getFriendByName(username)
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
        RoomDatabaseDAO.getInstance(applicationContext).deleteFriend(friendUsername)
        RoomDatabaseDAO.getInstance(applicationContext).deleteMessages(friendUsername)
    }

    /**
     * Delete the friend whose profile you're on
     *
     * @param view the delete friend button
     */
    fun deleteFriendOnClick(view: View) {
        // TODO: this might not correctly refresh list of friends, check when hitting back on FriendListActivity

        NetworkRequester.deleteFriend(ServerRoutes.DELETE_FRIEND, friendUsername, object: NetworkCallback {
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
