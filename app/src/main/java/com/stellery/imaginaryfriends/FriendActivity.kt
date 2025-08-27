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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.stellery.imaginaryfriends.ui.theme.ImaginaryFriendsTheme

class FriendActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var contact = Contact.getTestContact()
        super.onCreate(savedInstanceState)

        val contactString = intent.getStringExtra(Contact.EXTRA_KEY_CONTACT)
        if (contactString != null)
            contact = Contact.fromJson(contactString)

        enableEdgeToEdge()
        setContent {
            ImaginaryFriendsTheme {
                Scaffold(containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    floatingActionButton = {
                    FloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        onClick = {
                        finish()
                    }) {
                        Icon(Icons.Default.Done, contentDescription = "Done")
                    }
                }) { innerPadding ->
                    ContactCard(contact, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ContactCard (contact: Contact, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding( vertical = 50.dp, horizontal = 10.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.default_avatar),
            contentDescription = "Some Odd Creature",
            contentScale = ContentScale.Crop,            // crop the image if it's not a square
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)                       // clip to the circle shape
                .align(Alignment.CenterHorizontally)
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.onPrimary,
                    CircleShape
                )   // add a border (optional)
        )
        Text(
            text = "${contact.name.title} ${contact.name.first} ${contact.name.last}",
            modifier = Modifier.padding(top = 13.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
                .align(Alignment.CenterHorizontally),
            fontSize = 25.sp, textAlign = TextAlign.Center
        )
        Text(
            text = "Already ${contact.dob.age} years old",
            modifier = Modifier.padding(top = 13.dp, bottom = 20.dp, start = 10.dp, end = 10.dp)
                .align(Alignment.CenterHorizontally),
            fontSize = 17.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary
        )
        // make a clickable textfield to make a phonecall
        Text(
            text = "tel: ${contact.phone}",
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 10.dp)
                .align(Alignment.CenterHorizontally)
                .clickable { context.startActivity (Intent(Intent.ACTION_DIAL)
                    .setData("tel:${contact.phone}".toUri()))},
            fontSize = 20.sp, textAlign = TextAlign.Center
        )
        // make a clickable textfield to send Email
        Text(
            text = "email: ${contact.email}",
            modifier = Modifier.padding(top = 3.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
                .align(Alignment.CenterHorizontally)
                .clickable { context.startActivity (Intent(Intent.ACTION_SENDTO)
                    .setData("mailto:${contact.email}".toUri()))},
            fontSize = 20.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.tertiary
        )
        Text(
            text = "${contact.location?.city}, ${contact.location?.country}",
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 10.dp)
                .align(Alignment.CenterHorizontally),
            fontSize = 20.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary
        )
        Text(
                text = "${contact.location?.street?.name} ${contact.location?.street?.number}",
        modifier = Modifier.padding(vertical = 2.dp, horizontal = 10.dp)
            .align(Alignment.CenterHorizontally),
        fontSize = 18.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    ImaginaryFriendsTheme {
        ContactCard(Contact.getTestContact())
    }
}