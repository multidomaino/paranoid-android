package com.example.composetutorial

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.border
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Arrangement
// import androidx.compose.foundation.layout.FlowRowScopeInstance.align
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.activity.viewModels
import coil.compose.rememberImagePainter
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.composetutorial.screens.ProfileScreen
import com.example.composetutorial.screens.SettingsScreen
import android.Manifest

@Composable
fun Conversation(messages: List<Message>, profilePicture: String, userName: String) {
    LazyColumn {
        items(messages) { message ->
            MessageCard(msg = message, profilePicture = profilePicture, userName = userName)
        }
    }
}

@Preview
@Composable
fun PreviewConversation() {
    ComposeTutorialTheme {

        val context = LocalContext.current
        val mainViewModel = remember { MainViewModel(context) }
        val userProfilePicture by mainViewModel.userProfilePicture.collectAsState(initial = "defaultUrl")
        val userName by mainViewModel.userName.collectAsState(initial = "Chun-Li")

        Conversation(SampleData.conversationSample, profilePicture = userProfilePicture, userName = userName)
    }
}



class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }

        setContent {
            ComposeTutorialTheme {
                // create a NavController by calling rememberNavController
                val navController = rememberNavController()


                val userProfilePicture by mainViewModel.userProfilePicture.collectAsState(initial = "defaultUrl")
                val userName by mainViewModel.userName.collectAsState(initial = "Default Name")

                // set up navHost
                NavHost(navController = navController, startDestination = "conversation")
                {
                    composable("conversation") {
                        Conversation(SampleData.conversationSample, profilePicture = userProfilePicture, userName = userName)
                    }
                    composable("settings") {
                        SettingsScreen(navController)
                    }
                    composable("profile") {
                        val context = LocalContext.current
                        ProfileScreen(viewModel = mainViewModel, navController = navController, context = context)
                    }
                }

                // create the button that takes you to the magic screen..
                val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

                // make it so that the other button disappears when in the "SettingsScreen" or "ProfileScreen"
                if (currentDestination == "conversation") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                    Button(
                        onClick = { navController.navigate("settings") },
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text("Press me...")
                    }

                    Button(
                        onClick = { navController.navigate("profile") },
                        modifier = Modifier
                        .padding(16.dp)
                    ) {
                        Text("Profile")
                    }
                    }
                }
                }
            }

        startService(Intent(this, SensorService::class.java))

    }
}

data class Message(val body: String)

@Composable
fun MessageCard(msg: Message, profilePicture: String, userName: String) {
    Row(modifier = Modifier.padding(all = 8.dp)){
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = profilePicture).apply(block = fun ImageRequest.Builder.() {
                    crossfade(true)
                    placeholder(R.drawable.chunchu)
                }).build()
            ),
            contentDescription = "Contact profile picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))


        var isExpanded by remember { mutableStateOf(false) }

        val surfaceColor by animateColorAsState(
            if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        )


        Column(modifier = Modifier.clickable { isExpanded = !isExpanded}) {
            Text(
                text = userName,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = surfaceColor,
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
                ) {

                Text(
                    text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}



@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)

@Preview
@Composable
fun PreviewMessageCard() {
    ComposeTutorialTheme {
        val context = LocalContext.current

        val mainViewModel = remember { MainViewModel(context) }

        val userProfilePicture by mainViewModel.userProfilePicture.collectAsState(initial = "defaultUrl")
        val userName by mainViewModel.userName.collectAsState(initial = "Default Name")

        Surface {
            MessageCard(msg = Message("Hey, take a look at Jetpack Compose, it's great!"), profilePicture = userProfilePicture, userName = userName)
        }
    }
}
