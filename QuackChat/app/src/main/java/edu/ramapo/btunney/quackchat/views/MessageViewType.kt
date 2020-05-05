package edu.ramapo.btunney.quackchat.views

/**
 * Enum class to distinguish the type of message view (LinearLayout from MessageViewFactory)
 *
 * @property type the type of message
 */
enum class MessageViewType(val type: String) {
    TEXT("text"),
    PICTURE("picture"),
    VIDEO("video"),
    SENT_TEXT("textSent")
}