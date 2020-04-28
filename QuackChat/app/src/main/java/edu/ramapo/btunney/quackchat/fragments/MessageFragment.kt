package edu.ramapo.btunney.quackchat.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.*
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import edu.ramapo.btunney.quackchat.R
import edu.ramapo.btunney.quackchat.caching.entities.Message
import edu.ramapo.btunney.quackchat.views.MessageViewType
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.fragment_message.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

private const val MESSAGE_TYPE = "messageType"
private const val MESSAGE_SENT = "messageSent"


/**
 * A simple [Fragment] subclass.
 * Use the [MessageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MessageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var messageType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            messageType = it.getString(MESSAGE_TYPE)
        }


    }

    /**
     * The function called when the view is being created
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    /**
     * Once the fragment is created, we can add the text/picture/video view to it
     *
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Get the message from the bundle
        val bundle = arguments ?: throw RuntimeException("Bundle for message fragment is empty")
        val message = bundle.getParcelable<Message>(ReceivedMessageBundleKey)

        // TODO: handle all cases of text, picture, or video message

        if (message != null) {
            handleReceivedMessage(message)
        } else {
            val messageSent = bundle.getString(MESSAGE_SENT)
            handleSentMessage(messageSent)
        }
    }

    /**
     * Creates a view containing the message a friend sent you
     *
     * @param message
     */
    private fun handleReceivedMessage(message: Message) {
        when (message.type) {
            MessageViewType.TEXT.type -> {

                val messageTextView = TextView(messageLinearLayout.context)
                messageTextView.text = message.message
                messageLinearLayout.addView(messageTextView)
                messageLinearLayout.setBackgroundColor(Color.YELLOW)
            }
            MessageViewType.PICTURE.type -> {
                // TODO: finish and actually show picture with button and other things
//                val messageTextView = TextView(messageLinearLayout.context)
//                messageTextView.text = "PICTURE TEMP"
//                messageLinearLayout.addView(messageTextView)
//                messageLinearLayout.setBackgroundColor(Color.BLUE)

                // Set height and width of picture button
                val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.width = 50
                params.height = 50
                val pictureImageButton = ImageButton(messageLinearLayout.context)
                pictureImageButton.layoutParams = params

                // Make button red
                pictureImageButton.setBackgroundColor(Color.RED)

                // Add some padding to the view
                val imageLinearLayout = LinearLayout(messageLinearLayout.context)
                imageLinearLayout.addView(pictureImageButton)
                imageLinearLayout.setPadding(0, 20, 0, 20)

                // Add to fragment layout
                messageLinearLayout.addView(imageLinearLayout)

                // Add an onClick to the button so image is displayed in full screen
                messageLinearLayout.setOnClickListener {
                    // Decode and rotate image so it shows normally
                    val decodedString = Base64.decode(message.message, Base64.DEFAULT)
                    var decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    decodedBitmap = rotateImage(decodedBitmap, 90F)


                    // Create image view to display image
                    val pictureView = ImageView(messageLinearLayout.context)
                    pictureView.setImageBitmap(decodedBitmap)


                    val test = getActivity()?.findViewById<FrameLayout>(R.id.mediaFrameLayout)
                    test?.addView(pictureView)

//                    messageLinearLayout.isClickable = false

//                    messageLinearLayout.addView(pictureView)

                    Log.d("@CLICK", "click")

                }

            }
            MessageViewType.VIDEO.type -> {
                // TODO
                val messageTextView = TextView(messageLinearLayout.context)
                messageTextView.text = "VIDEO TEMP"
                messageLinearLayout.addView(messageTextView)
                messageLinearLayout.setBackgroundColor(Color.MAGENTA)
            }
            else -> {
                // TODO
            }
        }
    }

    /**
     * Creates a temporary TextView containing the text you sent to a friend
     *
     * @param messageSent the message you sent to a friend
     */
    private fun handleSentMessage(messageSent: String?) {
        // This is the message you sent to a friend
        // TODO: probably awful design

        if (messageSent != null) {
            val messageTextView = TextView(messageLinearLayout.context)
            messageTextView.text = messageSent

            // Right justify the text
            messageTextView.textAlignment = View.TEXT_ALIGNMENT_VIEW_END

            messageLinearLayout.addView(messageTextView)
            messageLinearLayout.setBackgroundColor(Color.GREEN)
        }
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

    // TODO might not need this
    companion object {
        const val ReceivedMessageBundleKey = "message"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param messageType The type of message
         * @return A new instance of fragment MessageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(messageType: String, bundle: Bundle): MessageFragment =
                MessageFragment().apply {
                    arguments = Bundle().apply {
                        putString(MESSAGE_TYPE, messageType)
                    }
                }
    }
}
