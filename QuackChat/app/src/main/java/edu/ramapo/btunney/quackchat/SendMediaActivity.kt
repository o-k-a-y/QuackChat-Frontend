package edu.ramapo.btunney.quackchat

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.FileProvider
import edu.ramapo.btunney.quackchat.networking.MessageType
import kotlinx.android.synthetic.main.activity_send_media.*
import java.io.File

/**
 * This activity is where you send the picture or video you've taken/record
 * The picture/video you took is displayed on the screen with a button to send if you decide to send it.
 * If you click the send button, you will be given your list of friends to decide which friends
 * should receive it.
 *
 */
class SendMediaActivity : AppCompatActivity() {

    /**
     * Display the media depending on what it is
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_media)

        // Disable screen rotations
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        // Hide the top bar in activity
        if (supportActionBar != null)
            supportActionBar?.hide()

        // Check what type of media it is and display
        val typeOfMedia = intent.getStringExtra(MEDIATYPE)

        if (typeOfMedia != null) {
            displayMedia(typeOfMedia)
        }

    }

    /**
     * Display the media (picture/video)
     *
     * @param typeOfMedia the type of media it is
     */
    private fun displayMedia(typeOfMedia: String) {
        when (typeOfMedia) {
            MessageType.PICTURE.type -> {
                // Disable video view
                videoView.visibility = View.INVISIBLE

                displayPicture()
            }
            MessageType.VIDEO.type -> {
                // Display video and have it loop
                loopVideo()
                displayVideo()
            }
        }
    }

    /**
     * Make video loop every time it finishes completion
     *
     */
    private fun loopVideo() {
        videoView.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
            override fun onPrepared(mp: MediaPlayer?) {
                mp?.isLooping = true;
            }

        });
    }

    /**
     * Place the picture in an ImageView and place that inside the FrameLayout
     *
     */
    private fun displayPicture() {
        val imageView = ImageView(this)

        // TODO make this a variable or function somewhere accessible to SendMediaToFriend and SendMedia
        val filePath = cacheDir.absolutePath + "/picture"
        val bitmap = BitmapFactory.decodeFile(filePath)
        imageView.setImageBitmap(bitmap)
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        mediaFrameLayout.addView(imageView)
    }

    /**
     * Place the video in the VideoView and play it
     *
     */
    private fun displayVideo() {
        // TODO make this a variable or function somewhere accessible to SendMediaToFriend and SendMedia
        val file = File(cacheDir.absolutePath + "/video")
        val uri = FileProvider.getUriForFile(this, this.packageName + ".provider", file)
        videoView.setVideoURI(uri)
        videoView.start()
    }


    /**
     * Display the list of friends to send the media to then allow to send
     *
     * @param view
     */
    fun sendToFriendsOnClick(view: View) {
        val typeOfMedia = intent.getStringExtra(MEDIATYPE)
        val intent = Intent(this, SendMediaToFriends::class.java)
        intent.putExtra(MEDIATYPE, typeOfMedia)
        startActivity(intent)

        // TODO: maybe figure out way to come back to this state using finishAffinity()
        finish()
    }


    /**
     * Used when passing intents to this Activity
     */
    companion object {
        val MEDIATYPE = "mediaType"
    }
}
