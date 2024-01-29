package com.example.composetutorial.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.res.fontResource
import com.example.composetutorial.ui.theme.Typography
import com.example.composetutorial.R
import androidx.compose.material3.Button
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.core.content.res.ResourcesCompat

// import androidx.compose.material3.fontFamily // unresolved reference here for fontFamily



@Composable
fun SettingsScreen(navController: NavController? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        navController?.let { BackButton(navController = it) }
        TopText()
        PorkyImage()
    }
}


@Composable
fun TopText() {
    Text(
        text = "That's all folks!",
        textAlign = TextAlign.Center,
        fontFamily = FontFamily.Cursive,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp, bottom = 10.dp),
        fontWeight = FontWeight.ExtraBold,
        fontSize = 50.sp
    )
}


@Composable
fun PorkyImage() {
    Image(
        painter = painterResource(id = R.drawable.porky),
        contentDescription = "Porky the Pig",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .height(300.dp)
    )
}

@Composable
fun BackButton(navController: NavController) {
    Button(
        onClick = { navController.popBackStack()},
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text("Back")
    }
}


@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}