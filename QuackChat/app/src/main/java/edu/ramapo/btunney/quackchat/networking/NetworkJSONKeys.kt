package edu.ramapo.btunney.quackchat.networking

/**
 * The key names of JSON sent to and from network
 *
 * @property type the name of the key
 */
enum class NetworkJSONKeys(val type: String) {
    HASHTYPE("hashType"),
    HASH("hash"),
    MESSAGESHASH("messagesHash"),
    MESSAGES("messages"),
    TYPE("type"),
    TO("to"),
    FROM("from"),
    MESSAGE("message"),
    TIMESENT("timeSent"),
}