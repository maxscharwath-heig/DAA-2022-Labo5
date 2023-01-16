package ch.heigvd.iict.and.rest.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.heigvd.iict.and.rest.ContactsApplication
import ch.heigvd.iict.and.rest.R
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModel
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModelFactory

@Composable
fun AppContact(application: ContactsApplication, contactsViewModel : ContactsViewModel = viewModel(factory= ContactsViewModelFactory(application))) {
    val context = LocalContext.current
    val contacts : List<Contact> by contactsViewModel.allContacts.observeAsState(initial = emptyList())
    val editionMode: Boolean? by contactsViewModel.editionMode.observeAsState()
    val editingContact: Contact? by contactsViewModel.editingContact.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = {
                        contactsViewModel.enroll()
                    }) { Icon(painter = painterResource(R.drawable.populate), contentDescription = null) }
                    IconButton(onClick = {
                        contactsViewModel.refresh()
                    }) { Icon(painter = painterResource(R.drawable.synchronize), contentDescription = null) }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                contactsViewModel.toggleEditionMode(true)
                contactsViewModel.setEditingContact(null)
            }){
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
    )
    { padding ->
        Column(modifier = Modifier.padding(padding)) { }

        if (editionMode == true) {
            ScreenContactEditor(contact= editingContact) {
                contactsViewModel.editionMode.value = false
            }
        } else {
            ScreenContactList(contacts) { selectedContact ->
                contactsViewModel.toggleEditionMode(true)
                contactsViewModel.setEditingContact(selectedContact)
                Toast.makeText(context, "Edition de ${selectedContact.firstname} ${selectedContact.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}