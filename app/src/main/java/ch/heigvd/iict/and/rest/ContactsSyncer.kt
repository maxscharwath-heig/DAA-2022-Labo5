package ch.heigvd.iict.and.rest

import ch.heigvd.iict.and.rest.models.Contact
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GsonRequest<T>(
    url: String,
    private val clazz: Class<T>,
    method: Int,
    private val headers: MutableMap<String, String>?,
    private val listener: Response.Listener<T>,
    errorListener: Response.ErrorListener
) : Request<T>(method, url, errorListener) {
    private val gson = Gson()
    override fun getHeaders(): MutableMap<String, String> = headers ?: super.getHeaders()
    override fun deliverResponse(response: T) = listener.onResponse(response)
    override fun parseNetworkResponse(response: NetworkResponse?): Response<T> {
        return try {
            val json = String(response?.data ?: ByteArray(0),
                Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))
            Response.success(
                gson.fromJson(json, clazz),
                HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            Response.error(ParseError(e))
        } catch (e: JsonSyntaxException) {
            Response.error(ParseError(e))
        }
    }
}

class ContactsSyncer(private val context: ContactsApplication, private val token: String) {
    private val queue = Volley.newRequestQueue(context)

    suspend fun getContacts() = suspendCoroutine<List<Contact>> { continuation ->
        val jsonRequest = GsonRequest(
            "https://daa.iict.ch/contacts",
            mutableListOf<Contact>()::class.java,
            Request.Method.GET,
            mutableMapOf("X-UUID" to token),
            { response -> continuation.resume(response)
            },
            { error -> continuation.resumeWithException(error)
            }
        )
        queue.add(jsonRequest)
    }

    suspend fun createContact(contact: Contact) = suspendCoroutine<Contact> { continuation ->
        val jsonRequest = GsonRequest(
            "https://daa.iict.ch/contacts",
            Contact::class.java,
            Request.Method.POST,
            mutableMapOf("X-UUID" to token, "Content-Type" to "application/json"),
            { response -> continuation.resume(response)
            },
            { error -> continuation.resumeWithException(error)
            }
        )
        queue.add(jsonRequest)
    }
}