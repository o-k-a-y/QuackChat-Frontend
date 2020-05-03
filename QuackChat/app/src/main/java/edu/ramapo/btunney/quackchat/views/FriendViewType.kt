package edu.ramapo.btunney.quackchat.views

/**
 * Enum class to distinguish the type of FriendView when displaying friend information
 * LIST is for when you are in the FriendListActivity and want to see everyone's username and profile picture
 * PROFILE is for when you are in the FriendProfileActivity and want to see more about their account
 * CHECKBOX is for when you are sending media (picture/video) and you want to choose who to send it to
 *
 */
enum class FriendViewType {
    LIST,
    PROFILE,
    CHECKBOX, // used when showing a list of friends to send to
}