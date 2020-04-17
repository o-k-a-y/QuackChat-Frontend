package edu.ramapo.btunney.quackchat.caching

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.ramapo.btunney.quackchat.caching.dao.CacheDao
import edu.ramapo.btunney.quackchat.caching.dao.FriendDao
import edu.ramapo.btunney.quackchat.caching.entities.Cache
import edu.ramapo.btunney.quackchat.caching.entities.Friend

@Database(entities = [Friend::class, Cache::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun friendDao(): FriendDao

    abstract fun cacheDao(): CacheDao
}