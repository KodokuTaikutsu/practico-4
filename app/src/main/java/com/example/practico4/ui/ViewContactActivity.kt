package com.example.practico4.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.practico4.R
import com.example.practico4.models.Contact
import com.example.practico4.models.Email
import com.example.practico4.models.Phone

class ViewContactActivity : AppCompatActivity() {

    private lateinit var phoneNumbersLayout: LinearLayout
    private lateinit var emailAddressesLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_contact)

        val contact = intent.getParcelableExtra<Contact>("CONTACT")

        if (contact == null) {
            finish() // Exit if contact is null
            return
        }

        val profilePicture: ImageView = findViewById(R.id.profilePictureImageView)
        val nameTextView: TextView = findViewById(R.id.contactNameTextView)
        val lastNameTextView: TextView = findViewById(R.id.contactLastNameTextView)
        val companyTextView: TextView = findViewById(R.id.companyTextView)
        val addressTextView: TextView = findViewById(R.id.addressTextView)
        val cityTextView: TextView = findViewById(R.id.cityTextView)
        val stateTextView: TextView = findViewById(R.id.stateTextView)
        phoneNumbersLayout = findViewById(R.id.phoneNumbersLayout)
        emailAddressesLayout = findViewById(R.id.emailAddressesLayout)

        // Load and display the profile picture
        Glide.with(this).load(contact.profilePicture).into(profilePicture)

        // Set the text for contact details
        nameTextView.text = contact.name ?: "N/A"
        lastNameTextView.text = contact.lastName ?: "N/A"
        companyTextView.text = contact.company ?: "N/A"
        addressTextView.text = contact.address ?: "N/A"
        cityTextView.text = contact.city ?: "N/A"
        stateTextView.text = contact.state ?: "N/A"

        // Display phone numbers
        contact.phones?.forEach { phone ->
            addPhoneNumberView(phone)
        }

        // Display email addresses
        contact.emails?.forEach { email ->
            addEmailAddressView(email)
        }
    }

    private fun addPhoneNumberView(phone: Phone) {
        val phoneTextView = TextView(this).apply {
            text = "${phone.label}: ${phone.number}"
            textSize = 16f
            setPadding(0, 8, 0, 8)
        }
        phoneNumbersLayout.addView(phoneTextView)
    }

    private fun addEmailAddressView(email: Email) {
        val emailTextView = TextView(this).apply {
            text = "${email.label}: ${email.email}"
            textSize = 16f
            setPadding(0, 8, 0, 8)
        }
        emailAddressesLayout.addView(emailTextView)
    }
}
