package edu.ramapo.btunney.quackchat.utils

import android.content.Context
import androidx.room.Room
import edu.ramapo.btunney.quackchat.caching.RoomDatabaseDAO

/**
 * TODO
 *
 * @constructor
 * TODO
 *
 * @param applicationContext
 * @param roomDatabaseName
 */
class CCleaner(private val applicationContext: Context, private val roomDatabaseName: String) {

    /**
     * Delete all cache files and all tables in Room DB
     *
     */
    fun wipeCache() {
        deleteCache()
        deleteRoomDB()
    }

    /**
     * Delete all files in cache directory
     *
     */
    private fun deleteCache() {
        applicationContext.cacheDir.deleteRecursively()
    }

    /**
     * Delete all tables in Room DB
     *
     */
    private fun deleteRoomDB() {
        Thread {
            val db = Room.databaseBuilder(applicationContext, RoomDatabaseDAO::class.java, roomDatabaseName).build()

            db.clearAllTables()
            db.close()
        }.start()

    }
}