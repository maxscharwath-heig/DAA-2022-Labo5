package ch.heigvd.iict.and.rest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.PhoneType
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Edition of contact
 *
 * @author Nicolas Crausaz
 * @author Lazar Pavicevic
 * @author Maxime Scharwath
 */
@Composable
fun ScreenContactEditor(
    contactViewModel: ContactsViewModel,
    contact: Contact?,
    onQuit: () -> Unit,
) {
    val tmpContact = remember {
        mutableStateOf(contact?.copy() ?: Contact())
    }

    val tmpBirthday = remember {
        mutableStateOf(formatCalendar(contact?.birthday))
    }
    Column(
        modifier = Modifier
            .padding(6.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = stringResource(R.string.screen_detail_title_new), fontSize = 22.sp)

        ContactEditRow(
            stringResource(R.string.screen_detail_name_subtitle),
            tmpContact.value.name,
            onValueChange = {
                tmpContact.value.name = it
            })
        ContactEditRow(
            stringResource(R.string.screen_detail_firstname_subtitle),
            tmpContact.value.firstname ?: "",
            onValueChange = { tmpContact.value.firstname = it })
        ContactEditRow(
            stringResource(R.string.screen_detail_birthday_subtitle),
            tmpBirthday.value,
            onValueChange = { tmpBirthday.value = it }
        )
        ContactEditRow(
            stringResource(R.string.screen_detail_address_subtitle),
            tmpContact.value.address ?: "",
            onValueChange = { tmpContact.value.address = it })
        ContactEditRow(
            stringResource(R.string.screen_detail_zip_subtitle),
            tmpContact.value.zip ?: "",
            onValueChange = { tmpContact.value.zip = it })
        ContactEditRow(
            stringResource(R.string.screen_detail_city_subtitle),
            tmpContact.value.city ?: "",
            onValueChange = { tmpContact.value.city = it })

        Text(
            text = stringResource(R.string.screen_detail_phonetype_subtitle),
            fontSize = 18.sp,
            modifier = Modifier.padding(6.dp, 0.dp)
        )

        PhoneTypeRadioGroup(selected = tmpContact.value.type ?: PhoneType.HOME) {
            tmpContact.value.type = it
        }

        ContactEditRow(
            stringResource(R.string.screen_detail_phonenumber_subtitle),
            tmpContact.value.phoneNumber ?: "",
            onValueChange = { tmpContact.value.phoneNumber = it })

        ButtonSection(editionMode = contact != null) {
            when (it) {
                "quit" -> onQuit()
                "delete" -> {
                    contactViewModel.delete(tmpContact.value!!)
                    onQuit()
                }
                "save" -> {
                    tmpContact.value.birthday = calendarFromString(tmpBirthday.value)
                    contactViewModel.update(tmpContact.value!!)
                    onQuit()
                }
                "create" -> {
                    tmpContact.value.birthday = calendarFromString(tmpBirthday.value)
                    contactViewModel.create(tmpContact.value!!)
                    onQuit()
                }
            }
        }
    }
}

@Composable
fun ContactEditRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    readonly: Boolean = false
) {
    val rValue = remember {
        mutableStateOf(value)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
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
            }, readOnly = readonly)
        }
    }
}

@Composable
fun ButtonSection(editionMode: Boolean, onAction: (String) -> Unit) {
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
    val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val str = cal?.let {
        dateFormatter.format(LocalDate.of(it.get(Calendar.YEAR), it.get(Calendar.MONTH) + 1, it.get(Calendar.DAY_OF_MONTH)))
    }
    return str ?: ""
}

fun calendarFromString(str: String): Calendar? {
    val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val date = LocalDate.parse(str, dateFormatter)
    return Calendar.getInstance().apply {
        set(date.year, date.monthValue - 1, date.dayOfMonth)
    }
}