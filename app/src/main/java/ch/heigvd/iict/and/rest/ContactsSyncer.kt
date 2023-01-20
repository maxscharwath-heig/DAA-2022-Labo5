package ch.heigvd.iict.and.rest

import android.util.Log
import ch.heigvd.iict.and.rest.models.Contact
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ContactsSyncer(private val context: ContactsApplication) {
    private val queue = Volley.newRequestQueue(context)

    suspend fun getContacts() = suspendCoroutine<List<Contact>> { continuation ->
        val url = "https://daa.iict.ch/contacts"
        // GsonGetRequest WHERE ARE YOU MOTHERFUCKER
        val jsonRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                Log.d("ContactsSyncer", "Response: $response")

            },
            { error ->
                Log.d("ContactsSyncer", "Error: $error")
                continuation.resumeWithException(error)
            }
        )
        queue.add(jsonRequest)
    }
}