package ch.heigvd.iict.and.rest.viewmodels

import android.util.Log
import androidx.lifecycle.*
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import ch.heigvd.iict.and.rest.ContactsApplication
import ch.heigvd.iict.and.rest.models.Contact
import kotlinx.coroutines.launch

class ContactsViewModel(application: ContactsApplication) : AndroidViewModel(application) {

    companion object {
        const val UUID_KEY = "uuid"
    }

    private val repository = application.repository

    private val securePreferences = EncryptedSharedPreferences.create(
        "sharedPrefs",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        application,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private var uuid: String = securePreferences.getString(UUID_KEY, "") ?: ""

    val allContacts: LiveData<List<Contact>> = repository.allContacts
    var editingContact: MutableLiveData<Contact?> = MutableLiveData(null)
    var editionMode: MutableLiveData<Boolean> = MutableLiveData(false)

    fun enroll() {
        viewModelScope.launch {
            // Delete the local data
            repository.deleteAll()

            // Enroll a new UUID
            try {
                uuid = repository.enroll()
                Log.d("ContactsViewModel", "Enroll UUID: $uuid")

                with(securePreferences.edit()) {
                    Log.d("ContactsViewModel", "Save UUID to secure preferences")
                    putString(UUID_KEY, uuid)
                    apply()
                }

                // Get the contacts and insert into DB
                repository.getAllAndInsert(uuid)
            } catch (e: Exception) {
                Log.d("Enrollment", "failed")
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            repository.retryDirties(uuid)
        }
    }

    fun setEditingContact(contact: Contact?) {
        editingContact.value = contact
    }

    fun toggleEditionMode(active: Boolean) {
        editionMode.value = active
    }

    fun create(contact: Contact) {
        viewModelScope.launch {
            repository.create(contact, uuid)
        }
    }

    fun update(contact: Contact) {
        viewModelScope.launch {
            repository.update(contact, uuid)
        }
    }

    fun delete(contact: Contact) {
        viewModelScope.launch {
            repository.delete(contact, uuid)
        }
    }
}

class ContactsViewModelFactory(private val application: ContactsApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}