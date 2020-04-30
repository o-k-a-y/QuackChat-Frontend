package edu.ramapo.btunney.quackchat.utils

/**
 * Used to handle callbacks
 *
 * @param E
 */
interface Callback<E> {
    /**
     * This function will handle what action to perform
     *
     * @param data any relevant data to be used
     * @param error any relevant errors
     */
    fun perform(data: E?, error:Throwable?)
}