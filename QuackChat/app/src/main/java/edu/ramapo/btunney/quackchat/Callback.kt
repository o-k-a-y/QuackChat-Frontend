package edu.ramapo.btunney.quackchat

interface Callback<E> {
    fun perform(data: E?, error:Throwable?)
}