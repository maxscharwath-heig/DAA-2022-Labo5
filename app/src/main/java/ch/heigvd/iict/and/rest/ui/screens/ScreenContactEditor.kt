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
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.PhoneType

@Composable
fun ScreenContactEditor (contact: Contact?, onQuit: () -> Unit) {

    val (selectedOption, onOptionSelected) = remember { mutableStateOf(PhoneType.HOME) } // tmp test

    Column(modifier = Modifier.padding(6.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = stringResource(R.string.screen_detail_title_new), fontSize = 22.sp)

        // todo: map functions to contact object

        ContactEditRow(stringResource(R.string.screen_detail_name_subtitle), "", onValueChange = { it })
        ContactEditRow(stringResource(R.string.screen_detail_firstname_subtitle), "", onValueChange = { it })
        ContactEditRow(stringResource(R.string.screen_detail_birthday_subtitle), "", onValueChange = { it }) // Pas besoin de date picker, readonly
        ContactEditRow(stringResource(R.string.screen_detail_address_subtitle), "", onValueChange = { it })
        ContactEditRow(stringResource(R.string.screen_detail_zip_subtitle), "", onValueChange = { it })
        ContactEditRow(stringResource(R.string.screen_detail_city_subtitle), "", onValueChange = { it })

        Text(text = stringResource(R.string.screen_detail_phonetype_subtitle), fontSize = 18.sp, modifier = Modifier.padding(6.dp, 0.dp))
        Row {
            PhoneType.values().forEach {
                RadioButton(
                    selected = (selectedOption == contact?.type),
                    onClick = { onOptionSelected(it) }
                )
                Text(
                    text = it.toString(),
                )
            }
        }

        ContactEditRow(stringResource(R.string.screen_detail_phonenumber_subtitle), "", onValueChange = { it })


        // TODO: utiliser les bons icônes
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement= Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { onQuit }, ) {
                Text(text= stringResource(R.string.screen_detail_btn_cancel))
            }
            Button(onClick = { /*TODO*/ }) {
                Text(text= stringResource(R.string.screen_detail_btn_delete))
                Icon(Icons.Default.Delete ,contentDescription = stringResource(R.string.screen_detail_btn_delete))
            }
            Button(onClick = { /*TODO*/ }) {
                Text(text= stringResource(R.string.screen_detail_btn_save))
                Icon(Icons.Default.Add ,contentDescription = stringResource(R.string.screen_detail_btn_save))
            }
        }
    }
}

@Composable
fun ContactEditRow(label: String, value: String, onValueChange: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(48.dp), horizontalArrangement= Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {

        Box {
            Text(text = label, fontSize = 18.sp, modifier = Modifier.padding(6.dp, 0.dp))
        }

        Box {
            TextField(value = value , onValueChange = {onValueChange})
        }
    }
}