package ch.heigvd.iict.and.rest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.heigvd.iict.and.rest.R
import ch.heigvd.iict.and.rest.database.converters.CalendarConverter
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.PhoneType
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScreenContactEditor(
    contactViewModel: ContactsViewModel,
    contact: Contact?,
    onQuit: () -> Unit
) {
    // TODO: etat sauvegardé: https://developer.android.com/jetpack/compose/state#restore-ui-state

    Column(modifier = Modifier.padding(6.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = stringResource(R.string.screen_detail_title_new), fontSize = 22.sp)

        ContactEditRow(
            stringResource(R.string.screen_detail_name_subtitle),
            contact?.name ?: "",
            onValueChange = {
                contact?.name = it
            })
        ContactEditRow(
            stringResource(R.string.screen_detail_firstname_subtitle),
            contact?.firstname ?: "",
            onValueChange = { contact?.firstname = it })
        ContactEditRow(
            stringResource(R.string.screen_detail_birthday_subtitle),
            contact?.birthday ?: "",
            onValueChange = {}) // Pas besoin de date picker, readonly ? // todo: ajouter readonly prop
        ContactEditRow(
            stringResource(R.string.screen_detail_address_subtitle),
            contact?.address ?: "",
            onValueChange = { contact?.address = it })
        ContactEditRow(
            stringResource(R.string.screen_detail_zip_subtitle),
            contact?.zip ?: "",
            onValueChange = { contact?.zip = it })
        ContactEditRow(
            stringResource(R.string.screen_detail_city_subtitle),
            contact?.city ?: "",
            onValueChange = { contact?.city = it })

        Text(
            text = stringResource(R.string.screen_detail_phonetype_subtitle),
            fontSize = 18.sp,
            modifier = Modifier.padding(6.dp, 0.dp)
        )

        PhoneTypeRadioGroup(selected = contact?.type ?: PhoneType.HOME) {
            contact?.type = it
        }

        ContactEditRow(
            stringResource(R.string.screen_detail_phonenumber_subtitle),
            contact?.phoneNumber ?: "",
            onValueChange = { contact?.phoneNumber = it })

        ButtonSection(editionMode = contact != null) {
            when (it) {
                "quit" -> onQuit()
                "delete" -> {
                    contactViewModel.delete(contact!!)
                    onQuit()
                }
                "save" -> {
                    contactViewModel.update(contact!!)
                    onQuit()
                }
                "create" -> {
                    contactViewModel.create(contact!!)
                    onQuit()
                }
            }
        }

    }
}

@Composable
fun ContactEditRow(label: String, value: String, onValueChange: (String) -> Unit) {
    val rValue = remember {
        mutableStateOf(value)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box {
            Text(text = label, fontSize = 18.sp, modifier = Modifier.padding(6.dp, 0.dp))
        }

        Box {
            TextField(value = rValue.value, onValueChange = {
                rValue.value = it
                onValueChange(rValue.value)
            })
        }
    }
}

@Composable
fun ButtonSection(editionMode: Boolean, onAction: (String) -> Unit) {
    // TODO: utiliser les bons icônes
    when (editionMode) {
        true -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { onAction("quit") }) {
                    Text(text = stringResource(R.string.screen_detail_btn_cancel))
                }
                Button(onClick = { onAction("delete") }) {
                    Text(text = stringResource(R.string.screen_detail_btn_delete))
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.screen_detail_btn_delete)
                    )
                }
                Button(onClick = { onAction("save") }) {
                    Text(text = stringResource(R.string.screen_detail_btn_save))
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.screen_detail_btn_save)
                    )
                }
            }
        }
        false -> Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,


            ) {
            Button(onClick = { onAction("quit") }) {
                Text(text = stringResource(R.string.screen_detail_btn_cancel))
            }
            Button(onClick = { onAction("create") }) {
                Text(text = stringResource(R.string.screen_detail_btn_save))
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.screen_detail_btn_create)
                )
            }
        }
    }
}

@Composable
fun PhoneTypeRadioGroup(selected: PhoneType?, onSelect: (PhoneType) -> Unit) {
    val choices = PhoneType.values()
    val (sel, setSelected) = remember { mutableStateOf(selected) }

    // TODO: click sur label doit selectionner la radio
    Row(verticalAlignment = Alignment.CenterVertically) {
        choices.forEach {
            RadioButton(
                selected = (it == sel),
                onClick = {
                    setSelected(it)
                    onSelect(it)
                }
            )
            Text(
                text = it.toString(),
            )
        }
    }
}

fun formatCalendar(cal: Calendar?): String {
    if (cal == null) {
        return ""
    }

    val time = CalendarConverter().fromCalendar(cal)
    val sdf = SimpleDateFormat("dd/M/yyyy", Locale.FRENCH)
    return sdf.format(time)
}
