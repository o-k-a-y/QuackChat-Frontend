package edu.ramapo.btunney.quackchat.networking

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.*

/**
 * Singleton class to allow HTTP requests
 */
object NetworkRequester {

    private var applicationContext: Context? = null // very bad design to do this

    private val cache = mutableSetOf<WrappedCookie>() // moved from MemoryCookieJar class, probably bad

    private val client = OkHttpClient()
        .newBuilder()
        .cookieJar(MemoryCookieJar())
        .build()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    // TODO: actually make use of this
    private const val host = "http://52.55.108.86:3000" // The protocol and host and port
    private val port = "3000" // The port the server is using


    // TODO: Make a generic method to post a json to whatever route

    // TODO enum classes for each distinct route type

//
//    fun getUsername(route: ServerRoutes, callback: NetworkCallback) {
//        val request = Request.Builder()
//            .url(host + route.route)
//            .get()
//            .build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                e.printStackTrace()
//
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                response.use {
//                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
//
//                    for ((name, value) in response.headers) {
//                        println("$name: $value")
//                    }
//
//                    println(response.body!!.string())
//                    callback.onSuccess()
//                    return
//                }
//            }
//        })
//    }

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

        val httpOnly = keys[3] // if value = "httponly" it should be a boolean set to true

        var httponly = false

        if (httpOnly == "httponly") {
            httponly = true
        }

        var date = Date(expiresAt)
        var time = date.time

        // Make strings into cookie
        return Cookie.Builder()
            .domain("52.55.108.86")
            .name(name)
            .value(value)
            .expiresAt(time) // long
            .path(path)
            .httpOnly()
            .build()
    }

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

                    callback.onSuccess()
//                    println(response.body!!.string())
                    return

                }
            }

        })
    }


    fun postUser(route: ServerRoutes, userJSON: JSONObject, callback: NetworkCallback) {
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
                    callback.onSuccess()
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
     * @param userJSON the data to send (user JSON object)
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
                    callback.onSuccess()
//                    println(response.code)
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


                callback.onSuccess()
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


                callback.onSuccess()
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

    private fun addStoredCookie(cookie: Cookie) {
        val wrappedCookie = WrappedCookie.wrap(cookie)

        // Add wrapped cookie to MemoryCookieJar cache
        cache.add(wrappedCookie)
    }


    class MemoryCookieJar : CookieJar {
//        private val cache = mutableSetOf<WrappedCookie>() made private member of singleton?

        @Synchronized
        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            val cookiesToRemove = mutableSetOf<WrappedCookie>()
            val validCookies = mutableSetOf<WrappedCookie>()

            cache.forEach { cookie ->
                if (cookie.isExpired()) {
                    cookiesToRemove.add(cookie)
                } else if (cookie.matches(url)) {
                    validCookies.add(cookie)
                }
            }

            cache.removeAll(cookiesToRemove)

            return validCookies.toList().map(WrappedCookie::unwrap)
        }

        @Synchronized
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            val cookiesToAdd = cookies.map { WrappedCookie.wrap(it) }

            // check what key cookies have
            // TODO: ONLY SAVE THE RIGHT COOKIE (I.E. CHECK THE COOKIE THAT HAS connect.sid NAME)
            cookies.forEach { cookie ->
                println("cookie: $cookie")
            }
            // Save cookie to SharedPreferences
            // TODO: ONLY SAVE THE RIGHT COOKIE (I.E. CHECK THE COOKIE THAT HAS connect.sid NAME)


            // TODO: ALSO make it save in a format like a hash map in string from that can be converted back to a map
            //          or save as a JSON form https://stackoverflow.com/questions/7944601/how-to-save-hashmap-to-shared-preferences
            //                  check the Gson().toJson(map) one
            // Only allow this application to see token
            val sharedPreferences: SharedPreferences = applicationContext!!.getSharedPreferences("AuthLogin", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("AuthToken", cookies[0].toString())
            editor.apply() // if something breaks, change to commit even though it doesn't activate in bg

            cache.removeAll(cookiesToAdd)
            cache.addAll(cookiesToAdd)
        }

        @Synchronized
        fun clear() {
            cache.clear()
        }


    }

    class WrappedCookie private constructor(val cookie: Cookie) {
        fun unwrap() = cookie

        fun isExpired() = cookie.expiresAt < System.currentTimeMillis()

        fun matches(url: HttpUrl) = cookie.matches(url)

        override fun equals(other: Any?): Boolean {
            if (other !is WrappedCookie) return false

            return other.cookie.name == cookie.name &&
                    other.cookie.domain == cookie.domain &&
                    other.cookie.path == cookie.path &&
                    other.cookie.secure == cookie.secure &&
                    other.cookie.hostOnly == cookie.hostOnly
        }

        override fun hashCode(): Int {
            var hash = 17
            hash = 31 * hash + cookie.name.hashCode()
            hash = 31 * hash + cookie.domain.hashCode()
            hash = 31 * hash + cookie.path.hashCode()
            hash = 31 * hash + if (cookie.secure) 0 else 1
            hash = 31 * hash + if (cookie.hostOnly) 0 else 1
            return hash
        }

        companion object {
            fun wrap(cookie: Cookie) = WrappedCookie(cookie)
        }
    }

}