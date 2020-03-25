package edu.ramapo.btunney.quackchat.Networking

/**
 * Enum class for server routes
 */
enum class ServerRoutes(val route: String) {
    LOGIN("/users/login"),
    LOGOUT("/logout"),
    SIGNUP("/users")
}