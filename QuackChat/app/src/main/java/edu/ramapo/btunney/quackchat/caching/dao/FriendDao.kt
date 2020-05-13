package edu.ramapo.btunney.quackchat.caching.dao

import androidx.room.*
import edu.ramapo.btunney.quackchat.caching.entities.Friend

/**
 * This DAO supplies queries to get, insert, update, and delete friends
 *
 * RoomDatabaseDAO makes use of this DAO
 */
@Dao
interface FriendDao {
    @Query("SELECT * FROM friend")
    fun getAll(): List<Friend>

    @Query("SELECT * FROM friend WHERE username = :username")
    fun findByName(username: String): Friend

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(friend: Friend)

    @Query("DELETE FROM friend WHERE username = :username")
    fun delete(username: String)
}
