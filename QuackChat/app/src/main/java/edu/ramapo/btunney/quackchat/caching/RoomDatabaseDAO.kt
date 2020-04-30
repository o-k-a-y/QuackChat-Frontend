package edu.ramapo.btunney.quackchat.caching

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import edu.ramapo.btunney.quackchat.caching.dao.CacheHashDao
import edu.ramapo.btunney.quackchat.caching.dao.FriendDao
import edu.ramapo.btunney.quackchat.caching.dao.MessageDao
import edu.ramapo.btunney.quackchat.caching.entities.Cache
import edu.ramapo.btunney.quackchat.caching.entities.Friend
import edu.ramapo.btunney.quackchat.caching.entities.Message

/**
 * DAO class to take care of accessing data from each of the tables in the Room DB
 *
 */
@Database(entities = [Cache::class, Friend::class, Message::class], version = 1)
abstract class RoomDatabaseDAO : RoomDatabase() {
    /**
     * DAO to access the CacheHash table
     *
     * @return the DAO object
     */
    abstract fun cacheHashDao(): CacheHashDao

    /**
     * DAO to access the Friend table
     *
     * @return the DAO object
     */
    abstract fun friendDao(): FriendDao

    /**
     * DAO to access the Message table
     *
     * @return the DAO object
     */
    abstract fun messageDao(): MessageDao

    /**
     * Return the current hash of the given type
     *
     * @param hashType the type of hash (refer to HashType enum)
     * @return
     */
    fun getHash(hashType: HashType): String {
        return cacheHashDao().getHash(hashType.type)
    }

    /**
     * Return the Friend object that matches the username given
     *
     * @param username the name of the friend
     * @return the Friend object
     */
    fun getFriendByName(username: String): Friend {
        return friendDao().findByName(username)
    }

    /**
     * Return a list containing all friends in the Friend table
     *
     * @return a list of the friends
     */
    fun getAllFriends(): List<Friend> {
        return friendDao().getAll()
    }

    /**
     * Return all the messages sent from a user
     *
     * @param fromUsername the user who sent the message
     */
    fun getAllMessagesFrom(fromUsername: String): List<Message> {
        return messageDao().getAllFromFriend(fromUsername)
    }


    /**
     * Insert a new hash into the CacheHash table
     *
     * @param hash the hash into insert
     */
    fun insertHash(hash: Cache) {
        cacheHashDao().insertOne(hash)
    }

    /**
     * Insert one friend into the Friend table
     *
     * @param friend the friend to insert
     */
    fun insertFriend(friend: Friend) {
        friendDao().insertOne(friend)
    }

    /**
     * Insert one message into the Message table
     *
     * @param message the message to insert
     */
    fun insertMessage(message: Message) {
        messageDao().insertOne(message)
    }

    // TODO: might be useful in the future
    private fun closeDb() {
        this.close()
    }

    companion object {
        // Name of the database
        const val DATABASE_NAME = "Cache"

        // Instance of database
        @Volatile
        private var INSTANCE: RoomDatabaseDAO? = null

        /**
         * Return the singleton instance of the database
         *
         * @param context application context
         * @return the instance of the class
         */
        fun getInstance(context: Context): RoomDatabaseDAO =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        /**
         * Build the instance of the Room DB
         *
         * @param context application context
         */
        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        RoomDatabaseDAO::class.java, DATABASE_NAME)
                        .build()
    }
}