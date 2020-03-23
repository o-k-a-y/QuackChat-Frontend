package edu.ramapo.btunney.quackchat.Networking

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Context.MODE_WORLD_WRITEABLE
import android.content.SharedPreferences
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.net.CookieHandler

/**
 * Singleton class to allow HTTP requests
 */
object NetworkRequester {

    private var applicationContext: Context? = null // very bad design to do this

    private val client = OkHttpClient()
        .newBuilder()
        .cookieJar(MemoryCookieJar())
        .build()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    // TODO: actually make use of this
    private val host = "http://52.55.108.86:3000" // The protocol and host and port
    private val port = "3000" // The port the server is using


    // TODO: Make a generic method to post a json to whatever route

    // TODO enum classes for each distinct route type


    fun getUser(url: String) {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()

            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }

                    println(response.body!!.string())
                }
            }
        })
    }

    fun authenticate(callback: NetworkCallback) {
        val request = Request.Builder()
            .url("$host/auth")
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

                        callback.onSuccess()
                    }

                    println(response.body!!.string())

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
     * Set application context to be able to save cookies to disk
     *
     * @param context the application's context
     */
    fun setContext(context: Context) {
        this.applicationContext = context
    }


    class MemoryCookieJar : CookieJar {
        private val cache = mutableSetOf<WrappedCookie>()

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
            // Only allow this application to see token
            val sharedPreferences: SharedPreferences = applicationContext!!.getSharedPreferences("AuthLogin", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("AuthToken", cookies[0].toString())
            editor.commit()
            // TODO: check that the cookie exists on app load
            // TODO: add this cookie to .cookieJar(MemoryCookieJar()) on app load ya dummy




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