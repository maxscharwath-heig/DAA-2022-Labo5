package ch.heigvd.iict.and.rest

import ch.heigvd.iict.and.rest.database.ContactsDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

}