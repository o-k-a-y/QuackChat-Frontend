package edu.ramapo.btunney.quackchat.networking

/**
 * Enum class to distinguish the type of message being sent
 *
 */
enum class MessageType(val type: String) {
    TEXT("text"),
    PICTURE("picture"),
    VIDEO("video")
}