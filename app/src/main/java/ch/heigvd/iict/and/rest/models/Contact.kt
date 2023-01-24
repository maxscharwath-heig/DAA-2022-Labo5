package ch.heigvd.iict.and.rest.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var state: ContactState,
    var name: String,
    var firstname: String?,
    var birthday: String?,
    var email: String?,
    var address: String?,
    var zip: String?,
    var city: String?,
    var type: PhoneType?,
    var phoneNumber: String?
) {

    // TODO: a better way ?
    constructor() : this(
        null,
        ContactState.CREATED,
        "",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
    )

    fun isSynced() = state == ContactState.SYNCED

    override fun toString(): String {
        return "Contact(id: $id, name: $name, firstname: $firstname, " +
                "birthday: $birthday, email :$email, address: $address, zip: $zip, city: $city, " +
                "type: $type, phoneNumber: $phoneNumber)"
    }
}