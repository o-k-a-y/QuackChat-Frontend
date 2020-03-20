package edu.ramapo.btunney.quackchat.Networking

/**
 * Handle success and failures for HTTP call
 */
interface NetworkCallback {
    // TODO add all the other failure codes for whatever actions
    enum class FailureCode {
        DEFAULT,
        INVALID_LOGIN,

    }

    fun onFailure(failureCode: FailureCode)

    fun onSuccess()
}