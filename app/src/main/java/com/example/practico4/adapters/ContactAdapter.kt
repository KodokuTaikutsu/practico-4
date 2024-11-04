package com.example.practico4.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practico4.R
import com.example.practico4.models.Contact

class ContactAdapter(
    private val context: Context,
    private val contacts: MutableList<Contact>,
    private val onViewClick: (Contact) -> Unit,
    private val onEditClick: (Contact) -> Unit,
    private val onDeleteClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int = contacts.size

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contactNameTextView: TextView = itemView.findViewById(R.id.contactNameTextView)
        private val contactLastNameTextView: TextView = itemView.findViewById(R.id.contactLastNameTextView)
        private val phoneNumberTextView: TextView = itemView.findViewById(R.id.phoneNumberTextView)
        private val viewButton: Button = itemView.findViewById(R.id.viewButton)
        private val editButton: Button = itemView.findViewById(R.id.editButton)
        private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

        fun bind(contact: Contact) {
            contactNameTextView.text = contact.name
            contactLastNameTextView.text = contact.lastName

            // Set the first phone number, if available
            val firstPhoneNumber = contact.phones?.firstOrNull()?.number ?: "No phone"
            phoneNumberTextView.text = firstPhoneNumber

            viewButton.setOnClickListener { onViewClick(contact) }
            editButton.setOnClickListener { onEditClick(contact) }
            deleteButton.setOnClickListener { onDeleteClick(contact) }
        }
    }



    // Method to update the contact list and notify the adapter
    fun updateList(newList: List<Contact>) {
        contacts.clear() // Clear the existing list
        contacts.addAll(newList) // Add all new contacts
        notifyDataSetChanged() // Notify the adapter that data has changed
    }
}
