package edu.ramapo.btunney.quackchat.caching.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import edu.ramapo.btunney.quackchat.caching.entities.Message

@Dao
interface MessageDao {
    @Query("SELECT * FROM message")
    fun getAll(): List<Message>

    @Query("SELECT * FROM message WHERE toWhom = :to")
    fun getAllToFriend(to: String): List<Message>

    @Query("SELECT * FROM message WHERE fromWhom = :from")
    fun getAllFromFriend(from: String): List<Message>

    // TODO: make query to delete all message from a friend
    // TODO: make query to delete all messages of type text from a friend

    @Insert
    fun insertOne(message: Message)

    @Query("DELETE FROM message")
    fun nukeTable()
}