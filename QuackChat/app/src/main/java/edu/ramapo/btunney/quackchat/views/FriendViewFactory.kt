package edu.ramapo.btunney.quackchat.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
            // What we're returning
            val linearLayout = LinearLayout(context)

            // Set how the image appears
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
                    // Add padding to LinearLayout
                    val duckLinearLayout = LinearLayout(context)
                    linearLayout.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, 150)
                    linearLayout.setPadding(20, 20, 20, 20)

                    // Style image
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

                    // Add image to LinearLayout
                    duckLinearLayout.addView(image)
                    duckLinearLayout.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
                    linearLayout.addView(duckLinearLayout)

                    // Add and style username text
                    val usernameView = TextView(context)
                    styleListText(usernameView)
                    usernameView.text = friend.username.also { linearLayout.addView(usernameView)}

                    // Set black border on LinearLayout and set background to white
                    styleBorderAndBackground(linearLayout)
                }
                FriendViewType.PROFILE -> {
                    linearLayout.orientation = LinearLayout.VERTICAL;

                    // Style and add username text
                    val usernameView = TextView(context)
                    usernameView.text = friend.username
                    styleProfileText(usernameView)
                    linearLayout.addView(usernameView)

                    // Add image to LinearLayout and style
                    val duckLinearLayout = LinearLayout(context)
                    linearLayout.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    styleProfileImage(image)
                    duckLinearLayout.addView(image)
                    duckLinearLayout.setBackgroundColor(Color.RED)
                    duckLinearLayout.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
                    linearLayout.addView(duckLinearLayout)
                }
            }

            return linearLayout
        }

        /**
         * Add black border to layout and make the background white
         *
         * @param linearLayout
         */
        private fun styleBorderAndBackground(linearLayout: LinearLayout) {
            val gradientDrawable = GradientDrawable()
            gradientDrawable.setStroke(4, Color.BLACK)
            gradientDrawable.setColor(Color.WHITE)
            linearLayout.background = gradientDrawable
        }

        /**
         * Add styling to TextView for friend list
         *
         * @param textView
         */
        private fun styleListText(textView: TextView) {
            textView.setPadding(250, 20, 20, 20)
            textView.setTypeface(Typeface.SANS_SERIF)
            textView.setTextSize(20F)
            textView.setTextColor(Color.BLACK)
        }

        /**
         * Add styling to ImageView for friend list
         *
         * @param imageView
         */
        private fun styleListImage(imageView: ImageView) {
            imageView.cropToPadding = true
            imageView.scaleType = ImageView.ScaleType.FIT_START
            imageView.adjustViewBounds = true
        }


        /**
         * Add styling to TextView for friend profile
         *
         * @param textView
         */
        private fun styleProfileText(textView: TextView) {
            textView.setPadding(0, 0, 0, 20)
            textView.setTypeface(Typeface.SANS_SERIF)
            textView.setTextColor(Color.BLACK)
            textView.textSize = 30F
        }

        /**
         * Add styling to ImageView for friend profile
         *
         * @param imageView
         */
        private fun styleProfileImage(imageView: ImageView) {
            imageView.cropToPadding = true
            imageView.scaleType = ImageView.ScaleType.FIT_START
            imageView.adjustViewBounds = true
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