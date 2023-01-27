package ch.heigvd.iict.and.rest

import ch.heigvd.iict.and.rest.database.ContactsDao
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.ContactState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
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
        val conn = getConnection("https://daa.iict.ch/contacts", "GET", uuid)

        val json = conn.inputStream.bufferedReader(UTF_8).use {
            it.readText()
        }

        val contacts = Json.decodeFromString(ListSerializer(Contact.serializer()), json)
        contacts.forEach { c ->
            c.remoteId = c.id
            c.state = ContactState.SYNCED
            contactsDao.insert(c)
        }
    }

    suspend fun create(contact: Contact, uuid: String) = withContext(Dispatchers.IO) {
        val json = Json.encodeToString(Contact.serializer(), contact)

        try {
            val conn = getConnection("https://daa.iict.ch/contacts", "POST", uuid)

            conn.outputStream.bufferedWriter(UTF_8).use {
                it.append(json)
            }

            if (conn.responseCode == 201) {
                val cJson = conn.inputStream.bufferedReader(UTF_8).use {
                    it.readText()
                }
                val created = Json.decodeFromString(Contact.serializer(), cJson)
                contact.remoteId = created.id
                contact.state = ContactState.SYNCED
            }
        } catch (e: IOException) {
            contact.state = ContactState.CREATED
        }
        contactsDao.insert(contact)
    }

    suspend fun update(contact: Contact, uuid: String) = withContext(Dispatchers.IO) {
        val contactId = contact.remoteId
        val json = Json.encodeToString(Contact.serializer(), contact)

        if (contactId != null) {
            try {
                val conn = getConnection("https://daa.iict.ch/contacts/$contactId", "PUT", uuid)

                conn.outputStream.bufferedWriter(UTF_8).use {
                    it.append(json)
                }

                if (conn.responseCode == 200) {
                    contact.state = ContactState.SYNCED
                }
            } catch (e: IOException) {
                if (contact.state != ContactState.SYNCED) {
                    contact.state = ContactState.UPDATED
                }
            }
        }
        contactsDao.update(contact)
    }

    suspend fun delete(contact: Contact, uuid: String) = withContext(Dispatchers.IO) {
        val contactId = contact.remoteId
        val json = Json.encodeToString(Contact.serializer(), contact)

        if (contactId != null) {
            try {
                val conn = getConnection("https://daa.iict.ch/contacts/$contactId", "DELETE", uuid)
                conn.outputStream.bufferedWriter(UTF_8).use {
                    it.append(json)
                }

                if (conn.responseCode == 204) {
                    contactsDao.delete(contact)
                }
            } catch (e: IOException) {
                contactsDao.softDelete(contact)
            }
        } else {
            contactsDao.softDelete(contact)
        }
    }

    suspend fun retryDirties(uuid: String) = withContext(Dispatchers.IO) {
        contactsDao.getAllUnsyncedContacts().forEach { c ->
            when (c.state) {
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

    suspend fun deleteAll() = withContext(Dispatchers.IO) {
        contactsDao.clearAllContacts()
    }

    private fun getConnection(url: String, method: String, uuid: String): HttpURLConnection {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = method
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("X-UUID", uuid)
        return connection
    }
}