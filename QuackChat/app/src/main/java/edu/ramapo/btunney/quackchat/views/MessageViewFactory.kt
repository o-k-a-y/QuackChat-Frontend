package edu.ramapo.btunney.quackchat.views

import android.content.Context
import android.drm.DrmStore
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import edu.ramapo.btunney.quackchat.caching.entities.Message

class MessageViewFactory {
    companion object {

        private lateinit var messageLinearLayout: LinearLayout

        fun createMessageView(context: Context, message: Message?, messageSent: String?): LinearLayout {

            messageLinearLayout = LinearLayout(context)
            // TODO: handle all cases of text, picture, or video message

            if (message != null) {
                return handleReceivedMessage(context, message)
            } else {
                return handleSentMessage(context, messageSent)
            }
        }

        /**
         * Creates a view containing the message a friend sent you
         *
         * @param message
         */
        private fun handleReceivedMessage(context: Context, message: Message): LinearLayout {
            when (message.type) {
                MessageViewType.TEXT.type -> {

                    val messageTextView = TextView(context)
                    messageTextView.text = message.message
                    messageTextView.setBackgroundColor(Color.BLACK)
                    messageLinearLayout.addView(messageTextView)
                    messageLinearLayout.setBackgroundColor(Color.YELLOW)
                }
                MessageViewType.PICTURE.type -> {

                    // Set height and width of picture button
                    val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    params.width = 50
                    params.height = 50
                    val pictureImageButton = ImageButton(context)
                    pictureImageButton.layoutParams = params

                    // Make button red
                    pictureImageButton.setBackgroundColor(Color.RED)

                    val imageLinearLayout = LinearLayout(context)
                    imageLinearLayout.addView(pictureImageButton)

//                    // Add some padding to the view
//                    imageLinearLayout.setPadding(0, 20, 0, 20)

                    // Add to fragment layout
                    messageLinearLayout.addView(imageLinearLayout)



                }
                MessageViewType.VIDEO.type -> {
                    // TODO
                    val messageTextView = TextView(context)
                    messageTextView.text = "VIDEO TEMP"
                    messageLinearLayout.addView(messageTextView)
                    messageLinearLayout.setBackgroundColor(Color.MAGENTA)
                }
                else -> {
                    // TODO
                }
            }
            return messageLinearLayout
        }

        /**
         * Creates a temporary TextView containing the text you sent to a friend
         *
         * @param messageSent the message you sent to a friend
         */
        private fun handleSentMessage(context: Context, messageSent: String?): LinearLayout {
            // This is the message you sent to a friend
            val messageTextView = TextView(context)
            messageTextView.text = messageSent

            // Needed for TextView so we can right justify the text
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            messageTextView.layoutParams = params

            // Right justify the text
            messageTextView.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
//            messageTextView.setBackgroundColor(Color.BLACK)

            messageLinearLayout.addView(messageTextView)
            messageLinearLayout.setBackgroundColor(Color.GREEN)
            
            return messageLinearLayout
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
    }
}