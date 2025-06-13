package com.evankeane.assessment3.ui.theme.screen


import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.evankeane.assessment3.R
import androidx.compose.ui.text.input.KeyboardType


@Composable
fun UpdateMobilDialog(
    bitmap: Bitmap?, // Ini adalah bitmap awal yang diteruskan ke dialog
    currentNamaMobil: String,
    currentHargaMobil: String,
    currentTahunMobil: String,
    onDismissRequest: () -> Unit,
    onConfirmation: (nama: String, harga: String, tahun: String, bitmap: Bitmap) -> Unit
) {
    var namaMobil by remember { mutableStateOf(currentNamaMobil) }
    var harga by remember { mutableStateOf(currentHargaMobil) }
    var tahun by remember { mutableStateOf(currentTahunMobil) }


    val context = LocalContext.current
    var bitmapToEdit: Bitmap? by remember { mutableStateOf(null) } // State untuk bitmap yang baru dipilih/dipotong
    val launcher = rememberLauncherForActivityResult(CropImageContract()) {
        bitmapToEdit = getCroppedImage(context.contentResolver, it) // Perbarui bitmapToEdit setelah crop
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                bitmap?.let {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                    ) {
                        // Tampilkan bitmapToEdit jika sudah ada, jika tidak, tampilkan bitmap awal
                        Image(
                            bitmap = (bitmapToEdit ?: it).asImageBitmap(), // Perubahan di sini
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                        IconButton(
                            onClick = {
                                val options = CropImageContractOptions(
                                    null, // Set ke null agar CropImageContract memilih dari galeri/kamera
                                    CropImageOptions(
                                        imageSourceIncludeGallery = true,
                                        imageSourceIncludeCamera = true,
                                        fixAspectRatio = true
                                    )
                                )
                                launcher.launch(options) // Luncurkan cropper
                            },
                            modifier = Modifier
                                .align(Alignment.Center)
                                .background(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                                .size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Mobil",
                                tint = Color.White
                            )
                        }
                    }
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
                OutlinedTextField(
                    value = harga,
                    onValueChange = { harga = it },
                    label = { Text(text = stringResource(id = R.string.harga)) },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )

                OutlinedTextField(
                    value = tahun,
                    onValueChange = { tahun = it },
                    label = { Text(text = stringResource(id = R.string.tahun)) },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.batal))
                    }
                    OutlinedButton(
                        onClick = {
                            // Kirimkan bitmapToEdit jika sudah diperbarui, jika tidak, kirimkan bitmap awal
                            onConfirmation(namaMobil, harga, tahun, bitmapToEdit ?: bitmap!!)
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(R.string.simpan))
                    }
                }
            }
        }
    }
}

private fun getCroppedImage(
    resolver: ContentResolver,
    result: CropImageView.CropResult
) : Bitmap? {
    if (!result.isSuccessful) {
        Log.e("MOBIL", "Error: ${result.error}")
        return null
    }

    val uri = result.uriContent ?: return null

    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(resolver, uri)
    } else {
        val source = ImageDecoder.createSource(resolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}