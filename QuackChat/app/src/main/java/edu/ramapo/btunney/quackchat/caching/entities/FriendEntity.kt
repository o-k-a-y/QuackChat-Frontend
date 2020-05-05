package edu.ramapo.btunney.quackchat.caching.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This entity class represents a friend.
 * A friend has a username, and a large and small profile picture
 *
 * @property username the friend's username
 * @property imageLarge their large profile picture
 * @property imageSmall their small profile picture
 */
@Entity
data class Friend (
    @PrimaryKey
    val username: String,

    @ColumnInfo(name = "image_large")
    val imageLarge: String,

    @ColumnInfo(name = "image_small")
    val imageSmall: String
)
