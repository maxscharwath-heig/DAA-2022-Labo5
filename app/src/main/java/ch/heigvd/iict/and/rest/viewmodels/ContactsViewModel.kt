package ch.heigvd.iict.and.rest.viewmodels

import android.util.Log
import androidx.lifecycle.*
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import ch.heigvd.iict.and.rest.ContactsApplication
import ch.heigvd.iict.and.rest.models.Contact
import kotlinx.coroutines.launch

class ContactsViewModel(application: ContactsApplication) : AndroidViewModel(application) {

    private val repository = application.repository
    private lateinit var uuid: String

    private val securePreferences = EncryptedSharedPreferences.create(
        "sharedPrefs",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        application,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    val allContacts = repository.allContacts
    var editingContact: MutableLiveData<Contact?> = MutableLiveData(null)
    var editionMode: MutableLiveData<Boolean> = MutableLiveData(false)

    fun enroll() {
        viewModelScope.launch {
            uuid = repository.enroll()
            Log.d("ContactsViewModel", "Enroll UUID: $uuid")

            with(securePreferences.edit()) {
                Log.d("ContactsViewModel", "Save UUID to secure preferences")
                putString("uuid", uuid)
                apply()
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            //TODO
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
            repository.create(contact)
        }
    }

    fun update(contact: Contact) {
        viewModelScope.launch {
            repository.update(contact)
        }
    }

    fun delete(contact: Contact) {
        viewModelScope.launch {
            repository.delete(contact)
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