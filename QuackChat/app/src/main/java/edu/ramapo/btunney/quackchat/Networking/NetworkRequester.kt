package edu.ramapo.btunney.quackchat.Networking

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

/**
 * Singleton class to allow HTTP requests
 */
object NetworkRequester {
    private val client = OkHttpClient()
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




}