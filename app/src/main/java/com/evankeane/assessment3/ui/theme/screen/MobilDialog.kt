package com.evankeane.assessment3.ui.theme.screen



import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.evankeane.assessment3.R
import com.evankeane.assessment3.ui.theme.Assessment3Theme

@Composable
fun MobilDialog(
    bitmap: Bitmap?,
    onDismissRequest: () -> Unit,
    onConfirmation: (String, String, String) -> Unit
) {
    var namaMobil by remember { mutableStateOf("") }
    var hargaMobil by remember { mutableStateOf("") }
    var tahun by remember { mutableStateOf("") }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                )
            }
            OutlinedTextField(
                value = namaMobil,
                onValueChange = { namaMobil = it },
                label = { Text(text = stringResource(id = R.string.nama)) },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.padding(top = 8.dp)
            )



//            OutlinedTextField(
//                value = hargaMobil,
//                onValueChange = { hargaMobil = it },
//                label = { Text(text = stringResource(id = R.string.harga)) },
//                maxLines = 1,
//                keyboardOptions = KeyboardOptions(
//                    capitalization = KeyboardCapitalization.Sentences,
//                    imeAction = ImeAction.Done
//                ),
//                modifier = Modifier.padding(top = 8.dp)
//            )
//            OutlinedTextField(
//                value = tahun,
//                onValueChange = { tahun = it },
//                label = { Text(text = stringResource(id = R.string.tahun)) },
//                maxLines = 1,
//                keyboardOptions = KeyboardOptions(
//                    capitalization = KeyboardCapitalization.Sentences,
//                    imeAction = ImeAction.Done
//                ),
//                modifier = Modifier.padding(top = 8.dp)
//            )

            OutlinedTextField(
                value = hargaMobil,
                onValueChange = { hargaMobil = it },
                label = { Text(text = stringResource(id = R.string.harga)) },
                leadingIcon = {
                    Text(
                        text = "Rp",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.padding(top = 8.dp)
            )


            OutlinedTextField(
                value = tahun,
                onValueChange = { tahun = it },
                label = { Text(text = stringResource(id = R.string.tahun)) },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, // <-- di sini
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    onClick = { onDismissRequest() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = stringResource(id = R.string.batal))
                }
                OutlinedButton(
                    onClick = { onConfirmation(namaMobil, hargaMobil,tahun) },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = stringResource(R.string.simpan))
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AddDialogPreview() {
    Assessment3Theme  {
        MobilDialog(
            bitmap = null,
            onDismissRequest = {},
            onConfirmation = { _, _, _ -> }
        )
    }
}