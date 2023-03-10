package ch.heigvd.iict.and.rest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
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
    val contacts : List<Contact> by contactsViewModel.allContacts.observeAsState(initial = emptyList())
    val editionMode: Boolean? by contactsViewModel.editionMode.observeAsState()
    val editingContact: Contact? by contactsViewModel.editingContact.observeAsState()

    Scaffold(
        topBar = {
            if (editionMode == true) {
                EditionMenu(contactsViewModel = contactsViewModel)
            } else {
                HomeMenu(contactsViewModel = contactsViewModel)
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            if (editionMode == false) {
                FloatingActionButton(onClick = {
                    contactsViewModel.toggleEditionMode(true)
                    contactsViewModel.setEditingContact(null)
                }){
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        },
    )
    { padding ->
        Column(modifier = Modifier.padding(padding)) { }

        if (editionMode == true) {
            ScreenContactEditor(contactViewModel = contactsViewModel, contact= editingContact) {
                contactsViewModel.toggleEditionMode(false)
            }
        } else {
            ScreenContactList(contacts) { selectedContact ->
                contactsViewModel.toggleEditionMode(true)
                contactsViewModel.setEditingContact(selectedContact)
            }
        }
    }
}
@Composable
fun HomeMenu(contactsViewModel: ContactsViewModel) {
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
}

@Composable
fun EditionMenu(contactsViewModel: ContactsViewModel) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name)) },
        navigationIcon = {
            IconButton(onClick = {
                contactsViewModel.toggleEditionMode(false)
                contactsViewModel.setEditingContact(null)
            }) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
        }
    )
}


