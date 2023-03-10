package ch.heigvd.iict.and.rest.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.heigvd.iict.and.rest.R
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.PhoneType

/**
 * Display list of contacts
 *
 * @author Nicolas Crausaz
 * @author Lazar Pavicevic
 * @author Maxime Scharwath
 */
@Composable
fun ScreenContactList(contacts: List<Contact>, onContactSelected: (Contact) -> Unit) {
    Column {
        Text(text = stringResource(R.string.screen_list_title), fontSize = 24.sp)
        if (contacts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.screen_list_empty),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(contacts) { item ->
                    ContactItemView(item) { clickedContact ->
                        onContactSelected(clickedContact)
                    }
                }
            }
        }
    }
}

@Composable
fun ContactItemView(contact: Contact, onClick: (Contact) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(2.dp)
            .clickable {
                onClick(contact)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.contact),
            contentDescription = stringResource(id = R.string.screen_list_contacticon_ctndesc),
            colorFilter = ColorFilter.tint(if (contact.isSynced()) Color.Green else Color(0xFFFF9768)),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp, vertical = 2.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "${contact.firstname} ${contact.name}")
            Text(text = "${contact.phoneNumber}")
        }
        Image(
            painter = painterResource(
                id = when (contact.type) {
                    PhoneType.MOBILE -> R.drawable.cellphone
                    PhoneType.FAX -> R.drawable.fax
                    PhoneType.HOME -> R.drawable.phone
                    PhoneType.OFFICE -> R.drawable.office
                    else -> R.drawable.office
                }
            ),
            contentDescription = stringResource(id = R.string.screen_list_contacttype_ctndesc)
        )
    }
}