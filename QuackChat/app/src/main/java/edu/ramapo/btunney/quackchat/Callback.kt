package edu.ramapo.btunney.quackchat

/**
 * Used to handle callbacks
 *
 * @param E the type of data
 */
interface Callback<E> {
    /**
     * This function will handle what action to perform
     *
     * @param data any relevant data to be used
     * @param error any relevant errors
     */
    fun perform(data: E?, error: Throwable?)
}