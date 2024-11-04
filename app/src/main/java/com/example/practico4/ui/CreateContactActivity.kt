package com.example.practico4.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.practico4.R
import com.example.practico4.api.RetrofitClient
import com.example.practico4.models.Contact
import com.example.practico4.models.Email
import com.example.practico4.models.Phone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateContactActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var companyEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var stateEditText: EditText
    private lateinit var profilePictureEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var addPhoneButton: Button
    private lateinit var addEmailButton: Button
    private lateinit var phoneNumbersLayout: LinearLayout
    private lateinit var emailAddressesLayout: LinearLayout

    private val phoneNumberViews = mutableListOf<View>()
    private val emailAddressViews = mutableListOf<View>()
    private var contactId: Int? = null
    private var originalPhones: List<Phone> = emptyList()
    private var originalEmails: List<Email> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_contact)

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        companyEditText = findViewById(R.id.companyEditText)
        addressEditText = findViewById(R.id.addressEditText)
        cityEditText = findViewById(R.id.cityEditText)
        stateEditText = findViewById(R.id.stateEditText)
        profilePictureEditText = findViewById(R.id.profilePictureEditText)
        saveButton = findViewById(R.id.saveButton)
        addPhoneButton = findViewById(R.id.addPhoneButton)
        addEmailButton = findViewById(R.id.addEmailButton)
        phoneNumbersLayout = findViewById(R.id.phoneNumbersLayout)
        emailAddressesLayout = findViewById(R.id.emailAddressesLayout)

        val contact = intent.getParcelableExtra<Contact>("CONTACT")
        contact?.let {
            contactId = it.id
            originalPhones = it.phones ?: emptyList()
            originalEmails = it.emails ?: emptyList()
            loadContactData(it)
        }

        saveButton.setOnClickListener {
            if (contactId == null) {
                addContact()
            } else {
                updateOrRecreateContact()
            }
        }

        addPhoneButton.setOnClickListener {
            addPhoneNumberField()
        }

        addEmailButton.setOnClickListener {
            addEmailAddressField()
        }
    }

    private fun loadContactData(contact: Contact) {
        nameEditText.setText(contact.name)
        lastNameEditText.setText(contact.lastName)
        companyEditText.setText(contact.company)
        addressEditText.setText(contact.address)
        cityEditText.setText(contact.city)
        stateEditText.setText(contact.state)
        profilePictureEditText.setText(contact.profilePicture)

        contact.phones?.forEach { phone ->
            addPhoneNumberField(phone.number, phone.label)
        }

        contact.emails?.forEach { email ->
            addEmailAddressField(email.email, email.label)
        }
    }

    private fun addPhoneNumberField(number: String = "", label: String = "Casa") {
        val inflater = LayoutInflater.from(this)
        val phoneFieldView = inflater.inflate(R.layout.phone_number_item, phoneNumbersLayout, false)

        val phoneNumberEditText = phoneFieldView.findViewById<EditText>(R.id.phoneNumberEditText)
        val labelSpinner = phoneFieldView.findViewById<Spinner>(R.id.labelSpinner)
        val removeButton = phoneFieldView.findViewById<Button>(R.id.removePhoneButton)

        phoneNumberEditText.setText(number)
        val labels = arrayOf("Casa", "Trabajo", "Celular")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, labels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        labelSpinner.adapter = adapter
        labelSpinner.setSelection(labels.indexOf(label))

        removeButton.setOnClickListener {
            phoneNumbersLayout.removeView(phoneFieldView)
            phoneNumberViews.remove(phoneFieldView)
        }

        phoneNumbersLayout.addView(phoneFieldView)
        phoneNumberViews.add(phoneFieldView)
    }

    private fun addEmailAddressField(email: String = "", label: String = "Persona") {
        val inflater = LayoutInflater.from(this)
        val emailFieldView = inflater.inflate(R.layout.email_address_item, emailAddressesLayout, false)

        val emailAddressEditText = emailFieldView.findViewById<EditText>(R.id.emailAddressEditText)
        val labelSpinner = emailFieldView.findViewById<Spinner>(R.id.emailLabelSpinner)
        val removeButton = emailFieldView.findViewById<Button>(R.id.removeEmailButton)

        emailAddressEditText.setText(email)
        val labels = arrayOf("Persona", "Trabajo", "Universidad")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, labels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        labelSpinner.adapter = adapter
        labelSpinner.setSelection(labels.indexOf(label))

        removeButton.setOnClickListener {
            emailAddressesLayout.removeView(emailFieldView)
            emailAddressViews.remove(emailFieldView)
        }

        emailAddressesLayout.addView(emailFieldView)
        emailAddressViews.add(emailFieldView)
    }


    private fun addContact() {
        val name = nameEditText.text.toString()
        val lastName = lastNameEditText.text.toString()
        val company = companyEditText.text.toString()
        val address = addressEditText.text.toString()
        val city = cityEditText.text.toString()
        val state = stateEditText.text.toString()
        val profilePicture = profilePictureEditText.text.toString()

        val newContact = Contact(
            id = null,
            name = name,
            lastName = lastName,
            company = company,
            address = address,
            city = city,
            state = state,
            profilePicture = profilePicture,
            phones = emptyList(),
            emails = emptyList()
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.addContact(newContact)
                if (response.isSuccessful) {
                    val addedContact = response.body()
                    addedContact?.id?.let { newId ->
                        addPhonesForContact(newId)
                        addEmailsForContact(newId)
                    }
                    runOnUiThread {
                        Toast.makeText(this@CreateContactActivity, "Contact added successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@CreateContactActivity, "Failed to add contact: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@CreateContactActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun addPhonesForContact(contactId: Int) {
        val phones = phoneNumberViews.map { view ->
            val number = view.findViewById<EditText>(R.id.phoneNumberEditText).text.toString()
            val label = view.findViewById<Spinner>(R.id.labelSpinner).selectedItem.toString()
            Phone(id = null, contactId = contactId, number = number, label = label)
        }

        phones.forEach { phone ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val phoneResponse = RetrofitClient.apiService.addPhone(phone)
                    if (!phoneResponse.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@CreateContactActivity, "Failed to add phone: ${phoneResponse.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@CreateContactActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun addEmailsForContact(contactId: Int) {
        val emails = emailAddressViews.map { view ->
            val email = view.findViewById<EditText>(R.id.emailAddressEditText).text.toString()
            val label = view.findViewById<Spinner>(R.id.emailLabelSpinner).selectedItem.toString()
            Email(id = null, contactId = contactId, email = email, label = label)
        }

        emails.forEach { emailObj ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val emailResponse = RetrofitClient.apiService.addEmail(emailObj)
                    if (!emailResponse.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@CreateContactActivity, "Failed to add email: ${emailResponse.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@CreateContactActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateOrRecreateContact() {
        // Collect new data
        val name = nameEditText.text.toString()
        val lastName = lastNameEditText.text.toString()
        val company = companyEditText.text.toString()
        val address = addressEditText.text.toString()
        val city = cityEditText.text.toString()
        val state = stateEditText.text.toString()
        val profilePicture = profilePictureEditText.text.toString()

        val phones = phoneNumberViews.map { view ->
            val number = view.findViewById<EditText>(R.id.phoneNumberEditText).text.toString()
            val label = view.findViewById<Spinner>(R.id.labelSpinner).selectedItem.toString()
            Phone(id = null, contactId = contactId, number = number, label = label)
        }

        val emails = emailAddressViews.map { view ->
            val email = view.findViewById<EditText>(R.id.emailAddressEditText).text.toString()
            val label = view.findViewById<Spinner>(R.id.emailLabelSpinner).selectedItem.toString()
            Email(id = null, contactId = contactId, email = email, label = label)
        }

        if (phones != originalPhones || emails != originalEmails) {
            deleteAndRecreateContact(name, lastName, company, address, city, state, profilePicture, phones, emails)
        } else {
            updateContact(name, lastName, company, address, city, state, profilePicture)
        }
    }

    private fun deleteAndRecreateContact(
        name: String,
        lastName: String,
        company: String,
        address: String,
        city: String,
        state: String,
        profilePicture: String,
        phones: List<Phone>,
        emails: List<Email>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                contactId?.let { RetrofitClient.apiService.deleteContact(it) }

                val newContact = Contact(
                    id = null,
                    name = name,
                    lastName = lastName,
                    company = company,
                    address = address,
                    city = city,
                    state = state,
                    profilePicture = profilePicture,
                    phones = emptyList(),
                    emails = emptyList()
                )

                val response = RetrofitClient.apiService.addContact(newContact)
                if (response.isSuccessful) {
                    val addedContact = response.body()
                    addedContact?.id?.let { newId ->
                        addPhonesForContact(newId)
                        addEmailsForContact(newId)
                    }
                    runOnUiThread {
                        Toast.makeText(this@CreateContactActivity, "Contact recreated successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@CreateContactActivity, "Failed to recreate contact: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@CreateContactActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateContact(
        name: String,
        lastName: String,
        company: String,
        address: String,
        city: String,
        state: String,
        profilePicture: String
    ) {
        val updatedContact = Contact(
            id = contactId,
            name = name,
            lastName = lastName,
            company = company,
            address = address,
            city = city,
            state = state,
            profilePicture = profilePicture,
            phones = emptyList(),
            emails = emptyList()
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.updateContact(contactId!!, updatedContact)
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@CreateContactActivity, "Contact updated successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@CreateContactActivity, "Failed to update contact: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@CreateContactActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
