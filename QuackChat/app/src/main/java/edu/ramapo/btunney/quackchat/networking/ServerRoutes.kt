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
}