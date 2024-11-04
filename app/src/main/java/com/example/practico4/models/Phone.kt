package com.example.practico4.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Phone(
    val id: Int?,
    val number: String,
    val label: String,
    @SerializedName("persona_id") val contactId: Int?
) : Parcelable
