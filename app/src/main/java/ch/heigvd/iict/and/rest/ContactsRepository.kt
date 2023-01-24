package ch.heigvd.iict.and.rest

import ch.heigvd.iict.and.rest.database.ContactsDao
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.ContactState
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.text.Charsets.UTF_8

class ContactsRepository(
    private val contactsDao: ContactsDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    val allContacts = contactsDao.getAllContactsLiveData()

    suspend fun enroll(): String = withContext(dispatcher) {
        val url = URL("https://daa.iict.ch/enroll")
        url.readText(UTF_8)
    }

    suspend fun getAllAndInsert(uuid: String) = withContext(Dispatchers.IO) {
        val url = URL("https://daa.iict.ch/contacts")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("X-UUID", uuid)

        val json = connection.inputStream.bufferedReader(UTF_8).use {
            it.readText()
        }

        val contacts = Gson().fromJson(json, Array<Contact>::class.java)
        contacts.forEach { c ->
            c.state = ContactState.SYNCED
            contactsDao.insert(c)
        }
    }

    suspend fun create(contact: Contact,  uuid: String) = withContext(Dispatchers.IO) {
        val url = URL("https://daa.iict.ch/contacts")
        val json = Gson().toJson(contact)
        var toCreate: Contact = contact

        try {
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("X-UUID", uuid)
            connection.outputStream.bufferedWriter(UTF_8).use {
                it.append(json)
            }

            if (connection.responseCode == 201) {
                val cJson = connection.inputStream.bufferedReader(UTF_8).use {
                    it.readText()
                }
                toCreate = Gson().fromJson(cJson, Contact::class.java)
                toCreate.state = ContactState.SYNCED
            }
        } catch (e: IOException) {
            toCreate.state = ContactState.CREATED
        }
        contactsDao.insert(toCreate)
    }

    suspend fun update(contact: Contact,  uuid: String) = withContext(Dispatchers.IO) {
        val contactId = contact.id
        val url = URL("https://daa.iict.ch/contacts/$contactId")
        val json = Gson().toJson(contact)

        try {
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "PUT"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("X-UUID", uuid)
            connection.outputStream.bufferedWriter(UTF_8).use {
                it.append(json)
            }

            if (connection.responseCode == 200) {
                contact.state = ContactState.SYNCED
            }
        } catch (e: IOException) {
            contact.state = ContactState.UPDATED
        }

        contactsDao.update(contact)
    }

    suspend fun delete(contact: Contact, uuid: String) = withContext(Dispatchers.IO) {
        val contactId = contact.id
        val url = URL("https://daa.iict.ch/contacts/$contactId")
        val json = Gson().toJson(contact)

        try {
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "DELETE"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("X-UUID", uuid)
            connection.outputStream.bufferedWriter(UTF_8).use {
                it.append(json)
            }

            if (connection.responseCode == 204) {
                contactsDao.delete(contact)
            }
        } catch (e: IOException) {
            contactsDao.softDelete(contact)
        }
    }

    suspend fun retryDirties(uuid: String) = withContext(Dispatchers.IO) {
        allContacts.value!!.forEach { c ->
            if (!c.isSynced()) {
                when(c.state) {
                    ContactState.CREATED -> {
                        create(c, uuid)
                    }
                    ContactState.UPDATED -> {
                        update(c, uuid)
                    }
                    ContactState.DELETED -> {
                        delete(c, uuid)
                    }
                    else -> {}
                }
            }
        }
    }

    suspend fun deleteAll() = withContext(Dispatchers.IO) {
        contactsDao.clearAllContacts()
    }

}