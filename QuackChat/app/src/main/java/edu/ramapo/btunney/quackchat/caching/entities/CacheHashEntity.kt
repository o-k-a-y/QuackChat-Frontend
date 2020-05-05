package edu.ramapo.btunney.quackchat.caching.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This entity class represents the hash of different data of interest.
 * Such data includes: the list of friends and the messages the user has received.
 *
 * @property cacheType what type of data is it
 * @property hash what the hash is
 */
@Entity
data class Cache (
    @PrimaryKey
    val cacheType: String,

    @ColumnInfo(name = "hash")
    val hash: String
)