package edu.ramapo.btunney.quackchat.networking

/**
 * Enum class for server routes to distinguish where the data is being sent
 */
enum class ServerRoutes(val route: String) {
    LOGIN("/users/login"),
    LOGOUT("/logout"),
    SIGNUP("/users"),
    ME("/users/me"),
    AUTH("/auth"),
    ADD_FRIEND("/users/friends/add"),
    DELETE_FRIEND("/users/friends/delete"),
    FETCH_FRIENDS("/users/friends/fetch"),
    SEND_MESSAGE("/users/message/send"),
    CHECK_HASH("/users/hash/check"),
    FETCH_MESSAGES("/users/messages/fetch")


}