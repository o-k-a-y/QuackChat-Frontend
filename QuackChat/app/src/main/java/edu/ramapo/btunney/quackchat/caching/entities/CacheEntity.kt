package edu.ramapo.btunney.quackchat.caching.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cache(
    @PrimaryKey val cacheType: String,
    @ColumnInfo(name = "hash") val hash: String
)