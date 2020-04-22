package edu.ramapo.btunney.quackchat.caching.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import edu.ramapo.btunney.quackchat.caching.entities.Message

@Dao
interface MessageDao {
    @Query("SELECT * FROM message")
    fun getAll(): List<Message>

    @Insert
    fun insertOne(message: Message)
}