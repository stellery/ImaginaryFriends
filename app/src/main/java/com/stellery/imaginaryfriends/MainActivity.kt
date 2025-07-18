package com.stellery.imaginaryfriends

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellery.imaginaryfriends.ui.theme.ImaginaryFriendsTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    lateinit var contacts: List<Contact>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // read from Randomuser test file stored in resources
        val filestr: String = resources.openRawResource(R.raw.local_contacts).bufferedReader().readText()
        // awful hardcoded stripping (from the -"results":- and final aux info)
        val str = "${filestr.removePrefix("{\"results\":").substringBefore("]")}]"

        // those fields -number- and -postcode- are stored either as Int or as Strings, make them Strings
        val regex = Regex("\"number\":[0-9]+|\"postcode\":[0-9]+")
        val jsonString = str.replace(regex) { "${it.value.replace(":", ":\"")}\"" }

        // I suppose that should be done someway in separate thread, but that's not what should be done in a hurry
        contacts = Json.decodeFromString<List<Contact>>(jsonString).sortedBy { it.name.last }

        enableEdgeToEdge()
        setContent {
            ImaginaryFriendsTheme {
                ScaffoldContacts(contacts)
            }
        }
    }
}


@Composable
fun ScaffoldContacts (contactSet: List<Contact>) {
    val context = LocalContext.current
    val json = Json { // this: JsonBuilder
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            for (i in contactSet) {
                // for each contact create clickable field to show info card in FriendActivity
                SingleContact(i, modifier = Modifier.clickable {
                    val intent = Intent(context, FriendActivity::class.java)
                    val jsonstr = json.encodeToString(i)
                    intent.putExtra(Contact.EXTRA_KEY_CONTACT, jsonstr)
                    context.startActivity(intent)
                })
            }
        }
    }
}

@Composable
fun SingleContact(contact: Contact, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(vertical = 20.dp, horizontal = 10.dp))
    {
        Image(
            painter = painterResource(id = R.drawable.default_avatar),
            contentDescription = "Some Odd Creature",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .align(Alignment.CenterVertically)
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.onPrimary,
                    CircleShape
                )
        )
        Column(
            modifier = Modifier
                .padding(5.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = "${contact.name.title} ${contact.name.first} ${contact.name.last}",
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 10.dp),
                fontSize = 18.sp
            )
            Text(
                text = "tel: ${contact.phone}",
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 10.dp)
            )
            Text(
                text = "email: ${contact.email}",
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 10.dp)
            )
            Text(
                text = "${contact.location?.city}",
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 10.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ImaginaryFriendsTheme {
        SingleContact(Contact.getTestContact())
    }
}