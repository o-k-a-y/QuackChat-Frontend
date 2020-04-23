package edu.ramapo.btunney.quackchat.caching.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Message (
        @PrimaryKey(autoGenerate = true)
        val primaryKey: Int,

        @ColumnInfo(name = "type")
        val type: String,

        @ColumnInfo(name = "toWhom")
        val to: String,

        @ColumnInfo(name = "fromWhom")
        val from: String,

        @ColumnInfo(name = "message")
        val message: String,

        @ColumnInfo(name = "time_sent")
        val timeSent: String
) : Parcelable