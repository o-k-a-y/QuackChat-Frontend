package edu.ramapo.btunney.quackchat.dao

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper

class database : AppDatabase() {
    override fun userDao(): UserDao {
//        TODO("Not yet implemented")
        return userDao()
    }

    override fun createOpenHelper(config: DatabaseConfiguration?): SupportSQLiteOpenHelper {
//        TODO("Not yet implemented")

        return object: SupportSQLiteOpenHelper {
            override fun getDatabaseName(): String {
                TODO("Not yet implemented")
            }

            override fun getWritableDatabase(): SupportSQLiteDatabase {
                TODO("Not yet implemented")
            }

            override fun getReadableDatabase(): SupportSQLiteDatabase {
                TODO("Not yet implemented")
            }

            override fun close() {
                TODO("Not yet implemented")
            }

            override fun setWriteAheadLoggingEnabled(enabled: Boolean) {
                TODO("Not yet implemented")
            }

        }
    }

    override fun createInvalidationTracker(): InvalidationTracker {
//        TODO("Not yet implemented")
        return invalidationTracker
    }

    override fun clearAllTables() {
//        TODO("Not yet implemented")
    }

}