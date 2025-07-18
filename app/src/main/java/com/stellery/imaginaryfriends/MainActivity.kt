package com.stellery.imaginaryfriends

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Website.URL
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellery.imaginaryfriends.ui.theme.ImaginaryFriendsTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class MainActivity : ComponentActivity() {
    lateinit var contacts: List<Contact>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var storage = ContactsLocalLoader()

        var urlString = resources.getString(R.string.internet_request)

        //-------------inet time!------------------



        //-------------inet time!------------------

        //to read from local storage
        val file = File(filesDir, resources.getString(R.string.contacts_file))
        // read from Randomuser test file
        var filestr: String = resources.openRawResource(R.raw.local_contacts).bufferedReader().readText()
        val str = "${filestr.removePrefix("{\"results\":").substringBefore("]")}]"

        val regex = Regex("\"number\":[0-9]+|\"postcode\":[0-9]+")
        val jsonString = str.replace(regex) { "${it.value.replace(":", ":\"")}\"" }

        contacts = Json.decodeFromString<List<Contact>>(jsonString).sortedBy { it.name.last }

        enableEdgeToEdge()
        setContent {
            ImaginaryFriendsTheme {
                scaffoldContacts(contacts)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // maybe something more
    }
}


@Composable
fun scaffoldContacts (contactSet: List<Contact>) {
    val context = LocalContext.current
    val json = Json { // this: JsonBuilder
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    Scaffold(
            floatingActionButton = {
            FloatingActionButton(onClick = {
                context.startActivity(Intent(context, FriendActivity::class.java))
                //TODO обновление списка контактов
            }) {
                Icon(Icons.Default.Refresh, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                //.padding(10.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            for (i in contactSet) {
                SingleContact(i, modifier = Modifier.clickable {
                    var intent = Intent(context, FriendActivity::class.java)
                    var jsonstr = json.encodeToString(i)
                    intent.putExtra(Contact.EXTRA_KEY_CONTACT, jsonstr)
                    context.startActivity(intent)
                })
            }
        }
    }
}

@Serializable
class TestDataClass (val a: Int = 0)

@Composable
fun SingleContact(contact: Contact, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(vertical = 20.dp, horizontal = 10.dp))
    {
        Image(
            painter = painterResource(id = R.drawable.default_avatar),
            contentDescription = "Some Odd Creature",
            contentScale = ContentScale.Crop,            // crop the image if it's not a square
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)                       // clip to the circle shape
                .align(Alignment.CenterVertically)
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.onPrimary,
                    CircleShape
                )   // add a border (optional)
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