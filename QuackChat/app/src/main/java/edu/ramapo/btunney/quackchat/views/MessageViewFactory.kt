package edu.ramapo.btunney.quackchat.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.*
import edu.ramapo.btunney.quackchat.R
import edu.ramapo.btunney.quackchat.caching.entities.Message

/**
 * This factory contains a method to create a LinearLayout representing a message.
 * The message will either:
 *      1. Just text if the message type = text
 *      2. A red square that can be clicked to display the image in fullscreen if type = picture
 *      3. A purple square that can be clicked to display the video in fullscreen if type = video
 *
 */
class MessageViewFactory {
    companion object {

        private lateinit var messageLinearLayout: LinearLayout

        /**
         * Creates and returns a LinearLayout containing message information
         * A simple text message will be a TextView inside a LinearLayout
         * A picture will be a button inside a LinearLayout containing an onClick to display the containing image
         * A video will be a button inside a LinearLayout containing an onClick to display the containing video
         *
         * @param context the context of the activity in which to the LinearLayout will be displayed
         * @param message the Message Entity object containing the received message data
         * @param messageSent a string containing the text message you sent to a friend
         * @return a LinearLayout containing message information
         */
        fun createMessageView(context: Context, message: Message?, messageSent: String?): LinearLayout {

            messageLinearLayout = LinearLayout(context)
            messageLinearLayout.setBackgroundColor(Color.WHITE)

            // Create black border around each message and set background to white
            val gradientDrawable = GradientDrawable()
            gradientDrawable.setStroke(4, Color.BLACK)
            gradientDrawable.setColor(Color.WHITE)
            messageLinearLayout.background = gradientDrawable

            // Message Entity will be null if we are only sending text
            if (message != null) {
                return handleReceivedMessage(context, message)
            } else {
                return handleSentMessage(context, messageSent)
            }
        }

        /**
         * Creates a view containing the message a friend sent you
         *
         * @param context the context of the activity you want the layout displayed to
         * @param message the message you've received
         * @return
         */
        private fun handleReceivedMessage(context: Context, message: Message): LinearLayout {

            // Set height and width of picture button
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.width = 50
            params.height = 50
            val pictureImageButton = ImageButton(context)
            pictureImageButton.layoutParams = params

            when (message.type) {
                MessageViewType.TEXT.type -> {
                    val messageTextView = TextView(context)
                    messageTextView.text = message.message

                    // TODO: PADDING NO
                    messageTextView.setPadding(20, 0, 20, 0)

                    messageLinearLayout.addView(messageTextView)
//                    messageLinearLayout.setBackgroundColor(Color.YELLOW)
                }
                MessageViewType.PICTURE.type -> {

                    // Make button red and add to LinearLayout
                    val imageLinearLayout = LinearLayout(context)

                    // TODO: PADDING NO
                    imageLinearLayout.setPadding(20, 0, 20, 0)

                    pictureImageButton.setImageResource(R.drawable.ic_unopenedpicture)
                    imageLinearLayout.addView(pictureImageButton)

                    messageLinearLayout.addView(imageLinearLayout)
                }
                MessageViewType.VIDEO.type -> {
                    // Make button purple and add to LinearLayout
                    val imageLinearLayout = LinearLayout(context)


                    // TODO: PADDING NO
                    imageLinearLayout.setPadding(20, 0, 20, 0)

                    pictureImageButton.setImageResource(R.drawable.ic_unopenedvideo)
                    imageLinearLayout.addView(pictureImageButton)

                    messageLinearLayout.addView(imageLinearLayout)
                }
                else -> {
                    // TODO: any other types
                }
            }
            return messageLinearLayout
        }

        /**
         * Creates a temporary TextView containing the text you sent to a friend
         *
         * @param context the context of the activity you want the layout displayed to
         * @param messageSent the message you sent to a friend
         * @return a LinearLayout containing a TextView with the text you've sent
         */
        private fun handleSentMessage(context: Context, messageSent: String?): LinearLayout {
            // This is the message you sent to a friend
            val messageTextView = TextView(context)
            messageTextView.text = messageSent

            // TODO: PADDING NO
            messageTextView.setPadding(20, 0, 20, 0)

            // Needed for TextView so we can right justify the text
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            messageTextView.layoutParams = params

            // Right justify the text
            messageTextView.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
//            messageTextView.setBackgroundColor(Color.BLACK)

            messageLinearLayout.addView(messageTextView)
//            messageLinearLayout.setBackgroundColor(Color.GREEN)
            
            return messageLinearLayout
        }
    }
}