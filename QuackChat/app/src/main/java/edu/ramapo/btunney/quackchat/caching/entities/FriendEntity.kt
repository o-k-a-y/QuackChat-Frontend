package edu.ramapo.btunney.quackchat.caching.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Friend (
    @PrimaryKey val username: String,
    @ColumnInfo(name = "image_large") val imageLarge: String?,
    @ColumnInfo(name = "image_small") val imageSmall: String?
)
