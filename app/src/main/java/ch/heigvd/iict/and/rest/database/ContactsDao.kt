package ch.heigvd.iict.and.rest.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.ContactState

@Dao
interface ContactsDao {

    @Insert
    fun insert(contact: Contact) : Long

    @Update
    fun update(contact: Contact)

    @Delete
    fun delete(contact: Contact)

    fun softDelete(contact: Contact) {
        contact.state = ContactState.DELETED
        update(contact)
    }

    @Query("SELECT * FROM contact WHERE state = :state")
    fun getAllContactsByState(state: ContactState) : List<Contact>

    @Query("SELECT * FROM contact WHERE state != :state")
    fun getAllUnsyncedContacts(state: ContactState = ContactState.SYNCED) : List<Contact>

    @Query("SELECT * FROM Contact WHERE state != :state")
    fun getAllContactsLiveData(state: ContactState = ContactState.DELETED) : LiveData<List<Contact>>

    @Query("SELECT * FROM Contact WHERE id = :id AND state != :state")
    fun getContactById(id : Long, state: ContactState = ContactState.DELETED) : Contact?

    @Query("SELECT COUNT(*) FROM Contact WHERE state != :state")
    fun getCount(state: ContactState = ContactState.DELETED) : Int

    @Query("DELETE FROM Contact")
    fun clearAllContacts()

}