package edu.ramapo.btunney.quackchat.caching.dao

import androidx.room.*
import edu.ramapo.btunney.quackchat.caching.entities.Cache

/**
 * This DAO supplies queries to insert and retrieve a hash
 * The hash is used to check whether or not the local database is up to date
 * with the remote database
 *
 */
@Dao
interface CacheHashDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(cache: Cache)

    @Query("SELECT hash FROM cache WHERE cacheType = :cacheType")
    fun getHash(cacheType: String): String
}