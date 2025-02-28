package com.example.composetutorial.screens


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.composetutorial.MainViewModel
import com.example.composetutorial.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.Manifest




// composable functions

@Composable
fun ProfileScreen(viewModel: MainViewModel, navController: NavController? = null, context: Context) {


    val userName by viewModel.userName.collectAsState(initial = "")
    val userProfilePictureUri by viewModel.userProfilePicture.collectAsState(initial = "")



    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        navController?.let { SettingsBackButton(navController = it) }
        NameChange(currentName = userName,
            onNameChange = { newName -> viewModel.updateName(newName)})
        Spacer(modifier = Modifier.height(50.dp))
        LoadImage(userProfilePictureUri = userProfilePictureUri,
            onProfilePictureChange = { newUrl -> viewModel.updateUserProfilePicture(newUrl)}, mainViewModel = viewModel)
        EnableNotificationsButton(context)


    }
}

@Composable
fun EnableNotificationsButton(context: Context) {
    Button(
        onClick = {
            if (Build.VERSION.SDK_INT >= 33) {
                val activity = context as? Activity
                if (activity != null) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        100
                    )
                }
            } else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }
        },
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "Enable notifications")
    }
}


@Composable
fun LoadImage(userProfilePictureUri: String, onProfilePictureChange: (String) -> Unit, mainViewModel: MainViewModel) {
    val context = LocalContext.current
    val defaultImage = painterResource(id = R.drawable.chunchu)
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            try {
                val inputStream = context.contentResolver.openInputStream(selectedUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                val filename = "profile_picture"
                val directory = context.filesDir
                val file = File(directory, "$filename.jpg")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }

                val internalPath = file.absolutePath
                mainViewModel.updateUserProfilePicture(internalPath)
                onProfilePictureChange(internalPath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val userProfilePictureUri by mainViewModel.userProfilePicture.collectAsState(initial = "")

    val imagePath = if (userProfilePictureUri.startsWith("/")) {
        File(userProfilePictureUri)
    } else {
        userProfilePictureUri?.let { Uri.parse(it) }
    }

    val request = ImageRequest.Builder(context)
        .data(imagePath)
        .error(R.drawable.chunchu)
        .fallback(R.drawable.chunchu)
        .build()

    Column {
        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Change image")
        }
        Image(
            painter = rememberAsyncImagePainter(request),
            contentDescription = "Contact profile picture",
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}


@Composable
fun SettingsBackButton(navController: NavController) {
    Button(
        onClick = { navController.popBackStack()},
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text("Back")
    }
}


@Composable
fun NameChange(currentName: String, onNameChange: (String) -> Unit){
    var name by remember { mutableStateOf(currentName) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    )
    {
        Text(
            text = "Name",
            modifier = Modifier
                .width(100.dp)
                .padding(16.dp)
        )
        TextField(
            value = name,
            onValueChange = { name = it },
        )

    }
    Button(
        onClick = { onNameChange(name) },
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text("Update Name")
    }
}



@Preview
@Composable
fun PreviewProfileScreen() {
    val context = LocalContext.current
    val viewModel = MainViewModel(context)
    val navController = rememberNavController()
    ProfileScreen(viewModel = viewModel, navController = navController, context = context)
}

