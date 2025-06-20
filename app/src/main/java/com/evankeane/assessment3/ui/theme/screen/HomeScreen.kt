package com.evankeane.assessment3.ui.theme.screen

//import android.content.ContentResolver
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
//import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
//import android.os.Build
//import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
//import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.datastore.core.IOException
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
//import com.canhub.cropper.CropImageContract
//import com.canhub.cropper.CropImageView
import com.evankeane.assessment3.BuildConfig
import com.evankeane.assessment3.R
import com.evankeane.assessment3.model.Mobil
import com.evankeane.assessment3.model.User
import com.evankeane.assessment3.network.ApiStatus
import com.evankeane.assessment3.network.MobilApi
import com.evankeane.assessment3.network.UserDataStore
import com.evankeane.assessment3.ui.theme.Assessment3Theme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val dataStore = UserDataStore(context)
    val user by dataStore.userFlow.collectAsState(User())

    val viewModel: MainViewModel = viewModel()
    val errorMessage by viewModel.errorMessage
    val itemToEdit by remember { mutableStateOf<Mobil?>(null) }
    var bitmapToEdit by remember { mutableStateOf<Bitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var showMobilDialog by remember { mutableStateOf(false) }

    val bitmap: Bitmap? by remember { mutableStateOf(null) }
//    val launcher = rememberLauncherForActivityResult(CropImageContract()) {
//        bitmap = getCroppedImage(context.contentResolver, it)
//        if (bitmap != null) showMobilDialog = true
//    }

    LaunchedEffect(itemToEdit) {
        itemToEdit?.let { mobil ->
            coroutineScope.launch(Dispatchers.IO) {
                bitmapToEdit = loadBitmapFromUrl(context, MobilApi.getMobilUrl(mobil.gambar))
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = {
                        if (user.email.isEmpty()) {
                            CoroutineScope(Dispatchers.IO).launch {
                                signIn(context, dataStore)
                            }
                        }
                        else {
                            showDialog = true
                        }

                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_account_circle_24),
                            contentDescription = stringResource(id = R.string.profile),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }

    ) { innerPadding ->
        ScreenContentHome(viewModel,user.email, Modifier.padding(innerPadding))

        if (showDialog) {
            ProfilDialog(
                user = user,
                onDismissRequest = { showDialog = false }) {
                CoroutineScope(Dispatchers.IO).launch { signOut(context, dataStore) }
                showDialog = false
            }
        }

        if (showMobilDialog) {
            MobilDialog(
                bitmap = bitmap,
                onDismissRequest = { showMobilDialog = false }) { nama, harga,tahun -> viewModel.saveData(user.email, nama, harga, tahun, bitmap!!)
                showMobilDialog = false
            }
        }


        if (errorMessage != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }
}

@Composable
fun ScreenContentHome(viewModel: MainViewModel, userId: String ,modifier: Modifier = Modifier) {
    val data by viewModel.data
    val status by viewModel.status.collectAsState()

    LaunchedEffect(userId) {
        viewModel.retrieveData()
    }

    when (status) {
        ApiStatus.LOADING -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        }
        ApiStatus.SUCCESS -> {
            LazyVerticalGrid(
                modifier = modifier.fillMaxSize().padding(4.dp),
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(data) { ListItemHome(mobil = it, userId = userId, onDelete = { id -> viewModel.deleteData(userId, id)}) }
            }
        }

        ApiStatus.FAILED -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(id = R.string.error))
                Button(
                    onClick = { viewModel.retrieveData(userId) },
                    modifier = Modifier.padding(top = 16.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.try_again))
                }
            }
        }


    }
}

private suspend fun signIn(context: Context, dataStore: UserDataStore) {
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private suspend fun handleSignIn(result: GetCredentialResponse, dataStore: UserDataStore) {
    val credential = result.credential
    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleId.displayName ?: ""
            val email = googleId.id
            val photoUrl = googleId.profilePictureUri.toString()
            dataStore.saveData(User(nama, email, photoUrl))
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("SIGN-IN", "Error: ${e.message}")
        }
    }
    else {
        Log.e("SIGN-IN", "Error: unrecognized custom credential type.")
    }
}

private suspend fun signOut(context: Context, dataStore: UserDataStore) {
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User())
    } catch (e: ClearCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}
//
//private fun getCroppedImage(
//    resolver: ContentResolver,
//    result: CropImageView.CropResult
//) : Bitmap? {
//    if (!result.isSuccessful) {
//        Log.e("IMAGE", "Error: ${result.error}")
//        return null
//    }
//
//    val uri = result.uriContent ?: return null
//
//    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
//        MediaStore.Images.Media.getBitmap(resolver, uri)
//    } else {
//        val source = ImageDecoder.createSource(resolver, uri)
//        ImageDecoder.decodeBitmap(source)
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListItemHome(
    mobil: Mobil,
    userId: String,
    onDelete: (String) -> Unit,
//    onEdit: (Mobil, String, String, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }
    var showConfirmDelete by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    val viewModel: MainViewModel = viewModel()

    val bitmap = rememberBitmapFromUrl(MobilApi.getMobilUrl(mobil.gambar))

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    "Tindakan Produk",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Pilih tindakan untuk \"${mobil.namaMobil}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Edit Button
                Button(
                    onClick = {
                        showSheet = false
                        showEditDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF1F5FF),
                        contentColor = Color(0xFF4C33FF)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                    Spacer(Modifier.width(8.dp))
                    Text("Edit Produk")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Delete Button
                Button(
                    onClick = {
                        showSheet = false
                        showConfirmDelete = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFF1F1),
                        contentColor = Color.Red
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus")
                    Spacer(Modifier.width(8.dp))
                    Text("Hapus Produk")
                }

                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { showSheet = false }) {
                    Text("Tutup")
                }
            }
        }
    }

    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda yakin ingin menghapus mobil ini?") },
            confirmButton = {
                Button(onClick = {
                    showConfirmDelete = false
                    onDelete(mobil.id)
                }) {
                    Text("Ya")
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmDelete = false }) {
                    Text("Tidak")
                }
            }
        )
    }

    // Dialog Edit Mobil
    if (showEditDialog) {
        UpdateMobilDialog(
            bitmap = bitmap, // Gambar bisa diisi kalau kamu ambil dari server
            currentNamaMobil = mobil.namaMobil,
            currentHargaMobil = mobil.hargaMobil,
            currentTahunMobil = mobil.tahun,
            onDismissRequest = { showEditDialog = false },
            onConfirmation = { nama, harga,tahun,newBitmap -> viewModel.updateData(
                userId = userId,
                namaMobil = nama,
                hargaMobil = harga,
                tahun = tahun,
                bitmap = newBitmap, // Pass the new bitmap from the dialog
                id = mobil.id
            )
                showEditDialog = false
//                onEdit(mobil, namaBaru, hargaBaru, tahunBaru)
            }
        )
    }


    // Card Tampilan
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(MobilApi.getMobilUrl(mobil.gambar))
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.gambar, mobil.namaMobil),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.loading_img),
                    error = painterResource(id = R.drawable.broken_img),
                    modifier = Modifier.fillMaxSize()
                )

           
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = mobil.namaMobil,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = mobil.tahun,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Rp. ${mobil.hargaMobil}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

private suspend fun loadBitmapFromUrl(context: Context, url: String): Bitmap? {
    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(url)
        .allowHardware(false)
        .build()
    return try {
        val result = (loader.execute(request) as SuccessResult).drawable
        (result as BitmapDrawable).bitmap
    } catch (e: Exception) {
        Log.e("LoadBitmap", "Failed to load bitmap from URL: $url", e)
        null
    }
}

@Composable
fun rememberBitmapFromUrlHome(url: String): Bitmap? {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(url) {
        withContext(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input: InputStream = connection.inputStream
                bitmap = BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    return bitmap
}



@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun HomeScreenPreview() {
    Assessment3Theme  {
        HomeScreen()
    }
}



