package edu.ramapo.btunney.quackchat.caching.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Message (
        @PrimaryKey(autoGenerate = true)
        val primaryKey: Int,

        @ColumnInfo(name = "type")
        val type: String,

        @ColumnInfo(name = "to")
        val to: String,

        @ColumnInfo(name = "from")
        val from: String,

        @ColumnInfo(name = "message")
        val message: String,

        @ColumnInfo(name = "time_sent")
        val timeSent: String
)