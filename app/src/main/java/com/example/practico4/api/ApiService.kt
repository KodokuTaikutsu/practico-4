package com.example.practico4.api

import com.example.practico4.models.Contact
import com.example.practico4.models.Email
import com.example.practico4.models.Phone
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Contacts
    @GET("api/personas")
    suspend fun getContacts(): Response<List<Contact>>

    @GET("api/search")
    suspend fun searchContacts(@Query("q") query: String): Response<List<Contact>>

    @POST("api/personas")
    suspend fun addContact(@Body contact: Contact): Response<Contact>

    @PUT("api/personas/{id}")
    suspend fun updateContact(@Path("id") id: Int, @Body contact: Contact): Response<Contact>

    @DELETE("api/personas/{id}")
    suspend fun deleteContact(@Path("id") id: Int): Response<Unit>

    @GET("api/personas/{id}")
    suspend fun getContactById(@Path("id") id: Int): Response<Contact>

    // Phones
    @GET("api/phones")
    suspend fun getPhones(): Response<List<Phone>>

    @POST("api/phones")
    suspend fun addPhone(@Body phone: Phone): Response<Phone>

    // Emails
    @POST("api/emails")
    suspend fun addEmail(@Body email: Email): Response<Email>

    // Profile Picture
    @Multipart
    @POST("api/personas/{id}/profile-picture")
    suspend fun uploadProfilePicture(
        @Path("id") id: Int,
        @Part image: MultipartBody.Part
    ): Response<Unit>
}
