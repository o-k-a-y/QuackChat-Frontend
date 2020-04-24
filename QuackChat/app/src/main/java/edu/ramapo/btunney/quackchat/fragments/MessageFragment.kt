package edu.ramapo.btunney.quackchat.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import edu.ramapo.btunney.quackchat.R
import edu.ramapo.btunney.quackchat.caching.entities.Message
import edu.ramapo.btunney.quackchat.views.MessageViewType
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.fragment_message, container, false)

        // Get the message from the bundle
        val bundle = arguments
        val message = bundle?.getParcelable<Message>("message")

        messageType = bundle?.getString("messageType")
        Log.d("@Message fragment", messageType ?: "null")


        // TODO: handle all cases of text, picture, or video message

        if (message != null) {
            when (message.type) {
                MessageViewType.TEXT.type -> {
                    inf.messageTextView.text = message.message
                    inf.setBackgroundColor(Color.RED)
                }
                MessageViewType.PICTURE.type -> {
                    // TODO
                }
                MessageViewType.VIDEO.type -> {
                    // TODO
                }
                else -> {
                    // TODO
                }
            }
        } else {
            // TODO: probably awful design
            val messageSent = bundle?.getString(MESSAGE_SENT)
            if (messageSent != null) {
                inf.messageTextView.text = messageSent
                inf.setBackgroundColor(Color.GREEN)

                // Right justify the text
                inf.messageTextView.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
            }
        }

        return inf
    }

    // TODO might not need this
    companion object {
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
