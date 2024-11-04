package com.example.practico4.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Email(
    val id: Int?,
    val email: String,
    val label: String,
    @SerializedName("persona_id") val contactId: Int?
) : Parcelable
