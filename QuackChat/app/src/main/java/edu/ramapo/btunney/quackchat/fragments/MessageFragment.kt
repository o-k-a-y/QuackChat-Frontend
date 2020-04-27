package edu.ramapo.btunney.quackchat.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import edu.ramapo.btunney.quackchat.R
import edu.ramapo.btunney.quackchat.caching.entities.Message
import edu.ramapo.btunney.quackchat.views.MessageViewType
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_message.*
import kotlinx.android.synthetic.main.fragment_message.view.*


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

    private fun handleReceivedMessage(message: Message) {
        when (message.type) {
            MessageViewType.TEXT.type -> {

                val messageTextView = TextView(messageLinearLayout.context)
                messageTextView.text = message.message
                messageLinearLayout.addView(messageTextView)
                messageLinearLayout.setBackgroundColor(Color.RED)
            }
            MessageViewType.PICTURE.type -> {
                // TODO: finish and actually show picture with button and other things
                val messageTextView = TextView(messageLinearLayout.context)
                messageTextView.text = "PICTURE TEMP"
                messageLinearLayout.addView(messageTextView)
                messageLinearLayout.setBackgroundColor(Color.BLUE)
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
