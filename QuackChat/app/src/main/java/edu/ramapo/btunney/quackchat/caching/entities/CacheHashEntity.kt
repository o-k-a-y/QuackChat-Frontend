package edu.ramapo.btunney.quackchat.caching.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This entity class represents the hash of different data of interest.
 * Such data includes: the list of friends and the messages the user has received.
 *
 * CacheHashDao makes use of this Entity
 *
 * @property cacheType what type of data is it
 * @property hash what the hash is
 */
@Entity
data class Cache (
    /**
     * The type of data we're caching
     */
    @PrimaryKey
    val cacheType: String,

    /**
     * The hash of the piece of data
     */
    @ColumnInfo(name = "hash")
    val hash: String
)