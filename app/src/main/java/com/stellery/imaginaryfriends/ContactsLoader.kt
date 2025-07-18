package com.stellery.imaginaryfriends

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface ContactsLoader {
    fun refreshContacts(): List<Contact>
    fun getContacts(): List<Contact>
}

// он должен быть синглтон, но что-то я не успеваю ):
class ContactsLocalLoader : ContactsLoader {
    lateinit var contacts: ArrayList<Contact>
    init {
        contacts = ArrayList<Contact>()
        contacts.add(Contact.getTestContact())
        var tmp_cont = Contact.getTestContact()
        tmp_cont.name = Name("Masha", "Medvedeva", "Mrs")
        contacts.add (tmp_cont)
        tmp_cont = Contact.getTestContact()
        tmp_cont.name = Name("Misha", "Medvedev", "Mr")
        tmp_cont.phone = "+79121212244"
        contacts.add (tmp_cont)

    }

    fun contactsToString() : String {
        return Json.encodeToString(contacts)
    }

    override fun refreshContacts(): List<Contact> {
       // TODO("Not yet implemented")
        var newContact = Contact.getTestContact()
        newContact.name.first = "${newContact.name.first}@"
        contacts.add(newContact)
        return contacts
    }
    override fun getContacts(): List<Contact> {
        return contacts
    }

}

@Serializable
data class Contact (var name: Name,
                    var email: String,
                    var phone: String,
                    var dob: Dob,
                    var location: Location?,
                    var picture: Picture?) {
    companion object {
        const val EXTRA_KEY_CONTACT = "EXTRA_CONTACT"

        fun getTestContact(): Contact {
            val testobj = Contact(
                Name("Cheburashka", "Krokodilov", "Mr"),
                "chebur@sh.ca",
                "+79232333322",
                Dob ("1969-08-20T00:21:36.863Z", 57),
                Location( Street("Kremlin", "1A"),
                    "Moscow",
                    "Russia",
                    "Moscow Obl",
                    "460036",
                    Coordinates("01", "01"),
                    Timezone("-3:00", "Something")
                ),
                Picture("res/drawable/default_avatar", "", "")
            )
            return testobj
        }

        fun fromJson (str: String): Contact {
            return Json.decodeFromString(str)
        }
        fun toJson (contact: Contact): String {
            return Json.encodeToString(contact)
        }
    }
}

@Serializable
data class Name ( var first: String, var last: String, var title: String?)

@Serializable
data class Coordinates (var latitude: String, var longitude: String)

@Serializable
data class Street (var name: String, var number: String?)

@Serializable
data class Timezone (var offset: String, var description: String)

@Serializable
data class Dob (var date: String, var age: Int)


@Serializable
data class Location (
    var street: Street,
    var city: String,
    var country: String,
    var state: String?,
    var postcode: String,
    var coordinates: Coordinates,
    var timezone: Timezone
)
@Serializable
data class Picture (
    var large: String,
    var medium: String,
    var thumbnail: String
)