package ch.heigvd.iict.and.rest.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.*

// TODO: add remote id


@Serializable
@Entity
data class Contact(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @Transient
    var state: ContactState = ContactState.CREATED,
    var name: String,
    var firstname: String?,
    @Serializable(CalendarSerializer::class)
    var birthday: Calendar?,
    var email: String?,
    var address: String?,
    var zip: String?,
    var city: String?,
    var type: PhoneType?,
    var phoneNumber: String?
) {

    constructor() : this(
        null,
        ContactState.CREATED,
        "",
        "",
        null,
        "",
        "",
        "",
        "",
        PhoneType.HOME,
        "",
    )

    fun isSynced() = state == ContactState.SYNCED

    override fun toString(): String {
        return "Contact(id: $id, name: $name, firstname: $firstname, " +
                "birthday: $birthday, email :$email, address: $address, zip: $zip, city: $city, " +
                "type: $type, phoneNumber: $phoneNumber)"
    }
}

object CalendarSerializer: KSerializer<Calendar> {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    
    override val descriptor: SerialDescriptor
        = PrimitiveSerialDescriptor("Calendar", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(decoder.decodeString())
        return calendar
    }

    override fun serialize(encoder: Encoder, value: Calendar) {
        encoder.encodeString(dateFormat.format(value.time))
    }
}