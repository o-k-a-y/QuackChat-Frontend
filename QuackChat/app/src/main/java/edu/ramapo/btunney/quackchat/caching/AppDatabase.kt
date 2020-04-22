package edu.ramapo.btunney.quackchat.caching

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.ramapo.btunney.quackchat.caching.dao.CacheHashDao
import edu.ramapo.btunney.quackchat.caching.dao.FriendDao
import edu.ramapo.btunney.quackchat.caching.dao.MessageDao
import edu.ramapo.btunney.quackchat.caching.entities.Cache
import edu.ramapo.btunney.quackchat.caching.entities.Friend
import edu.ramapo.btunney.quackchat.caching.entities.Message

@Database(entities = [Cache::class, Friend::class, Message::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cacheHashDao(): CacheHashDao

    abstract fun friendDao(): FriendDao

    abstract fun messageDao(): MessageDao
}