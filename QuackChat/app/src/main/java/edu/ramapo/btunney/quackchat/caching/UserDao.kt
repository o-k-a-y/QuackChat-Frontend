package edu.ramapo.btunney.quackchat.caching

import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE username IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): User

    // To prevent inserting same data twice
    @Query("SELECT count(*)!=0 FROM user Where username=1")
    fun userAlreadyExists(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(user: User)

    @Update
    fun updateOne(user: User)

    @Insert
    fun insertAll(users: Array<User>)

    @Delete
    fun delete(user: User)
}
