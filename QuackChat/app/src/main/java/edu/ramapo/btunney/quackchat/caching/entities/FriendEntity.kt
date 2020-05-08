package edu.ramapo.btunney.quackchat.caching.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This entity class represents a friend.
 * A friend has a username, and a large and small profile picture
 *
 * FriendDao makes use of this Entity
 *
 * @property username the friend's username
 * @property imageLarge their large profile picture
 * @property imageSmall their small profile picture
 */
@Entity
data class Friend (
    /**
     * The friend's username
     */
    @PrimaryKey
    val username: String,

    /**
     * The large version of the friend's profile picture
     */
    @ColumnInfo(name = "image_large")
    val imageLarge: String,

    /**
     * The small version of the friend's profile picture
     */
    @ColumnInfo(name = "image_small")
    val imageSmall: String
)
