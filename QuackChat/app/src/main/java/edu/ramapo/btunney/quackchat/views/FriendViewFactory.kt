package edu.ramapo.btunney.quackchat.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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
import edu.ramapo.btunney.quackchat.caching.entities.Friend
import java.lang.NullPointerException
import java.util.*

class FriendViewFactory {

    companion object {
        private lateinit var linearLayout: LinearLayout
        private lateinit var mDetector: GestureDetectorCompat

        @Synchronized
        fun createFriendView(context: Context, friendViewType: FriendViewType, friend: Friend): LinearLayout {

            linearLayout = LinearLayout(context)

            val image = when (friendViewType) {
                FriendViewType.LIST -> {
                    decodeImage(context, friend.imageSmall)

                }
                FriendViewType.SINGLE -> decodeImage(context, friend.imageLarge)
                else -> null
            } ?: throw NullPointerException("Image is null. Wrong FriendViewType passed in: $friendViewType")

            // TODO: Make a separation between this and a single/profile friend view
            mDetector = GestureDetectorCompat(context, MyGestureListener())

            val duckLinearLayout = LinearLayout(context)

            val lp = LinearLayout.LayoutParams(MATCH_PARENT, 150)


            linearLayout.layoutParams = lp
            linearLayout.setPadding(20, 20, 20, 20)


            image.cropToPadding = true
            image.scaleType = ImageView.ScaleType.FIT_START
            image.adjustViewBounds = true

            duckLinearLayout.addView(image)
            duckLinearLayout.setBackgroundColor(Color.RED)

            duckLinearLayout.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
//            duckLinearLayout.setPadding(20, 20, 20, 20)


            linearLayout.addView(duckLinearLayout)


            val usernameView = TextView(context)
            usernameView.setPadding(20, 0, 20, 20)
            usernameView.setBackgroundColor(Color.GREEN)
            usernameView.text = friend.username.also { linearLayout.addView(usernameView)}

            addGestureListener()

            return linearLayout
        }

        private fun addGestureListener() {

            linearLayout.setOnTouchListener(object: View.OnTouchListener {
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


        private class MyGestureListener : GestureDetector.SimpleOnGestureListener() {

            override fun onDown(event: MotionEvent): Boolean {
                Log.d("@Factory on click", "onDown: $event")
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