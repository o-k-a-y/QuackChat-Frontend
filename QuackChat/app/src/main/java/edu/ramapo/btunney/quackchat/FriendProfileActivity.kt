package edu.ramapo.btunney.quackchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.room.Room
import edu.ramapo.btunney.quackchat.caching.AppDatabase
import edu.ramapo.btunney.quackchat.views.FriendViewFactory
import edu.ramapo.btunney.quackchat.views.FriendViewType
import kotlinx.android.synthetic.main.activity_friend_profile.*

class FriendProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_profile)

        // Get username of friend from intent
        val extras = intent.extras
        var username: String = ""
        if (extras != null) {
            username = extras.getString("username").toString()
        }

        // Display friend information
        displayFriendProfile(username)
    }

    private fun displayFriendProfile(username: String) {
        Thread {

            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, DATABASE_NAME).build()
            var friendProfile = LinearLayout(this)

            val friend = db.friendDao().findByName(username)
            runOnUiThread {
                Runnable {
                    friendProfile = FriendViewFactory.createFriendView(this, FriendViewType.PROFILE, friend)
                    friendProfileLinearLayout.addView(friendProfile)
                }.run()
            }

            db.close()

        }.start()


    }

}
