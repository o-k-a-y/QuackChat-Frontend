package edu.ramapo.btunney.quackchat.caching

/**
 * The type of hash used for local DB caching
 * Used to check if the data stored locally is up to date by comparing to the remote database
 *
 * @property type the type of hash
 */
enum class HashType(val type: String) {
    MESSAGES("messages"),
    FRIENDLIST("friendList")
}