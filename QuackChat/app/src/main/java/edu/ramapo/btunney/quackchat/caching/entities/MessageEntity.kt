package edu.ramapo.btunney.quackchat.caching.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * This entity class represents a message sent from a friend.
 *
 * MessageDao makes use of this Entity
 *
 * @property primaryKey used to distinguish messages
 * @property type type of message (text/picture/video)
 * @property to who the message is to (hopefully the logged in user)
 * @property from who the message is from (hopefully from a friend of the user)
 * @property message the content of the message (may be base64 encoded if not text)
 * @property timeSent number of milliseconds since 1970
 */
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