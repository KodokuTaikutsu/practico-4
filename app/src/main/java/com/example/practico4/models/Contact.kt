package com.example.practico4.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: Int?,
    val name: String?,
    @SerializedName("last_name") val lastName: String, val company: String?,
    val address: String?,
    val city: String?,
    val state: String?,
    @SerializedName("profile_picture") val profilePicture: String,
    val phones: List<Phone> = emptyList(),
    val emails: List<Email> = emptyList()
) : Parcelable
