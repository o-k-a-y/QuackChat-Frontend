package edu.ramapo.btunney.quackchat.caching.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import edu.ramapo.btunney.quackchat.caching.entities.Message

/**
 * This DAO supplies queries to get, insert, and delete messages
 *
 * RoomDatabaseDAO makes use of this DAO
 */
@Dao
interface MessageDao {
    @Query("SELECT * FROM message")
    fun getAll(): List<Message>

    @Query("SELECT * FROM message WHERE toWhom = :to")
    fun getAllToFriend(to: String): List<Message>

    @Query("SELECT * FROM message WHERE fromWhom = :from")
    fun getAllFromFriend(from: String): List<Message>

    @Insert
    fun insertOne(message: Message)

    @Query("DELETE FROM message WHERE fromWhom = :from")
    fun deleteAllFromFriend(from: String)

//    @Query("DELETE FROM message")
//    fun nukeTable()
}