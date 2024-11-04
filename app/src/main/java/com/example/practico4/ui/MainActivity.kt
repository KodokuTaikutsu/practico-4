package com.example.practico4.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practico4.R
import com.example.practico4.adapters.ContactAdapter
import com.example.practico4.api.RetrofitClient
import com.example.practico4.models.Contact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var contactAdapter: ContactAdapter
    private val contacts = mutableListOf<Contact>()
    private lateinit var searchBar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchBar = findViewById(R.id.searchBarEditText)
        contactAdapter = ContactAdapter(this, contacts, ::viewContact, ::editContact, ::deleteContact)

        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = contactAdapter

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAddContact).setOnClickListener {
            startActivity(Intent(this, CreateContactActivity::class.java))
        }

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterContacts(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        loadContacts()
    }

    override fun onResume() {
        super.onResume()
        loadContacts() // Reload contacts when the activity resumes
    }

    private fun loadContacts() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitClient.apiService.getContacts()
            if (response.isSuccessful) {
                response.body()?.let {
                    contacts.clear()
                    contacts.addAll(it)
                    contacts.sortBy { contact -> contact.name ?: "" } // Sort alphabetically
                    runOnUiThread {
                        filterContacts(searchBar.text.toString()) // Update UI with the current search query
                    }
                }
            }
        }
    }

    private fun filterContacts(query: String) {
        val filteredContacts = contacts.filter { contact ->
            (contact.name?.contains(query, ignoreCase = true) == true) ||
                    (contact.lastName?.contains(query, ignoreCase = true) == true) ||
                    contact.phones.any { it.number?.contains(query, ignoreCase = true) == true }
        }
        contactAdapter.updateList(filteredContacts)
    }

    private fun viewContact(contact: Contact) {
        val intent = Intent(this, ViewContactActivity::class.java).apply {
            putExtra("CONTACT", contact)
        }
        startActivity(intent)
    }

    private fun editContact(contact: Contact) {
        val intent = Intent(this, CreateContactActivity::class.java).apply {
            putExtra("CONTACT", contact)
        }
        startActivity(intent)
    }

    private fun deleteContact(contact: Contact) {
        contact.id?.let { id ->
            CoroutineScope(Dispatchers.IO).launch {
                RetrofitClient.apiService.deleteContact(id)
                runOnUiThread {
                    contacts.remove(contact)
                    filterContacts(searchBar.text.toString()) // Refresh the filtered list
                }
            }
        }
    }
}
