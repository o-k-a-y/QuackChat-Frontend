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

class FriendViewFactory {

    companion object {
//        private lateinit var linearLayout: LinearLayout
        private lateinit var mDetector: GestureDetectorCompat

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
                    mDetector = GestureDetectorCompat(context, MyGestureListener(friend.username))

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

        // TODO: BROKEN AND ONLY WORKS ON THE LAST THING PASSED
        private fun addGestureListener(view: View) {

            view.setOnTouchListener(object: View.OnTouchListener {
                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    if (mDetector.onTouchEvent(event)) {
                        return true
                    }
                    return false
                }
            })

        }

        private fun decodeImage(context: Context, imageBase64: String): ImageView {
            val decodedString = Base64.decode(imageBase64, Base64.DEFAULT)
            val decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            val image = ImageView(context)
            image.setImageBitmap(decodedBitmap)

            return image
        }


        private class MyGestureListener(val username: String) : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(event: MotionEvent): Boolean {
                Log.d("@Factory on click", "onDown: $event")
                Log.d("@Factory on click", username)
                return true
            }

            override fun onFling(
                    event1: MotionEvent,
                    event2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
            ): Boolean {
                Log.d("@Factory on click", "onFling: $event1 $event2")
                return true
            }
        }
    }
}