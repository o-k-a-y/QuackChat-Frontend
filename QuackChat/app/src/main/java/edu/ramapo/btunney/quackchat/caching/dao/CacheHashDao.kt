package edu.ramapo.btunney.quackchat.caching.dao

import androidx.room.*
import edu.ramapo.btunney.quackchat.caching.entities.Cache

@Dao
interface CacheHashDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(cache: Cache)

    @Query("SELECT hash FROM cache WHERE cacheType = :cacheType")
    fun getHash(cacheType: String): String
}