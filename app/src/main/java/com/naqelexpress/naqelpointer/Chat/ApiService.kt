package com.naqelexpress.naqelpointer

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("/users")
    fun createUser(@Query("username") user:String): Call<String>
}