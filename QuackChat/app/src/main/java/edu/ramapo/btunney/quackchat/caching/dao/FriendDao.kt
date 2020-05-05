package edu.ramapo.btunney.quackchat.caching.dao

import androidx.room.*
import edu.ramapo.btunney.quackchat.caching.entities.Friend

/**
 * This DAO supplies queries to get, insert, update, and delete friends
 *
 */
@Dao
interface FriendDao {
    @Query("SELECT * FROM friend")
    fun getAll(): List<Friend>

//    @Query("SELECT * FROM friend WHERE username IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<Friend>

    @Query("SELECT * FROM friend WHERE username = :username")
    fun findByName(username: String): Friend

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(friend: Friend)

//    @Update
//    fun updateOne(friend: Friend)

//    @Insert
//    fun insertAll(friends: Array<Friend>)

//    @Delete
//    fun delete(friend: Friend)
    @Query("DELETE FROM friend WHERE username = :username")
    fun delete(username: String)
}
