package edu.ramapo.btunney.quackchat.networking

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*

/**
 * Singleton class to allow HTTP requests to the backend
 */
object NetworkRequester {
    // Needed to store cookie in Shared Preferences
    private var applicationContext: Context? = null // very bad design to do this

    // Cached cookies
    private val cachedCookies = mutableSetOf<WrappedCookie>() // moved from MemoryCookieJar class, probably bad

    // OkHttpClient with ability to send and receive cookies
    private val client = OkHttpClient()
        .newBuilder()
        .cookieJar(MemoryCookieJar())
        .build()

    // Tells the server the filetype is JSONCo
    private val JSON = "application/json; charset=utf-8".toMediaType()
    // The endpoint of the server
    private const val host = "http://52.55.108.86:3000"

    // TODO: Make a generic method (builder) to post a json to whatever route

    /**
     * Add the authentication cookie if it exists on disk
     *
     */
    fun addAuthToken() {
        // Check that cookie exists in SharedPreferences
        val sharedPreferences: SharedPreferences = applicationContext!!.getSharedPreferences("AuthLogin", MODE_PRIVATE)

        val sharedPrefCookie = Cookie.parseCookie(sharedPreferences) ?: return

        // Wrap cookie and add to MemoryCookieJar cache
        addStoredCookie(sharedPrefCookie)
    }

    /**
     * Check if the user is authenticated
     *
     * @param route the route on the server
     * @param callback handles success and failure of call
     */
    fun authenticate(route: ServerRoutes, callback: NetworkCallback) {
        val request = Request.Builder()
            .url(host + route.route)
            .get()
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()

                // TODO change to some other err besides default.
                // What can cause this to fail and how do we handle it?
                callback.onFailure(NetworkCallback.FailureCode.DEFAULT)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        when (response.code) {
                            // User is not authenticated
                            401 -> {
                                callback.onFailure(NetworkCallback.FailureCode.NOT_AUTHENTICATED)
                                return
                            }
                        }
                        Log.d("Successfully auth", "test")
                    }

                    callback.onSuccess(null)
//                    println(response.body!!.string())
                    return
                }
            }
        })
    }


    /**
     * Create a new user account
     *
     * @param route the route on the server
     * @param userJSON the user object
     * @param callback handles success and failure of call
     */
    fun createUser(route: ServerRoutes, userJSON: JSONObject, callback: NetworkCallback) {
        val body = userJSON.toString()
            .toRequestBody(JSON)

        val request = Request.Builder()
            .url(host + route.route)
            .put(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // TODO change to some other err
                callback.onFailure(NetworkCallback.FailureCode.DEFAULT)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {

                    // Return failure when signup fails
                    // Failure can occur for example when user already exists
                    if (!response.isSuccessful) {
                        when (response.code) {
                            409 -> {
                                callback.onFailure(NetworkCallback.FailureCode.DUPLICATE_USER)
                                return
                            }
                        }
                    }

                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }

                    // Signup succeeded
                    callback.onSuccess(null)
                    return
                    println(response.body!!.string())
                }
            }
        })

    }

    /**
     * Login and authenticate user to server
     *
     * @param route the route on the server
     * @param userJSON user credentials to login
     * @param callback handles success and failure of call
     */
    fun login(route: ServerRoutes, userJSON: JSONObject, callback: NetworkCallback) {
        val body = userJSON.toString()
            .toRequestBody(JSON)

        val request = Request.Builder()
            .url(host + route.route)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // TODO change to some other err
                callback.onFailure(NetworkCallback.FailureCode.DEFAULT)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {

                        // Return failure code when login fails
                        when (response.code) {
                            404, 401 -> {
                                callback.onFailure(NetworkCallback.FailureCode.INVALID_LOGIN)
                                return
                            }
                        }
                        callback.onFailure(NetworkCallback.FailureCode.DEFAULT)
                        return
                    }



                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }

                    println(response.body!!.string())
                    callback.onSuccess(null)
                }
            }
        })
    }

    /**
     * Logout and de-authenticate user from server
     *
     * @param route the route on the server
     * @param callback handles success and failure of call
     */
    fun logOut(route: ServerRoutes, callback: NetworkCallback) {
        val request = Request.Builder()
            .url(host + route.route)
            .post("".toRequestBody())
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // TODO change to some other err
                callback.onFailure(NetworkCallback.FailureCode.DEFAULT)
            }

            override fun onResponse(call: Call, response: Response) {
                // TODO: Check for what errors can occur from logging out
                //      are there any and how can we handle them
                //      how to handle crap internet?
                if (!response.isSuccessful) {
                    // Return failure code when login fails
                    when (response.code) {
                        else -> {
                            callback.onFailure(NetworkCallback.FailureCode.DEFAULT)
                        }
                    }

                    return
                }

//                println(response.body!!.string())
                callback.onSuccess(null)
            }

        })
    }

    /**
     * Send a friend request to another user
     *
     * @param route the route on the server
     * @param username the user to add
     * @param callback handles success and failure of call
     */
    fun addFriend(route: ServerRoutes, username: String, callback: NetworkCallback) {
        val request = Request.Builder()
                .url(host + route.route + "/" + username)
                .post(username.toRequestBody())
                .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // TODO change to some other err
                callback.onFailure(NetworkCallback.FailureCode.DEFAULT)
            }

            override fun onResponse(call: Call, response: Response) {
                // Return failure code when adding a friend fails
                if (!response.isSuccessful) {
                    when (response.code) {
                        404 -> {
                            callback.onFailure(NetworkCallback.FailureCode.DOES_NOT_EXIST)
                        }
                        409 -> {
                            callback.onFailure(NetworkCallback.FailureCode.ALREADY_ADDED)
                        }
                        else -> {
                            callback.onFailure(NetworkCallback.FailureCode.DEFAULT)
                        }
                    }
                    return
                }


                callback.onSuccess(null)
            }

        })
    }

    /**
     * Delete a friend
     *
     * @param route the route on the server
     * @param username the user to delete
     * @param callback handles success and failure of call
     */
    fun deleteFriend(route: ServerRoutes, username: String, callback: NetworkCallback) {
        val request = Request.Builder()
                .url(host + route.route + "/" + username)
                .delete(username.toRequestBody())
                .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // TODO change to some other err
                callback.onFailure(NetworkCallback.FailureCode.DEFAULT)
            }

            override fun onResponse(call: Call, response: Response) {
                // Return failure code when deleting a friend fails
                if (!response.isSuccessful) {
                    when (response.code) {
                        else -> {
                            callback.onFailure(NetworkCallback.FailureCode.DEFAULT)
                        }
                    }
                    return
                }

                callback.onSuccess(null)
            }

        })
    }

    /**
     * Return the list of friends the logged in user has
     *
     * @param route the route on the server
     * @param callback handles success and failure of call
     */
    fun fetchFriends(route: ServerRoutes, callback: NetworkCallback)  {
        val request = Request.Builder()
                .url(host + route.route)
                .build()

        var friendJSON: JSONObject? = null

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // TODO change to some other err
                callback.onFailure(NetworkCallback.FailureCode.DEFAULT)
            }

            override fun onResponse(call: Call, response: Response) {
                // Return failure code when friends can not be returned
                if (!response.isSuccessful) {
                    when (response.code) {
                        else -> {
                            callback.onFailure(NetworkCallback.FailureCode.DEFAULT)
                        }
                    }
                }

                // Convert response to JSON object
                friendJSON = JSONObject(response.body?.string())
                callback.onSuccess(friendJSON)
            }

        })

    }

    /**
     * Send a message to a list of friends
     *
     * @param route the route on the server
     * @param friends the list of friends to send message to
     * @param callback handles success and failure of call
     */
    fun sendMessage(route: ServerRoutes, friends: Array<String>, message: String, messageType: MessageType, callback: NetworkCallback) {
        val messageJSONString = "{\"message\": \"$message\"}"
        val messageJSON = JSONObject(messageJSONString)

        val friendJSONArray = JSONArray(friends)

        // Insert the type of message into the request body
        messageJSON.put("messageType", messageType.type)

        // Insert the friends to send message to into the request body
        messageJSON.put("friends", friendJSONArray)

        val body = messageJSON.toString()
                .toRequestBody(JSON)
        println(body)

        val request = Request.Builder()
                .url(host + route.route)
                .post(body)
                .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // TODO change to some other err
                callback.onFailure(NetworkCallback.FailureCode.DEFAULT)
            }

            override fun onResponse(call: Call, response: Response) {
                // Return failure code when adding a friend fails
                if (!response.isSuccessful) {
                    // TODO
                }


                callback.onSuccess(null)
            }
        })
    }

    /**
     * Return the list of messages the logged in user has
     *
     * @param route the route on the server
     * @param callback handles success and failure of call
     */
    fun fetchMessages(route: ServerRoutes, callback: NetworkCallback) {
        val request = Request.Builder()
                .url(host + route.route)
                .build()

        var messageJSON: JSONObject? = null

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // TODO change to some other err
                callback.onFailure(NetworkCallback.FailureCode.DEFAULT)
            }

            override fun onResponse(call: Call, response: Response) {
                // Return failure code when friends can not be returned
                if (!response.isSuccessful) {
                    when (response.code) {
                        else -> {
                            callback.onFailure(NetworkCallback.FailureCode.DEFAULT)
                        }
                    }
                }

                // Convert response to JSON object
                messageJSON = JSONObject(response.body?.string())
                callback.onSuccess(messageJSON)
            }
        })
    }

    /**
     * Delete all the messages that were sent from a user's friend
     *
     * @param route the route on the server
     * @param sentFrom which friend sent the messages
     * @param callback handles success and failure of call
     */
    fun deleteMessages(route: ServerRoutes, sentFrom: String, callback: NetworkCallback) {
        val request = Request.Builder()
                .url(host + route.route + "/" + sentFrom)
                .delete(sentFrom.toRequestBody())
                .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // TODO change to some other err
                callback.onFailure(NetworkCallback.FailureCode.DEFAULT)
            }

            override fun onResponse(call: Call, response: Response) {
                // Return failure code when deleting a friend's messages fails
                if (!response.isSuccessful) {
                    when (response.code) {
                        else -> {
                            callback.onFailure(NetworkCallback.FailureCode.DEFAULT)
                        }
                    }
                    return
                }

                callback.onSuccess(null)
            }

        })
    }

    /**
     * Validate if the hash in local DB matches hash in remote DB
     *
     * @param route the route on the server
     * @param hash the local hash of the data
     * @param callback handles success and failure of call
     */
    fun validateHash(route: ServerRoutes, hash: JSONObject, callback: NetworkCallback) {
        println(hash.toString())
        val body = hash.toString()
                .toRequestBody(JSON)

        val request = Request.Builder()
                .url(host + route.route)
                .post(body)
                .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // TODO change to some other err
                callback.onFailure(NetworkCallback.FailureCode.DEFAULT)
            }

            // Pass new hash to the onSuccess
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val newHash = response.body?.string()
                    val hashJSON = JSONObject(newHash)

                    callback.onSuccess(hashJSON)
                }
            }
        })
    }

    /**
     * Set application context to be able to save cookies and other SharedPreferences objects to disk
     *
     * @param context the application's context
     */
    fun setContext(context: Context) {
        this.applicationContext = context
    }

    /**
     * Add cookie stored on disk to the list of cookies for network requests
     * The cookie is grabbed from SharedPreferences and first wrapped before adding
     *
     * @param cookie
     */
    private fun addStoredCookie(cookie: Cookie) {
        val wrappedCookie = WrappedCookie.wrap(cookie)

        // Add wrapped cookie to MemoryCookieJar cache
        cachedCookies.add(wrappedCookie)
    }


    /**
     * This class is for the CookieJar stored in memory on the app used for sending network requests
     *
     */
    class MemoryCookieJar : CookieJar {
        /**
         * Return the list of cookies for sending with the request object
         *
         * @param url where we're sending the cookies to (what url the cookies have)
         * @return the list of cookies
         */
        @Synchronized
        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            val cookiesToRemove = mutableSetOf<WrappedCookie>()
            val validCookies = mutableSetOf<WrappedCookie>()

            // Get the valid cookies and remove expired ones
            cachedCookies.forEach { cookie ->
                if (cookie.isExpired()) {
                    cookiesToRemove.add(cookie)
                } else if (cookie.matches(url)) {
                    validCookies.add(cookie)
                }
            }

            cachedCookies.removeAll(cookiesToRemove)

            return validCookies.toList().map(WrappedCookie::unwrap)
        }

        /**
         * Save the cookies we got from the network response (should only be one for now)
         * Also save the cookies to SharedPreferences so we can access on app start to automatically
         * log the user in if they have a valid cookie
         *
         * @param url the url set on the cookies (hopefully where they came from)
         * @param cookies the list of cookies returned from network response
         */
        @Synchronized
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            val cookiesToAdd = cookies.map { WrappedCookie.wrap(it) }

//            // Check what key cookies have
//            cookies.forEach { cookie ->
//                println("cookie: $cookie")
//            }
            // TODO: ONLY SAVE THE RIGHT COOKIE (I.E. CHECK THE COOKIE THAT HAS connect.sid NAME)

            // Save cookie to SharedPreferences
            val sharedPreferences: SharedPreferences = applicationContext!!.getSharedPreferences("AuthLogin", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("AuthToken", cookies[0].toString())
            editor.apply() // if something breaks, change to commit() even though it doesn't activate in background

            // Refresh the cookies
            cachedCookies.removeAll(cookiesToAdd)
            cachedCookies.addAll(cookiesToAdd)
        }

        /**
         * Clear all cookies stored in cache
         *
         */
        @Synchronized
        fun clear() {
            cachedCookies.clear()
        }


    }

    /**
     * This class allows for taking a cookie and wrapping it so it can be sent to the server
     *
     * @property cookie the cookie to wrap
     */
    class WrappedCookie private constructor(val cookie: Cookie) {
        /**
         * Unwrap the cookie (return to original state
         */
        fun unwrap() = cookie

        /**
         * Check if cookie passed expire time
         *
         */
        fun isExpired() = cookie.expiresAt < System.currentTimeMillis()

        /**
         * Check if the cookie's url matches the correct url
         *
         * @param url url to check
         */
        fun matches(url: HttpUrl) = cookie.matches(url)

        /**
         * Check if something is equal to the wrapped cookie
         *
         * @param other the thing to check
         * @return true if equal to the wrapped cookie, false otherwise
         */
        override fun equals(other: Any?): Boolean {
            // Check if it's the same type
            if (other !is WrappedCookie) return false

            // See if they have the same values
            return other.cookie.name == cookie.name &&
                    other.cookie.domain == cookie.domain &&
                    other.cookie.path == cookie.path &&
                    other.cookie.secure == cookie.secure &&
                    other.cookie.hostOnly == cookie.hostOnly
        }

        /**
         * The hashCode() function of Cookie object but for WrappedCookie
         *
         * @return the hash of the cookie
         */
        override fun hashCode(): Int {
            var hash = 17
            hash = 31 * hash + cookie.name.hashCode()
            hash = 31 * hash + cookie.domain.hashCode()
            hash = 31 * hash + cookie.path.hashCode()
            hash = 31 * hash + if (cookie.secure) 0 else 1
            hash = 31 * hash + if (cookie.hostOnly) 0 else 1
            return hash
        }

        /**
         * Public functions
         */
        companion object {
            /**
             * Wrap the cookie
             *
             * @param cookie
             */
            fun wrap(cookie: Cookie) = WrappedCookie(cookie)
        }
    }

    /**
     * Extends Cookie to parse auth token (the cookie)
     *
     * @param sharedPreferences
     */
    private fun Cookie.Companion.parseCookie(sharedPreferences: SharedPreferences): Cookie? {
        val cookieString: String = sharedPreferences.getString("AuthToken", null) ?: return null

        // Get properties of cookie
        val keys:List<String> = cookieString.split(";")
        var name = keys[0]

        val nameAndValue = name.split("=")
        name = nameAndValue[0]
        val value = nameAndValue[1]

        var expiresAt = keys[1] // need to format date probably

        val expire = expiresAt.split("=")
        expiresAt = expire[1]

        var path = keys[2] //
        val pathh = path.split("=")
        path = pathh[1]


        var date = Date(expiresAt)
        var time = date.time

        // Make strings into cookie
        return Cookie.Builder()
                .domain("52.55.108.86")
                .name(name)
                .value(value)
                .expiresAt(time) // long integer
                .path(path)
                .httpOnly() // TODO: should be https but server doesn't support this
                .build()
    }

}