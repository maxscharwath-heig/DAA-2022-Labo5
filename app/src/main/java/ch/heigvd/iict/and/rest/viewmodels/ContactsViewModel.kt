package ch.heigvd.iict.and.rest.viewmodels

import android.util.Log
import androidx.lifecycle.*
import ch.heigvd.iict.and.rest.ContactsApplication
import ch.heigvd.iict.and.rest.models.Contact
import kotlinx.coroutines.launch

class ContactsViewModel(application: ContactsApplication) : AndroidViewModel(application) {

    private val repository = application.repository
    private lateinit var uuid: String

    val allContacts = repository.allContacts
    var editingContact: MutableLiveData<Contact?> = MutableLiveData(null)
    var editionMode: MutableLiveData<Boolean> = MutableLiveData(false)

    fun enroll() {
        viewModelScope.launch {
            uuid = repository.enroll()
            Log.d("ContactsViewModel", "Enroll UUID: $uuid")
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