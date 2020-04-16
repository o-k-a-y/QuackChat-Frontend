package edu.ramapo.btunney.quackchat.networking

/**
 * Enum class for server routes
 */
enum class ServerRoutes(val route: String) {
    LOGIN("/users/login"),
    LOGOUT("/logout"),
    SIGNUP("/users"),
    ME("/users/me"),
    AUTH("/auth"),
    ADD_FRIEND("/users/friends/add"),
    GET_FRIENDS("/users/friends/get"),
    SEND_MESSAGE("/users/message")

}