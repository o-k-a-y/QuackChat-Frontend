package edu.ramapo.btunney.quackchat.networking

/**
 * Handle success and failures for HTTP calls
 *
 */
interface NetworkCallback {
    // TODO add all the other failure codes for whatever actions
    enum class FailureCode {
        DEFAULT,
        INVALID_LOGIN,
        DUPLICATE_USER,
        NOT_AUTHENTICATED,
        DOES_NOT_EXIST,
        ALREADY_ADDED,
    }

    /**
     * Set failure code if call is not successful
     *
     * @param failureCode
     */
    fun onFailure(failureCode: FailureCode)

    /**
     * Return data or null if there is none
     *
     * @param data
     */
    fun onSuccess(data: Any?)
}