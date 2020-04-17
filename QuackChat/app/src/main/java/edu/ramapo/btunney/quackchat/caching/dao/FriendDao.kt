package edu.ramapo.btunney.quackchat.caching.dao

import androidx.room.*
import edu.ramapo.btunney.quackchat.caching.entities.Friend

@Dao
interface FriendDao {
    @Query("SELECT * FROM friend")
    fun getAll(): List<Friend>

    @Query("SELECT * FROM friend WHERE username IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Friend>

//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(friend: Friend)

    @Update
    fun updateOne(friend: Friend)

    @Insert
    fun insertAll(friends: Array<Friend>)

    @Delete
    fun delete(friend: Friend)
}
