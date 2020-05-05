package edu.ramapo.btunney.quackchat.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Base64
import android.util.Log
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import edu.ramapo.btunney.quackchat.FriendProfileActivity
import edu.ramapo.btunney.quackchat.MessageActivity
import edu.ramapo.btunney.quackchat.caching.entities.Friend

/**
 * This factory contains a method to return a LinearLayout representing a friend profile or list of friends
 * The friend profile will contain their username, their profile picture, and a button to delete them (FriendProfileActivity)
 * The friend list will be the list of friends you see in FriendListActivity
 */
class FriendViewFactory {

    companion object {
        @Synchronized
        fun createFriendView(context: Context, friendViewType: FriendViewType, friend: Friend): LinearLayout {

            val linearLayout = LinearLayout(context)


            val image = when (friendViewType) {
                FriendViewType.LIST -> {
                    decodeImage(context, friend.imageSmall)
                }
                FriendViewType.PROFILE -> {
                    decodeImage(context, friend.imageLarge)
                }
                else -> null
            } ?: throw NullPointerException("Image is null. Wrong FriendViewType passed in: $friendViewType")

            // TODO: Clean up this code
            when (friendViewType) {
                FriendViewType.LIST -> {
                    val duckLinearLayout = LinearLayout(context)

                    linearLayout.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, 150)
                    linearLayout.setPadding(20, 20, 20, 20)

                    image.cropToPadding = true
                    image.scaleType = ImageView.ScaleType.FIT_START
                    image.adjustViewBounds = true


                    // When friend image is clicked
                    image.setOnClickListener(object: View.OnClickListener {
                        override fun onClick(v: View?) {
                            val intent = Intent(context, FriendProfileActivity::class.java)
                            intent.putExtra("username", friend.username)

                            // Used to determine when to kill activity when friend is deleted
                            if (context !is Activity) return
                            context.startActivityForResult(intent, 999)

                            Log.d("@Friend image click", friend.username)
                        }

                    })

                    // When the friend username is clicked
                    linearLayout.setOnClickListener(object: View.OnClickListener {
                        override fun onClick(v: View?) {
                            val intent = Intent(context, MessageActivity::class.java)
                            intent.putExtra("username", friend.username)
                            context.startActivity(intent)
                            Log.d("@Friend linearlay click", friend.username)
                        }

                    })

                    duckLinearLayout.addView(image)
                    duckLinearLayout.setBackgroundColor(Color.RED)

                    duckLinearLayout.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)


                    linearLayout.addView(duckLinearLayout)

                    val usernameView = TextView(context)
                    usernameView.setPadding(20, 0, 20, 20)
                    usernameView.setBackgroundColor(Color.GREEN)
                    usernameView.text = friend.username.also { linearLayout.addView(usernameView)}


                    val gradientDrawable = GradientDrawable()
                    gradientDrawable.setStroke(4, Color.BLACK)
                    linearLayout.background = gradientDrawable
                }
                FriendViewType.PROFILE -> {
                    linearLayout.orientation = LinearLayout.VERTICAL;
                    val duckLinearLayout = LinearLayout(context)
                    linearLayout.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)

                    image.cropToPadding = true
                    image.scaleType = ImageView.ScaleType.FIT_START
                    image.adjustViewBounds = true

                    duckLinearLayout.addView(image)
                    duckLinearLayout.setBackgroundColor(Color.RED)

                    duckLinearLayout.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)


                    linearLayout.addView(duckLinearLayout)

                    val usernameView = TextView(context)
                    usernameView.gravity = Gravity.CENTER_HORIZONTAL
                    usernameView.setPadding(20, 300, 20, 20)
                    usernameView.setBackgroundColor(Color.GREEN)
                    usernameView.text = friend.username.also { linearLayout.addView(usernameView)}
                }
            }
            return linearLayout
        }

        /**
         * Base64 decode the image data into an ImageView
         *
         * @param context application context
         * @param imageBase64 the base64 encoded byte array representing a picture
         * @return
         */
        private fun decodeImage(context: Context, imageBase64: String): ImageView {
            val decodedString = Base64.decode(imageBase64, Base64.DEFAULT)
            val decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            val image = ImageView(context)
            image.setImageBitmap(decodedBitmap)

            return image
        }

    }
}